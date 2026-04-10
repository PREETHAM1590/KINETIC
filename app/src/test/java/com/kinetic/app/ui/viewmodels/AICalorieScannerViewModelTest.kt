package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class AICalorieScannerViewModelTest {

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
    fun initialState_isNotLoading() = runTest {
        val viewModel = AICalorieScannerViewModel()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun initialState_hasNoFoodResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        assertThat(viewModel.uiState.value.foodResult).isNull()
    }

    @Test
    fun initialState_hasNoTreadmillResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        assertThat(viewModel.uiState.value.treadmillResult).isNull()
    }

    @Test
    fun initialState_scanModeIsFood() = runTest {
        val viewModel = AICalorieScannerViewModel()
        assertThat(viewModel.uiState.value.scanMode).isEqualTo(ScanMode.FOOD)
    }

    @Test
    fun startScan_setsIsLoadingTrue() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        assertThat(viewModel.uiState.value.isLoading).isTrue()
    }

    @Test
    fun startScan_clearsPreviousError() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        assertThat(viewModel.uiState.value.error).isNull()
    }

    @Test
    fun startScan_afterDelay_isLoadingFalse() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun startScan_afterDelay_foodModeHasFoodResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.setScanMode(ScanMode.FOOD)
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.foodResult).isNotNull()
    }

    @Test
    fun startScan_afterDelay_treadmillModeHasTreadmillResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.setScanMode(ScanMode.TREADMILL)
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.treadmillResult).isNotNull()
    }

    @Test
    fun setScanMode_changesModeToTreadmill() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.setScanMode(ScanMode.TREADMILL)
        assertThat(viewModel.uiState.value.scanMode).isEqualTo(ScanMode.TREADMILL)
    }

    @Test
    fun reset_afterScan_clearsResults() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        viewModel.reset()
        assertThat(viewModel.uiState.value.foodResult).isNull()
        assertThat(viewModel.uiState.value.treadmillResult).isNull()
    }

    @Test
    fun reset_preservesScanMode() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.setScanMode(ScanMode.TREADMILL)
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        viewModel.reset()
        assertThat(viewModel.uiState.value.scanMode).isEqualTo(ScanMode.TREADMILL)
    }

    @Test
    fun logMeal_setsIsLoggedTrue() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.logMeal()
        assertThat(viewModel.uiState.value.isLogged).isTrue()
    }

    @Test
    fun logTreadmill_setsIsLoggedTrue() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.logTreadmill()
        assertThat(viewModel.uiState.value.isLogged).isTrue()
    }
}
