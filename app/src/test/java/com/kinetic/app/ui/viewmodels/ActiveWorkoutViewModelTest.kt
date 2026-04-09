package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeWorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveWorkoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

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
        val viewModel = ActiveWorkoutViewModel(FakeWorkoutRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.workout.id).isEqualTo("aw1")
        assertThat(state.data.currentExerciseIndex).isEqualTo(0)
    }

    @Test
    fun startTimer_marksWorkoutAsRunning() = runTest {
        val viewModel = ActiveWorkoutViewModel(FakeWorkoutRepository())

        advanceUntilIdle()
        viewModel.startTimer()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.isRunning).isTrue()
    }

    @Test
    fun completeSet_startsRestWithoutDroppingRestState() = runTest {
        val viewModel = ActiveWorkoutViewModel(FakeWorkoutRepository())

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
        val viewModel = ActiveWorkoutViewModel(FakeWorkoutRepository())

        advanceUntilIdle()
        viewModel.loadWorkoutById("aw2")
        advanceUntilIdle()

        val state = viewModel.uiState.value as ActiveWorkoutUiState.Success
        assertThat(state.data.workout.id).isEqualTo("aw2")
    }
}