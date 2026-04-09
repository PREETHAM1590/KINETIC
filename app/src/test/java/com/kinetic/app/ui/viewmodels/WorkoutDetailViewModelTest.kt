package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeWorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutDetailViewModelTest {

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
    fun loadWorkout_withValidId_populatesWorkoutAndExercises() = runTest {
        val viewModel = WorkoutDetailViewModel(FakeWorkoutRepository())

        viewModel.loadWorkout("w1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.workout).isNotNull()
        assertThat(state.workout!!.id).isEqualTo("w1")
        assertThat(state.exercises).isNotEmpty()
    }

    @Test
    fun loadWorkout_withUnknownId_setsError() = runTest {
        val viewModel = WorkoutDetailViewModel(FakeWorkoutRepository())

        viewModel.loadWorkout("missing")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.error).isEqualTo("Workout not found")
    }

    @Test
    fun startWorkout_emitsNavigationEvent() = runTest {
        val viewModel = WorkoutDetailViewModel(FakeWorkoutRepository())

        viewModel.loadWorkout("w1")
        advanceUntilIdle()

        val eventDeferred = async {
            withTimeout(1000) {
                viewModel.navigationEvent.first()
            }
        }

        viewModel.startWorkout()
        val event = eventDeferred.await() as WorkoutDetailNavigationEvent.NavigateToActiveWorkout
        assertThat(event.workoutId).isEqualTo("w1")
    }
}
