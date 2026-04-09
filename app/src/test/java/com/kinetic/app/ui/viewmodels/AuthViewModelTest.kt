package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseUser
import com.kinetic.app.data.models.AuthResult
import com.kinetic.app.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

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
    fun signIn_blankCredentials_setsValidationError() = runTest {
        val authRepository = RecordingAuthRepository()
        val settingsRepository = InMemorySettingsRepository()
        val viewModel = AuthViewModel(authRepository, settingsRepository)

        viewModel.signIn(onSuccess = {})

        val state = viewModel.uiState.value
        assertThat(state.error).isEqualTo("Email and password are required")
        assertThat(state.isLoading).isFalse()
        assertThat(authRepository.signInCalls).isEqualTo(0)
    }

    @Test
    fun signIn_success_trimsEmailAndCompletesOnboarding() = runTest {
        val authRepository = RecordingAuthRepository(signInResult = AuthResult.Success("user-1"))
        val settingsRepository = InMemorySettingsRepository()
        val viewModel = AuthViewModel(authRepository, settingsRepository)
        var successCalled = false

        viewModel.onEmailChange("  user@example.com  ")
        viewModel.onPasswordChange("password123")
        viewModel.signIn { successCalled = true }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(successCalled).isTrue()
        assertThat(authRepository.lastSignInEmail).isEqualTo("user@example.com")
        assertThat(settingsRepository.isOnboardingCompletedValue()).isTrue()
    }

    @Test
    fun signUp_passwordMismatch_setsValidationError() = runTest {
        val authRepository = RecordingAuthRepository()
        val settingsRepository = InMemorySettingsRepository()
        val viewModel = AuthViewModel(authRepository, settingsRepository)

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password321")
        viewModel.signUp(onSuccess = {})

        val state = viewModel.uiState.value
        assertThat(state.error).isEqualTo("Passwords do not match")
        assertThat(state.isLoading).isFalse()
        assertThat(authRepository.signUpCalls).isEqualTo(0)
    }

    @Test
    fun signUp_repositoryError_exposesMessageAndStopsLoading() = runTest {
        val authRepository = RecordingAuthRepository(signUpResult = AuthResult.Error("Email already exists"))
        val settingsRepository = InMemorySettingsRepository()
        val viewModel = AuthViewModel(authRepository, settingsRepository)
        var successCalled = false

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.signUp { successCalled = true }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo("Email already exists")
        assertThat(successCalled).isFalse()
        assertThat(settingsRepository.isOnboardingCompletedValue()).isFalse()
    }

    private class RecordingAuthRepository(
        var signInResult: AuthResult = AuthResult.Success("test-user"),
        var signUpResult: AuthResult = AuthResult.Success("test-user"),
        private var signedIn: Boolean = false
    ) : AuthRepository {

        override val currentUser: Flow<FirebaseUser?> = flowOf(null)

        var signInCalls: Int = 0
        var signUpCalls: Int = 0
        var lastSignInEmail: String? = null

        override suspend fun signIn(email: String, password: String): AuthResult {
            signInCalls += 1
            lastSignInEmail = email
            if (signInResult is AuthResult.Success) {
                signedIn = true
            }
            return signInResult
        }

        override suspend fun signUp(email: String, password: String): AuthResult {
            signUpCalls += 1
            if (signUpResult is AuthResult.Success) {
                signedIn = true
            }
            return signUpResult
        }

        override suspend fun signOut() {
            signedIn = false
        }

        override suspend fun deleteAccount(): AuthResult = AuthResult.Success("test-user")

        override fun isSignedIn(): Boolean = signedIn
    }
}

private suspend fun InMemorySettingsRepository.isOnboardingCompletedValue(): Boolean =
    isOnboardingCompleted().first()