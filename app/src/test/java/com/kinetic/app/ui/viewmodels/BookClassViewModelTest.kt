package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeMembershipRepository
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
class BookClassViewModelTest {

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
    fun loadData_populatesClasses() = runTest {
        val viewModel = BookClassViewModel(FakeMembershipRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.classes).isNotEmpty()
        assertThat(viewModel.categories).contains("Yoga")
    }

    @Test
    fun setCategory_filtersClassesAndUpdatesSelection() = runTest {
        val viewModel = BookClassViewModel(FakeMembershipRepository())

        advanceUntilIdle()
        viewModel.setCategory("Yoga")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.selectedCategory).isEqualTo("Yoga")
        assertThat(state.classes).isNotEmpty()
        assertThat(state.classes.all { it.category.equals("Yoga", ignoreCase = true) }).isTrue()
    }

    @Test
    fun setCategory_rapidChanges_keepsLatestCategoryResults() = runTest {
        val viewModel = BookClassViewModel(FakeMembershipRepository())

        advanceUntilIdle()
        viewModel.setCategory("Yoga")
        viewModel.setCategory("HIIT")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.selectedCategory).isEqualTo("HIIT")
        assertThat(state.classes).isNotEmpty()
        assertThat(state.classes.all { it.category.equals("HIIT", ignoreCase = true) }).isTrue()
    }
}