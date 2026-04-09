package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeDietRepository
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
class DietViewModelTest {

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
    fun loadData_populatesMealsAndNutrition() = runTest {
        val viewModel = DietViewModel(FakeDietRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.meals).isNotEmpty()
        assertThat(state.dailyNutrition).isNotNull()
    }

    @Test
    fun meals_matchTotalsShape() = runTest {
        val viewModel = DietViewModel(FakeDietRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        val nutrition = checkNotNull(state.dailyNutrition)
        assertThat(state.meals.size).isEqualTo(nutrition.meals.size)
        assertThat(nutrition.totalCalories).isGreaterThan(0)
    }
}
