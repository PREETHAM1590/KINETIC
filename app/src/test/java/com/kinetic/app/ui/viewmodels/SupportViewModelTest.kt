package com.kinetic.app.ui.viewmodels

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SupportViewModelTest {

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
    fun initialState_isSuccessWithFaqs() = runTest {
        val viewModel = SupportViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(SupportUiState.Success::class.java)
            val success = state as SupportUiState.Success
            assertThat(success.faqs).isNotEmpty()
        }
    }

    @Test
    fun initialState_faqsAreAllCollapsed() = runTest {
        val viewModel = SupportViewModel()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs.all { !it.isExpanded }).isTrue()
    }

    @Test
    fun toggleFaq_expandsCollapsedFaq() = runTest {
        val viewModel = SupportViewModel()
        viewModel.toggleFaq(0)
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs[0].isExpanded).isTrue()
    }

    @Test
    fun toggleFaq_twice_collapsesFaq() = runTest {
        val viewModel = SupportViewModel()
        viewModel.toggleFaq(0)
        viewModel.toggleFaq(0)
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs[0].isExpanded).isFalse()
    }

    @Test
    fun toggleFaq_doesNotAffectOtherFaqs() = runTest {
        val viewModel = SupportViewModel()
        viewModel.toggleFaq(0)
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs[1].isExpanded).isFalse()
        assertThat(state.faqs[2].isExpanded).isFalse()
    }

    @Test
    fun updateSubject_setsSubject() = runTest {
        val viewModel = SupportViewModel()
        viewModel.updateSubject("Test Subject")
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.subject).isEqualTo("Test Subject")
    }

    @Test
    fun updateMessage_setsMessage() = runTest {
        val viewModel = SupportViewModel()
        viewModel.updateMessage("Test Message")
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.message).isEqualTo("Test Message")
    }

    @Test
    fun initialTicketForm_isEmpty() = runTest {
        val viewModel = SupportViewModel()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.subject).isEmpty()
        assertThat(state.ticketForm.message).isEmpty()
        assertThat(state.ticketForm.isSubmitting).isFalse()
        assertThat(state.ticketForm.isSubmitted).isFalse()
    }

    @Test
    fun submitTicket_setsIsSubmittingTrue() = runTest {
        val viewModel = SupportViewModel()
        viewModel.submitTicket()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.isSubmitting).isTrue()
    }

    @Test
    fun submitTicket_afterDelay_setsIsSubmittedTrue() = runTest {
        val viewModel = SupportViewModel()
        viewModel.submitTicket()
        advanceTimeBy(1500)
        advanceUntilIdle()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.isSubmitted).isTrue()
    }

    @Test
    fun submitTicket_afterDelay_resetsFormFields() = runTest {
        val viewModel = SupportViewModel()
        viewModel.updateSubject("My Issue")
        viewModel.updateMessage("Please help")
        viewModel.submitTicket()
        advanceTimeBy(1500)
        advanceUntilIdle()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.subject).isEmpty()
        assertThat(state.ticketForm.message).isEmpty()
    }

    @Test
    fun submitTicket_afterDelay_isSubmittingIsFalse() = runTest {
        val viewModel = SupportViewModel()
        viewModel.submitTicket()
        advanceTimeBy(1500)
        advanceUntilIdle()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.ticketForm.isSubmitting).isFalse()
    }

    @Test
    fun faqs_haveNonEmptyQuestions() = runTest {
        val viewModel = SupportViewModel()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs.all { it.question.isNotEmpty() }).isTrue()
    }

    @Test
    fun faqs_haveNonEmptyAnswers() = runTest {
        val viewModel = SupportViewModel()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs.all { it.answer.isNotEmpty() }).isTrue()
    }

    @Test
    fun contactOptions_arePresent() = runTest {
        val viewModel = SupportViewModel()
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.contactOptions).isNotEmpty()
    }

    @Test
    fun toggleFaq_onDifferentIndices_worksIndependently() = runTest {
        val viewModel = SupportViewModel()
        viewModel.toggleFaq(1)
        val state = viewModel.uiState.value as SupportUiState.Success
        assertThat(state.faqs[0].isExpanded).isFalse()
        assertThat(state.faqs[1].isExpanded).isTrue()
        assertThat(state.faqs[2].isExpanded).isFalse()
    }
}
