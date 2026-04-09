package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.ui.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {

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
    fun init_whenOnboardingNotCompleted_routesToOnboarding() = runTest {
        val settingsRepository = InMemorySettingsRepository(
            onboardingCompleted = false,
            consentGiven = false
        )
        val authRepository = InMemoryAuthRepository(signedIn = false)

        val viewModel = StartupViewModel(settingsRepository, authRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.startDestination).isEqualTo(Screen.Onboarding.route)
    }

    @Test
    fun init_whenConsentMissing_routesToConsent() = runTest {
        val settingsRepository = InMemorySettingsRepository(
            onboardingCompleted = true,
            consentGiven = false
        )
        val authRepository = InMemoryAuthRepository(signedIn = false)

        val viewModel = StartupViewModel(settingsRepository, authRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.startDestination).isEqualTo(Screen.Consent.route)
    }

    @Test
    fun init_whenSignedOut_routesToLogin() = runTest {
        val settingsRepository = InMemorySettingsRepository(
            onboardingCompleted = true,
            consentGiven = true
        )
        val authRepository = InMemoryAuthRepository(signedIn = false)

        val viewModel = StartupViewModel(settingsRepository, authRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.startDestination).isEqualTo(Screen.Login.route)
    }

    @Test
    fun init_whenSignedInAndReady_routesToWorkouts() = runTest {
        val settingsRepository = InMemorySettingsRepository(
            onboardingCompleted = true,
            consentGiven = true
        )
        val authRepository = InMemoryAuthRepository(signedIn = true)

        val viewModel = StartupViewModel(settingsRepository, authRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.startDestination).isEqualTo(Screen.Workouts.route)
    }
}