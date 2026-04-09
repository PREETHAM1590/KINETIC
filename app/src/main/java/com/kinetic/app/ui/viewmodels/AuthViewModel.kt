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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null)
    }

    fun signIn(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email and password are required")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepository.signIn(state.email.trim(), state.password)) {
                is AuthResult.Success -> {
                    settingsRepository.setOnboardingCompleted(true)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email and password are required")
            return
        }
        if (state.password.length < 6) {
            _uiState.value = state.copy(error = "Password must be at least 6 characters")
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Passwords do not match")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = authRepository.signUp(state.email.trim(), state.password)) {
                is AuthResult.Success -> {
                    settingsRepository.setOnboardingCompleted(true)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
