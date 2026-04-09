package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.store.UserActivityStore
import com.kinetic.app.domain.coaching.CoachEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoachUiState(
    val insights: List<CoachInsight> = emptyList(),
    val performanceScore: Int = 0,
    val motivationalMessage: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val userActivityStore: UserActivityStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState(isLoading = true))
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()
    
    private val coachEngine = CoachEngine()

    init {
        observeActivityState()
    }

    private fun observeActivityState() {
        viewModelScope.launch {
            userActivityStore.state.collect { activityState ->
                val insights = coachEngine.generateInsights(activityState)
                val score = coachEngine.calculatePerformanceScore(activityState)
                val message = coachEngine.getMotivationalMessage(activityState)
                
                _uiState.value = CoachUiState(
                    insights = insights,
                    performanceScore = score,
                    motivationalMessage = message,
                    isLoading = false
                )
            }
        }
    }
}
