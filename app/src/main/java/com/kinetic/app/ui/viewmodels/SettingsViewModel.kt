package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.AuthResult
import com.kinetic.app.data.repository.AuthRepository
import com.kinetic.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = true,
    val useMetricUnits: Boolean = false,
    val soundEffects: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load settings"
                    )
                }
                .collect { settings ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notificationsEnabled = settings.notificationsEnabled,
                        darkMode = settings.darkMode,
                        useMetricUnits = settings.useMetricUnits,
                        soundEffects = settings.soundEffects,
                        error = null
                    )
                }
        }
    }

    fun toggleNotifications() {
        val current = _uiState.value
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(!current.notificationsEnabled)
        }
    }

    fun toggleDarkMode() {
        val current = _uiState.value
        viewModelScope.launch {
            settingsRepository.setDarkMode(!current.darkMode)
        }
    }

    fun toggleMetricUnits() {
        val current = _uiState.value
        viewModelScope.launch {
            settingsRepository.setUseMetricUnits(!current.useMetricUnits)
        }
    }

    fun toggleSoundEffects() {
        val current = _uiState.value
        viewModelScope.launch {
            settingsRepository.setSoundEffects(!current.soundEffects)
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }

    fun deleteAllData(onComplete: () -> Unit = {}) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            try {
                if (authRepository.isSignedIn()) {
                    when (val result = authRepository.deleteAccount()) {
                        is AuthResult.Success -> Unit
                        is AuthResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                error = result.message
                            )
                            return@launch
                        }
                    }
                }
                settingsRepository.clearAllData()
                _uiState.value = _uiState.value.copy(isSaving = false)
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to delete data"
                )
            }
        }
    }
}
