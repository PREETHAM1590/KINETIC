package com.kinetic.app.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.models.FitnessGoal
import com.kinetic.app.data.store.UserActivityStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var prefs: UserPreferences
    private lateinit var activityStore: UserActivityStore
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        prefs = mock()
        val mockPrefs2: UserPreferences = mock()
        whenever(mockPrefs2.targetCalories).thenReturn(flowOf(2500))
        activityStore = UserActivityStore(mockPrefs2)
        viewModel = OnboardingViewModel(prefs, activityStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasPageZero() {
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun nextPage_advancesPage() {
        viewModel.nextPage()
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
    }

    @Test
    fun previousPage_decrementsPage() {
        viewModel.nextPage()
        viewModel.previousPage()
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun previousPage_doesNotGoNegative() {
        viewModel.previousPage()
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun selectGoal_updatesState() {
        viewModel.selectGoal(FitnessGoal.MUSCLE_GAIN)
        assertThat(viewModel.uiState.value.selectedGoal).isEqualTo(FitnessGoal.MUSCLE_GAIN)
    }

    @Test
    fun completeOnboarding_setsOnboardingCompleted() = runTest {
        viewModel.completeOnboarding()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.onboardingCompleted).isTrue()
    }
}
