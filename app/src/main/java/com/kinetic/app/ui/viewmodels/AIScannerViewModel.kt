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

data class AIScannerUiState(
    val isLoading: Boolean = false,
    val imageUri: Uri? = null,
    val result: FoodScanResult? = null,
    val error: String? = null,
    val isLogged: Boolean = false
)

@HiltViewModel
class AIScannerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AIScannerUiState())
    val uiState: StateFlow<AIScannerUiState> = _uiState.asStateFlow()

    fun setImage(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, result = null, error = null) }
        analyzeImage(uri)
    }

    fun startScan() {
        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        viewModelScope.launch {
            try {
                delay(2000L)
                val fakeResult = FoodScanResult(
                    foods = listOf(
                        ScannedFood("Dal Makhani", 0.91f, 200f, 131f, 7f, 17f, 4.5f),
                        ScannedFood("Basmati Rice", 0.88f, 150f, 130f, 2.7f, 28f, 0.3f)
                    ),
                    rawImageDescription = "Indian meal with dal and rice"
                )
                _uiState.update { it.copy(isLoading = false, result = fakeResult) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Scan failed") }
            }
        }
    }

    private fun analyzeImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                delay(1500L)
                val fakeResult = FoodScanResult(
                    foods = listOf(
                        ScannedFood("Grilled Chicken", 0.94f, 150f, 165f, 31f, 0f, 3.6f),
                        ScannedFood("Brown Rice", 0.89f, 100f, 112f, 2.6f, 24f, 0.9f)
                    ),
                    rawImageDescription = "Healthy protein meal",
                    source = ScanSource.GEMINI_VISION
                )
                _uiState.update { it.copy(isLoading = false, result = fakeResult) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Analysis failed") }
            }
        }
    }

    fun logMeal() {
        _uiState.update { it.copy(isLogged = true) }
    }

    fun reset() {
        _uiState.update { AIScannerUiState() }
    }
}
