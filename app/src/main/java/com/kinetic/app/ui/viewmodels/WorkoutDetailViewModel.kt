package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.ExerciseItem
import com.kinetic.app.data.models.WorkoutItem
import com.kinetic.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutDetailUiState(
    val isLoading: Boolean = true,
    val workout: WorkoutItem? = null,
    val exercises: List<ExerciseItem> = emptyList(),
    val error: String? = null
)

sealed class WorkoutDetailNavigationEvent {
    data class NavigateToActiveWorkout(val workoutId: String) : WorkoutDetailNavigationEvent()
}

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<WorkoutDetailNavigationEvent>()
    val navigationEvent: SharedFlow<WorkoutDetailNavigationEvent> = _navigationEvent.asSharedFlow()

    fun loadWorkout(workoutId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(workoutId).first()
            if (workout == null) {
                _uiState.value = WorkoutDetailUiState(
                    isLoading = false,
                    error = "Workout not found"
                )
                return@launch
            }

            val exercises = workoutRepository.getExercisesForWorkout(workout.id).first()
            _uiState.value = WorkoutDetailUiState(
                isLoading = false,
                workout = workout,
                exercises = exercises
            )
        }
    }

    fun loadWorkoutByTitle(title: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutByTitle(title).first()
            if (workout == null) {
                _uiState.value = WorkoutDetailUiState(
                    isLoading = false,
                    error = "Workout not found"
                )
                return@launch
            }

            val exercises = workoutRepository.getExercisesForWorkout(workout.id).first()
            _uiState.value = WorkoutDetailUiState(
                isLoading = false,
                workout = workout,
                exercises = exercises
            )
        }
    }

    fun startWorkout() {
        val workoutId = _uiState.value.workout?.id ?: return
        viewModelScope.launch {
            _navigationEvent.emit(WorkoutDetailNavigationEvent.NavigateToActiveWorkout(workoutId))
        }
    }
}