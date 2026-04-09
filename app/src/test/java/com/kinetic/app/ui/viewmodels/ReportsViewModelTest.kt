package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeReportsRepository
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
class ReportsViewModelTest {

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
    fun loadData_populatesReportsRecordsAndChart() = runTest {
        val viewModel = ReportsViewModel(FakeReportsRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.reports).isNotEmpty()
        assertThat(state.personalRecords).isNotEmpty()
        assertThat(state.chartData).isNotEmpty()
    }

    @Test
    fun chartData_valuesAreInExpectedRange() = runTest {
        val viewModel = ReportsViewModel(FakeReportsRepository())

        advanceUntilIdle()

        val chartValues = viewModel.uiState.value.chartData.map { it.value }
        assertThat(chartValues.all { it in 0f..1f }).isTrue()
    }
}
