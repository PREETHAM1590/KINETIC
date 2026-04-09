package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.fake.FakeDietData
import com.kinetic.app.data.models.MealItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScanResult(
    val meal: MealItem,
    val confidence: Float
)

sealed class AICalorieScannerUiState {
    object Loading : AICalorieScannerUiState()
    data class Success(
        val scanState: ScanState = ScanState.Idle,
        val scanResult: ScanResult? = null,
        val hasCameraPermission: Boolean = false
    ) : AICalorieScannerUiState()
    data class Error(val message: String) : AICalorieScannerUiState()
}

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    object Result : ScanState()
    data class Error(val message: String) : ScanState()
}

class AICalorieScannerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AICalorieScannerUiState>(
        AICalorieScannerUiState.Success()
    )
    val uiState: StateFlow<AICalorieScannerUiState> = _uiState.asStateFlow()

    fun requestCameraPermission() {
        val current = _uiState.value
        if (current is AICalorieScannerUiState.Success) {
            _uiState.value = current.copy(hasCameraPermission = true)
        }
    }

    fun startScan() {
        val current = _uiState.value
        if (current !is AICalorieScannerUiState.Success) return

        _uiState.value = current.copy(scanState = ScanState.Scanning, scanResult = null)

        viewModelScope.launch {
            delay(3000)
            val fakeMeal = FakeDietData.todayMeals.getOrNull(1) ?: FakeDietData.todayMeals.first()
            val result = ScanResult(meal = fakeMeal, confidence = 0.92f)
            val state = _uiState.value
            if (state is AICalorieScannerUiState.Success) {
                _uiState.value = state.copy(scanState = ScanState.Result, scanResult = result)
            }
        }
    }

    fun resetScan() {
        val current = _uiState.value
        if (current is AICalorieScannerUiState.Success) {
            _uiState.value = current.copy(scanState = ScanState.Idle, scanResult = null)
        }
    }
}
