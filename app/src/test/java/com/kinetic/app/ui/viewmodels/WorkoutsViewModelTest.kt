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
class WorkoutsViewModelTest {

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
    fun loadData_populatesWorkoutsAndHiit() = runTest {
        val viewModel = WorkoutsViewModel(FakeWorkoutRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.workouts).isNotEmpty()
        assertThat(state.hiitWorkouts).isNotEmpty()
    }

    @Test
    fun setFilter_updatesFilterAndResults() = runTest {
        val viewModel = WorkoutsViewModel(FakeWorkoutRepository())

        advanceUntilIdle()
        viewModel.setFilter("Strength")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.currentFilter).isEqualTo("Strength")
        assertThat(state.workouts).isNotEmpty()
        assertThat(state.workouts.all { it.category.equals("Strength", ignoreCase = true) }).isTrue()
    }
}
