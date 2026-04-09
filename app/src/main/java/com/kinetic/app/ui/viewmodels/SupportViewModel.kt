package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FaqItem(
    val question: String,
    val answer: String,
    val isExpanded: Boolean = false
)

data class TicketFormState(
    val subject: String = "",
    val message: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false
)

sealed class SupportUiState {
    object Loading : SupportUiState()
    data class Success(
        val faqs: List<FaqItem> = emptyList(),
        val ticketForm: TicketFormState = TicketFormState(),
        val contactOptions: List<ContactOption> = defaultContactOptions
    ) : SupportUiState()
    data class Error(val message: String) : SupportUiState()
}

data class ContactOption(
    val label: String,
    val icon: String
)

private val defaultContactOptions = listOf(
    ContactOption("LIVE CHAT", "Chat"),
    ContactOption("EMAIL", "Email"),
    ContactOption("CALL", "Phone")
)

class SupportViewModel : ViewModel() {
    private val defaultFaqs = listOf(
        FaqItem("How do I cancel my membership?", "You can cancel anytime from Settings > Account > Cancel Membership. Your access will continue until the end of the billing period."),
        FaqItem("How does the AI Calorie Scanner work?", "Point your camera at any meal and our AI will identify the food items and estimate calories, protein, carbs, and fats using our trained model."),
        FaqItem("Can I freeze my membership?", "Yes! Pro and Elite members can freeze their membership for up to 30 days per year. Go to Settings > Account > Freeze Membership."),
        FaqItem("How do I track my workouts?", "Start any workout from the Workouts tab. The app will automatically log your sets, reps, and duration. View your history in Reports."),
        FaqItem("What's included in the Elite plan?", "Elite includes everything in Pro plus 1-on-1 personal training, custom meal prep, priority class booking, recovery room access, and 2 guest passes per month.")
    )

    private val _uiState = MutableStateFlow<SupportUiState>(
        SupportUiState.Success(faqs = defaultFaqs)
    )
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    fun toggleFaq(index: Int) {
        val current = _uiState.value
        if (current !is SupportUiState.Success) return
        _uiState.value = current.copy(
            faqs = current.faqs.mapIndexed { i, faq ->
                if (i == index) faq.copy(isExpanded = !faq.isExpanded) else faq
            }
        )
    }

    fun updateSubject(subject: String) {
        val current = _uiState.value
        if (current !is SupportUiState.Success) return
        _uiState.value = current.copy(
            ticketForm = current.ticketForm.copy(subject = subject)
        )
    }

    fun updateMessage(message: String) {
        val current = _uiState.value
        if (current !is SupportUiState.Success) return
        _uiState.value = current.copy(
            ticketForm = current.ticketForm.copy(message = message)
        )
    }

    fun submitTicket() {
        val current = _uiState.value
        if (current !is SupportUiState.Success) return
        _uiState.value = current.copy(
            ticketForm = current.ticketForm.copy(isSubmitting = true)
        )

        viewModelScope.launch {
            delay(1500)
            val state = _uiState.value
            if (state is SupportUiState.Success) {
                _uiState.value = state.copy(
                    ticketForm = TicketFormState(isSubmitted = true)
                )
            }
        }
    }
}
