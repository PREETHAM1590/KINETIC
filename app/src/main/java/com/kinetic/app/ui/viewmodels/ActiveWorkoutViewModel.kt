package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.ActiveWorkout
import com.kinetic.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveWorkoutState(
    val workout: ActiveWorkout,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val completedSets: Map<String, Int> = emptyMap(),
    val timeLeft: Float = 0f,
    val totalTime: Float = 60f,
    val isRunning: Boolean = false,
    val isResting: Boolean = false,
    val restTimeLeft: Int = 0,
    val reps: Int = 10,
    val caloriesBurned: Int = 0,
    val elapsedSeconds: Int = 0
)

sealed class ActiveWorkoutUiState {
    object Loading : ActiveWorkoutUiState()
    data class Success(val data: ActiveWorkoutState) : ActiveWorkoutUiState()
    data class Error(val message: String) : ActiveWorkoutUiState()
}

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null

    init {
        loadWorkout()
    }

    private fun buildInitialState(workout: ActiveWorkout): ActiveWorkoutState {
        val firstExercise = workout.exercises.first()
        return ActiveWorkoutState(
            workout = workout,
            currentExerciseIndex = 0,
            currentSet = 1,
            timeLeft = firstExercise.reps.toFloat(),
            totalTime = firstExercise.reps.toFloat(),
            reps = firstExercise.reps,
            completedSets = workout.exercises.associate { it.id to 0 }
        )
    }

    private fun loadWorkout(workoutId: String = "aw1") {
        _uiState.value = ActiveWorkoutUiState.Loading
        viewModelScope.launch {
            val workout = workoutRepository.getActiveWorkoutById(workoutId).first()
            if (workout == null || workout.exercises.isEmpty()) {
                _uiState.value = ActiveWorkoutUiState.Error("Active workout not found")
            } else {
                _uiState.value = ActiveWorkoutUiState.Success(buildInitialState(workout))
            }
        }
    }

    fun startTimer() {
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        if (state.isRunning) return
        _uiState.value = ActiveWorkoutUiState.Success(state.copy(isRunning = true))
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: break
                if (!current.isRunning) break
                val newTimeLeft = current.timeLeft - 1f
                val newElapsed = current.elapsedSeconds + 1
                val newCalories = (newElapsed * current.workout.caloriesPerMin) / 60
                if (newTimeLeft <= 0f) {
                    _uiState.value = ActiveWorkoutUiState.Success(
                        current.copy(
                            timeLeft = 0f,
                            isRunning = false,
                            elapsedSeconds = newElapsed,
                            caloriesBurned = newCalories
                        )
                    )
                    break
                }
                _uiState.value = ActiveWorkoutUiState.Success(
                    current.copy(
                        timeLeft = newTimeLeft,
                        elapsedSeconds = newElapsed,
                        caloriesBurned = newCalories
                    )
                )
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        _uiState.value = ActiveWorkoutUiState.Success(state.copy(isRunning = false))
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        _uiState.value = ActiveWorkoutUiState.Success(
            state.copy(isRunning = false, timeLeft = 0f)
        )
    }

    fun completeSet() {
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        val currentExercise = state.workout.exercises[state.currentExerciseIndex]
        val newCompletedSets = state.completedSets.toMutableMap().apply {
            this[currentExercise.id] = (this[currentExercise.id] ?: 0) + 1
        }
        val nextSet = state.currentSet + 1
        if (nextSet > currentExercise.sets) {
            completeExercise()
        } else {
            _uiState.value = ActiveWorkoutUiState.Success(
                state.copy(
                    currentSet = nextSet,
                    completedSets = newCompletedSets
                )
            )
            startRest(currentExercise.restSeconds)
        }
    }

    fun completeExercise() {
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        val nextIndex = state.currentExerciseIndex + 1
        if (nextIndex >= state.workout.exercises.size) {
            stopTimer()
            return
        }
        val nextExercise = state.workout.exercises[nextIndex]
        val newCompletedSets = state.completedSets.toMutableMap().apply {
            this[state.workout.exercises[state.currentExerciseIndex].id] =
                state.workout.exercises[state.currentExerciseIndex].sets
        }
        timerJob?.cancel()
        _uiState.value = ActiveWorkoutUiState.Success(
            state.copy(
                currentExerciseIndex = nextIndex,
                currentSet = 1,
                timeLeft = nextExercise.reps.toFloat(),
                totalTime = nextExercise.reps.toFloat(),
                reps = nextExercise.reps,
                isRunning = false,
                completedSets = newCompletedSets
            )
        )
    }

    fun startRest(restSeconds: Int) {
        restTimerJob?.cancel()
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        _uiState.value = ActiveWorkoutUiState.Success(
            state.copy(isResting = true, restTimeLeft = restSeconds)
        )
        restTimerJob = viewModelScope.launch {
            var remaining = restSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                val current = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: break
                _uiState.value = ActiveWorkoutUiState.Success(
                    current.copy(restTimeLeft = remaining)
                )
            }
            val current = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return@launch
            _uiState.value = ActiveWorkoutUiState.Success(
                current.copy(isResting = false, restTimeLeft = 0)
            )
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        restTimerJob = null
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        _uiState.value = ActiveWorkoutUiState.Success(
            state.copy(isResting = false, restTimeLeft = 0)
        )
    }

    fun updateReps(reps: Int) {
        val state = (_uiState.value as? ActiveWorkoutUiState.Success)?.data ?: return
        _uiState.value = ActiveWorkoutUiState.Success(state.copy(reps = reps))
    }

    fun loadWorkoutById(workoutId: String) {
        timerJob?.cancel()
        restTimerJob?.cancel()
        loadWorkout(workoutId)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        restTimerJob?.cancel()
    }
}