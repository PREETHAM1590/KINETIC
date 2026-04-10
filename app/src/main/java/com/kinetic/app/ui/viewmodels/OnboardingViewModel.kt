package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.models.ExperienceLevel
import com.kinetic.app.data.models.FitnessGoal
import com.kinetic.app.data.models.Gender
import com.kinetic.app.data.store.UserActivityStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val onboardingCompleted: Boolean = false,
    val selectedGoal: FitnessGoal? = null,
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val gender: Gender = Gender.OTHER,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val isSaving: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val activityStore: UserActivityStore
) : ViewModel() {

    companion object { const val TOTAL_PAGES = 5 }

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextPage() = _uiState.update { it.copy(currentPage = (it.currentPage + 1).coerceAtMost(TOTAL_PAGES - 1)) }
    fun previousPage() = _uiState.update { if (it.currentPage > 0) it.copy(currentPage = it.currentPage - 1) else it }
    fun selectGoal(goal: FitnessGoal) = _uiState.update { it.copy(selectedGoal = goal) }
    fun selectExperience(level: ExperienceLevel) = _uiState.update { it.copy(experienceLevel = level) }
    fun setGender(gender: Gender) = _uiState.update { it.copy(gender = gender) }
    fun setHeight(cm: Float) = _uiState.update { it.copy(heightCm = cm) }
    fun setWeight(kg: Float) = _uiState.update { it.copy(weightKg = kg) }

    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val goal = _uiState.value.selectedGoal ?: FitnessGoal.GENERAL_FITNESS
            val calories = defaultCaloriesFor(goal, _uiState.value.weightKg)
            prefs.setFitnessGoal(goal, calories)
            prefs.setExperienceLevel(_uiState.value.experienceLevel)
            prefs.setOnboardingComplete()
            activityStore.setTargetCalories(calories)
            _uiState.update { it.copy(isSaving = false, onboardingCompleted = true) }
        }
    }

    private fun defaultCaloriesFor(goal: FitnessGoal, weightKg: Float): Int = when (goal) {
        FitnessGoal.WEIGHT_LOSS     -> (weightKg * 28).toInt().coerceIn(1400, 2000)
        FitnessGoal.MUSCLE_GAIN     -> (weightKg * 38).toInt().coerceIn(2200, 3500)
        FitnessGoal.ENDURANCE       -> (weightKg * 35).toInt().coerceIn(2000, 3000)
        FitnessGoal.REHABILITATION  -> (weightKg * 30).toInt().coerceIn(1600, 2200)
        FitnessGoal.GENERAL_FITNESS -> (weightKg * 32).toInt().coerceIn(1800, 2500)
    }
}
