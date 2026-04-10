package com.kinetic.app.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.FoodScanResult
import com.kinetic.app.data.models.ScannedFood
import com.kinetic.app.data.models.ScanSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ScanMode { FOOD, TREADMILL }

data class TreadmillScanResult(
    val speedKmh: Float = 6.5f,
    val inclinePct: Float = 2f,
    val durationMinutes: Int = 30,
    val estimatedCalories: Int = 0,
    val estimatedSteps: Int = 0
)

data class AICalorieScannerUiState(
    val scanMode: ScanMode = ScanMode.FOOD,
    val isLoading: Boolean = false,
    val imageUri: Uri? = null,
    val foodResult: FoodScanResult? = null,
    val treadmillResult: TreadmillScanResult? = null,
    val error: String? = null,
    val isLogged: Boolean = false
)

@HiltViewModel
class AICalorieScannerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AICalorieScannerUiState())
    val uiState: StateFlow<AICalorieScannerUiState> = _uiState.asStateFlow()

    fun setScanMode(mode: ScanMode) {
        _uiState.update {
            AICalorieScannerUiState(scanMode = mode)
        }
    }

    fun setImage(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, foodResult = null, treadmillResult = null, error = null) }
    }

    fun startScan() {
        _uiState.update { it.copy(isLoading = true, error = null, foodResult = null, treadmillResult = null) }
        viewModelScope.launch {
            try {
                delay(2000L)
                if (_uiState.value.scanMode == ScanMode.FOOD) {
                    val fakeResult = FoodScanResult(
                        foods = listOf(
                            ScannedFood("Dal Makhani", 0.91f, 200f, 131f, 7f, 17f, 4.5f),
                            ScannedFood("Basmati Rice", 0.88f, 150f, 130f, 2.7f, 28f, 0.3f),
                            ScannedFood("Paneer Tikka", 0.85f, 100f, 265f, 19f, 5f, 18f)
                        ),
                        rawImageDescription = "Indian meal with dal, rice and paneer",
                        source = ScanSource.GEMINI_VISION
                    )
                    _uiState.update { it.copy(isLoading = false, foodResult = fakeResult) }
                } else {
                    val speedKmh = 6.5f
                    val inclinePct = 2f
                    val durationMinutes = 30
                    val estimatedCalories = (durationMinutes * speedKmh * 0.65f).toInt()
                    val estimatedSteps = (durationMinutes.toFloat() * speedKmh * 21.67f).toInt()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            treadmillResult = TreadmillScanResult(
                                speedKmh = speedKmh,
                                inclinePct = inclinePct,
                                durationMinutes = durationMinutes,
                                estimatedCalories = estimatedCalories,
                                estimatedSteps = estimatedSteps
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Scan failed") }
            }
        }
    }

    fun logMeal() {
        _uiState.update { it.copy(isLogged = true) }
    }

    fun scanTreadmill() {
        _uiState.update { it.copy(isLoading = true, error = null, treadmillResult = null) }
        viewModelScope.launch {
            try {
                delay(1500L)
                val speedKmh = 6.5f
                val inclinePct = 2f
                val durationMinutes = 30
                val estimatedCalories = (durationMinutes * speedKmh * 0.65f).toInt()
                val estimatedSteps = (durationMinutes.toFloat() * speedKmh * 21.67f).toInt()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        treadmillResult = TreadmillScanResult(
                            speedKmh = speedKmh,
                            inclinePct = inclinePct,
                            durationMinutes = durationMinutes,
                            estimatedCalories = estimatedCalories,
                            estimatedSteps = estimatedSteps
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Scan failed") }
            }
        }
    }

    fun logTreadmill() {
        _uiState.update { it.copy(isLogged = true) }
    }

    fun reset() {
        _uiState.update { AICalorieScannerUiState(scanMode = it.scanMode) }
    }
}
