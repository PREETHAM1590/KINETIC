package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.repository.AuthRepository
import com.kinetic.app.data.repository.SettingsRepository
import com.kinetic.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StartupUiState(
    val isLoading: Boolean = true,
    val startDestination: String = Screen.Onboarding.route
)

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val onboardingDone = settingsRepository.isOnboardingCompleted().first()
            val consentGiven = settingsRepository.isConsentGiven().first()
            val startRoute = when {
                !onboardingDone -> Screen.Onboarding.route
                !consentGiven -> Screen.Consent.route
                !authRepository.isSignedIn() -> Screen.Login.route
                else -> Screen.Workouts.route
            }
            _uiState.value = StartupUiState(isLoading = false, startDestination = startRoute)
        }
    }
}
