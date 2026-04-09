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
class MealDetailViewModelTest {

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
    fun loadMeal_withValidId_populatesMeal() = runTest {
        val viewModel = MealDetailViewModel(FakeDietRepository())

        viewModel.loadMeal("m1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.meal).isNotNull()
        assertThat(state.meal!!.id).isEqualTo("m1")
    }

    @Test
    fun loadMeal_withUnknownId_setsError() = runTest {
        val viewModel = MealDetailViewModel(FakeDietRepository())

        viewModel.loadMeal("missing")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.error).isEqualTo("Meal not found")
    }

    @Test
    fun logMeal_setsConfirmationState() = runTest {
        val viewModel = MealDetailViewModel(FakeDietRepository())

        viewModel.loadMeal("m1")
        advanceUntilIdle()
        viewModel.logMeal()

        val state = viewModel.uiState.value
        assertThat(state.isMealLogged).isTrue()
        assertThat(state.showLoggedConfirmation).isTrue()
    }
}
