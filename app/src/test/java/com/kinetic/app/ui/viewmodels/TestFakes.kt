package com.kinetic.app.ui.viewmodels

import com.google.firebase.auth.FirebaseUser
import com.kinetic.app.data.models.AuthResult
import com.kinetic.app.data.models.SettingsData
import com.kinetic.app.data.repository.AuthRepository
import com.kinetic.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class InMemorySettingsRepository(
    initialSettings: SettingsData = SettingsData(),
    onboardingCompleted: Boolean = false,
    consentGiven: Boolean = false
) : SettingsRepository {

    private val settings = MutableStateFlow(initialSettings)
    private val onboarding = MutableStateFlow(onboardingCompleted)
    private val consent = MutableStateFlow(consentGiven)

    override fun getSettings(): Flow<SettingsData> = settings.asStateFlow()

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(notificationsEnabled = enabled)
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        settings.value = settings.value.copy(darkMode = enabled)
    }

    override suspend fun setUseMetricUnits(enabled: Boolean) {
        settings.value = settings.value.copy(useMetricUnits = enabled)
    }

    override suspend fun setSoundEffects(enabled: Boolean) {
        settings.value = settings.value.copy(soundEffects = enabled)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboarding.value = completed
    }

    override suspend fun setConsentGiven(given: Boolean, version: Int) {
        consent.value = given
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = onboarding.asStateFlow()

    override fun isConsentGiven(): Flow<Boolean> = consent.asStateFlow()

    override suspend fun clearAllData() {
        settings.value = SettingsData()
        onboarding.value = false
        consent.value = false
    }
}

class InMemoryAuthRepository(
    private var signedIn: Boolean = false,
    var deleteResult: AuthResult = AuthResult.Success("test-user"),
    var eraseResult: AuthResult = AuthResult.Success("test-user")
) : AuthRepository {

    override val currentUser: Flow<FirebaseUser?> = flowOf(null)

    override suspend fun signIn(email: String, password: String): AuthResult {
        signedIn = true
        return AuthResult.Success("test-user")
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        signedIn = true
        return AuthResult.Success("test-user")
    }

    override suspend fun signOut() {
        signedIn = false
    }

    override suspend fun deleteAccount(): AuthResult {
        if (deleteResult is AuthResult.Success) {
            signedIn = false
        }
        return deleteResult
    }

    override suspend fun eraseUserData(): AuthResult = eraseResult

    override fun isSignedIn(): Boolean = signedIn
}
