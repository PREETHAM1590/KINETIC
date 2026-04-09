package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsentUiState(
    val accepted: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ConsentViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState.asStateFlow()

    fun setAccepted(value: Boolean) {
        _uiState.value = _uiState.value.copy(accepted = value, error = null)
    }

    fun saveConsent(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.accepted) {
            _uiState.value = state.copy(error = "You must accept the privacy consent to continue")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)
        viewModelScope.launch {
            settingsRepository.setConsentGiven(true, UserPreferences.CURRENT_CONSENT_VERSION)
            _uiState.value = _uiState.value.copy(isSaving = false)
            onSuccess()
        }
    }
}
