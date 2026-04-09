package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val currentPage: Int = 0,
    val onboardingCompleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = settingsRepository.isOnboardingCompleted().first()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                onboardingCompleted = completed
            )
        }
    }

    fun nextPage() {
        val current = _uiState.value
        _uiState.value = current.copy(currentPage = current.currentPage + 1)
    }

    fun previousPage() {
        val current = _uiState.value
        if (current.currentPage > 0) {
            _uiState.value = current.copy(currentPage = current.currentPage - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
            _uiState.value = _uiState.value.copy(onboardingCompleted = true)
        }
    }
}
