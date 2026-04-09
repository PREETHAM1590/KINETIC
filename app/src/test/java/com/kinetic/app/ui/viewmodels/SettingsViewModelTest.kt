package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.models.AuthResult
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
class SettingsViewModelTest {

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
    fun initialState_reflectsRepositorySettings() = runTest {
        val settingsRepository = InMemorySettingsRepository()
        val authRepository = InMemoryAuthRepository()
        val viewModel = SettingsViewModel(settingsRepository, authRepository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.notificationsEnabled).isTrue()
        assertThat(state.darkMode).isTrue()
    }

    @Test
    fun toggleNotifications_updatesState() = runTest {
        val settingsRepository = InMemorySettingsRepository()
        val authRepository = InMemoryAuthRepository()
        val viewModel = SettingsViewModel(settingsRepository, authRepository)

        advanceUntilIdle()
        viewModel.toggleNotifications()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.notificationsEnabled).isFalse()
    }

    @Test
    fun deleteAllData_surfacesAuthDeletionError() = runTest {
        val settingsRepository = InMemorySettingsRepository()
        val authRepository = InMemoryAuthRepository(
            signedIn = true,
            deleteResult = AuthResult.Error("Delete failed")
        )
        val viewModel = SettingsViewModel(settingsRepository, authRepository)

        advanceUntilIdle()
        viewModel.deleteAllData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isSaving).isFalse()
        assertThat(state.error).isEqualTo("Delete failed")
    }
}
