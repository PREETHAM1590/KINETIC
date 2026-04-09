package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ConsentViewModelTest {

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
    fun saveConsent_withoutAcceptance_setsErrorAndDoesNotPersist() = runTest {
        val settingsRepository = InMemorySettingsRepository(consentGiven = false)
        val viewModel = ConsentViewModel(settingsRepository)
        var successCalled = false

        viewModel.saveConsent { successCalled = true }

        val state = viewModel.uiState.value
        assertThat(state.error).isEqualTo("You must accept the privacy consent to continue")
        assertThat(state.isSaving).isFalse()
        assertThat(successCalled).isFalse()
        assertThat(settingsRepository.isConsentGiven().first()).isFalse()
    }

    @Test
    fun saveConsent_withAcceptance_persistsConsentAndInvokesSuccess() = runTest {
        val settingsRepository = InMemorySettingsRepository(consentGiven = false)
        val viewModel = ConsentViewModel(settingsRepository)
        var successCalled = false

        viewModel.setAccepted(true)
        viewModel.saveConsent { successCalled = true }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.accepted).isTrue()
        assertThat(state.error).isNull()
        assertThat(state.isSaving).isFalse()
        assertThat(successCalled).isTrue()
        assertThat(settingsRepository.isConsentGiven().first()).isTrue()
    }

    @Test
    fun setAccepted_clearsExistingError() = runTest {
        val settingsRepository = InMemorySettingsRepository(consentGiven = false)
        val viewModel = ConsentViewModel(settingsRepository)

        viewModel.saveConsent(onSuccess = {})
        assertThat(viewModel.uiState.value.error).isNotNull()

        viewModel.setAccepted(true)

        val state = viewModel.uiState.value
        assertThat(state.accepted).isTrue()
        assertThat(state.error).isNull()
    }
}