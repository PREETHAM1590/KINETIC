package com.kinetic.app.ui.viewmodels

import app.cash.turbine.test
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
    fun initialState_isIdle() = runTest {
        val viewModel = AICalorieScannerViewModel()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanState).isInstanceOf(ScanState.Idle::class.java)
    }

    @Test
    fun initialState_hasNoScanResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult).isNull()
    }

    @Test
    fun initialState_hasNoCameraPermission() = runTest {
        val viewModel = AICalorieScannerViewModel()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.hasCameraPermission).isFalse()
    }

    @Test
    fun requestCameraPermission_setsPermissionTrue() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.requestCameraPermission()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.hasCameraPermission).isTrue()
    }

    @Test
    fun startScan_transitionsToScanning() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanState).isInstanceOf(ScanState.Scanning::class.java)
    }

    @Test
    fun startScan_clearsPreviousResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult).isNull()
    }

    @Test
    fun startScan_afterDelay_transitionsToResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanState).isInstanceOf(ScanState.Result::class.java)
    }

    @Test
    fun startScan_afterDelay_hasScanResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult).isNotNull()
    }

    @Test
    fun startScan_afterDelay_scanResultHasConfidence() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult!!.confidence).isGreaterThan(0f)
    }

    @Test
    fun startScan_afterDelay_scanResultHasMeal() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult!!.meal).isNotNull()
    }

    @Test
    fun resetScan_afterResult_returnsToIdle() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        viewModel.resetScan()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanState).isInstanceOf(ScanState.Idle::class.java)
    }

    @Test
    fun resetScan_afterResult_clearsScanResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(3000)
        advanceUntilIdle()
        viewModel.resetScan()
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanResult).isNull()
    }

    @Test
    fun startScan_beforeDelay_stillScanning() = runTest {
        val viewModel = AICalorieScannerViewModel()
        viewModel.startScan()
        advanceTimeBy(1000)
        val state = viewModel.uiState.value as AICalorieScannerUiState.Success
        assertThat(state.scanState).isInstanceOf(ScanState.Scanning::class.java)
    }

    @Test
    fun fullScanCycle_idleToScanningToResult() = runTest {
        val viewModel = AICalorieScannerViewModel()
        assertThat((viewModel.uiState.value as AICalorieScannerUiState.Success).scanState)
            .isInstanceOf(ScanState.Idle::class.java)
        viewModel.startScan()
        assertThat((viewModel.uiState.value as AICalorieScannerUiState.Success).scanState)
            .isInstanceOf(ScanState.Scanning::class.java)
        advanceTimeBy(3000)
        advanceUntilIdle()
        assertThat((viewModel.uiState.value as AICalorieScannerUiState.Success).scanState)
            .isInstanceOf(ScanState.Result::class.java)
    }
}
