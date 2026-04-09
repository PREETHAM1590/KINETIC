package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.repository.FakeMembershipRepository
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
class MembershipViewModelTest {

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
    fun loadData_populatesMembershipTiers() = runTest {
        val viewModel = MembershipViewModel(FakeMembershipRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.tiers).isNotEmpty()
    }

    @Test
    fun onlyOneTierIsCurrent() = runTest {
        val viewModel = MembershipViewModel(FakeMembershipRepository())

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.tiers.count { it.isCurrent }).isEqualTo(1)
    }
}
