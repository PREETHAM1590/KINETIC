package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.models.ActiveWorkout
import com.kinetic.app.data.models.ExerciseItem
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem
import com.kinetic.app.data.repository.FakeWorkoutRepository
import com.kinetic.app.data.repository.WorkoutRepository
import com.kinetic.app.data.store.UserActivityStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockPrefs: UserPreferences = mock()

    private fun makeStore(): UserActivityStore {
        whenever(mockPrefs.targetCalories).thenReturn(flowOf(2500))
        return UserActivityStore(mockPrefs)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsDefaultActiveWorkout() = runTest {
        val viewModel = ActiveWorkoutViewModel(
            FakeWorkoutRepository(),
            makeStore()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.workout.id).isEqualTo("aw1")
        assertThat(state.data.currentExerciseIndex).isEqualTo(0)
    }

    @Test
    fun startTimer_marksWorkoutAsRunning() = runTest {
        val viewModel = ActiveWorkoutViewModel(
            FakeWorkoutRepository(),
            makeStore()
        )

        advanceUntilIdle()
        viewModel.startTimer()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.isRunning).isTrue()
    }

    @Test
    fun completeSet_startsRestWithoutDroppingRestState() = runTest {
        val viewModel = ActiveWorkoutViewModel(
            FakeWorkoutRepository(),
            makeStore()
        )

        advanceUntilIdle()
        viewModel.completeSet()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.currentSet).isEqualTo(2)
        assertThat(state.data.completedSets["e1"]).isEqualTo(1)
        assertThat(state.data.isResting).isTrue()
        assertThat(state.data.restTimeLeft).isEqualTo(90)
    }

    @Test
    fun loadWorkoutById_switchesWorkout() = runTest {
        val viewModel = ActiveWorkoutViewModel(
            FakeWorkoutRepository(),
            makeStore()
        )

        advanceUntilIdle()
        viewModel.loadWorkoutById("aw2")
        advanceUntilIdle()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.workout.id).isEqualTo("aw2")
    }

    @Test
    fun completeExercise_onLastExercise_recordsWorkoutCompleted() = runTest {
        val singleExerciseWorkout = ActiveWorkout(
            id = "aw-single",
            title = "Single Exercise Test",
            difficulty = "Easy",
            durationMin = 10,
            caloriesPerMin = 60,
            exercises = listOf(ExerciseItem("ex1", "Squat", sets = 1, reps = 10, weightLbs = 50))
        )
        val singleExerciseRepo = object : WorkoutRepository {
            override fun getWorkouts(): Flow<List<WorkoutItem>> = flow { emit(emptyList()) }
            override fun getHiitWorkouts(): Flow<List<HiitItem>> = flow { emit(emptyList()) }
            override fun getWorkoutById(id: String): Flow<WorkoutItem?> = flow { emit(null) }
            override fun getWorkoutByTitle(title: String): Flow<WorkoutItem?> = flow { emit(null) }
            override fun getExercisesForWorkout(workoutId: String): Flow<List<ExerciseItem>> = flow { emit(emptyList()) }
            override fun getActiveWorkoutById(id: String): Flow<ActiveWorkout?> = flow { emit(singleExerciseWorkout) }
            override fun getFilteredWorkouts(category: String): Flow<List<WorkoutItem>> = flow { emit(emptyList()) }
        }
        val store = makeStore()
        val vm = ActiveWorkoutViewModel(
            workoutRepository = singleExerciseRepo,
            userActivityStore = store
        )

        advanceUntilIdle()
        vm.startTimer()
        advanceTimeBy(1_001L) // advance 1 second so caloriesBurned = (1 * 60) / 60 = 1
        advanceUntilIdle()

        vm.completeExercise()

        assertThat(store.state.value.todayCaloriesBurned).isGreaterThan(0)
    }
}
