package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeProfileRepository
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
class ProfileViewModelTest {

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
    fun loadData_populatesProfileAndAchievements() = runTest {
        val viewModel = ProfileViewModel(FakeProfileRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.profile).isNotNull()
        assertThat(state.achievements).isNotEmpty()
    }

    @Test
    fun profile_hasValidNameAndEmail() = runTest {
        val viewModel = ProfileViewModel(FakeProfileRepository())

        advanceUntilIdle()

        val profile = viewModel.uiState.value.profile!!
        assertThat(profile.name).isNotEmpty()
        assertThat(profile.email).contains("@")
    }
}
