package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.repository.FakeDietRepository
import com.kinetic.app.data.store.UserActivityStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DietViewModelTest {

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
    fun loadData_populatesMealsAndNutrition() = runTest {
        val viewModel = DietViewModel(
            FakeDietRepository(),
            makeStore()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.meals).isNotEmpty()
        assertThat(state.dailyNutrition).isNotNull()
    }

    @Test
    fun meals_matchTotalsShape() = runTest {
        val viewModel = DietViewModel(
            FakeDietRepository(),
            makeStore()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        val nutrition = checkNotNull(state.dailyNutrition)
        assertThat(state.meals.size).isEqualTo(nutrition.meals.size)
        assertThat(nutrition.totalCalories).isGreaterThan(0)
    }

    @Test
    fun `logMeal records calories in UserActivityStore`() = runTest {
        val store = makeStore()
        val vm = DietViewModel(dietRepository = FakeDietRepository(), activityStore = store)
        advanceUntilIdle()

        vm.logMeal(calories = 650)

        assertEquals(650, store.state.value.todayCaloriesConsumed)
    }
}
