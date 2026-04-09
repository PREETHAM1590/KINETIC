package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
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
class OnboardingViewModelTest {

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
    fun init_loadsPersistedCompletionFlag() = runTest {
        val viewModel = OnboardingViewModel(InMemorySettingsRepository(onboardingCompleted = false))

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.onboardingCompleted).isFalse()
    }

    @Test
    fun nextPage_advancesIndex() = runTest {
        val viewModel = OnboardingViewModel(InMemorySettingsRepository())

        advanceUntilIdle()
        viewModel.nextPage()

        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
    }

    @Test
    fun completeOnboarding_updatesState() = runTest {
        val viewModel = OnboardingViewModel(InMemorySettingsRepository())

        advanceUntilIdle()
        viewModel.completeOnboarding()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.onboardingCompleted).isTrue()
    }
}
