package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.kinetic.app.data.models.MealDetail
import com.kinetic.app.data.repository.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

data class MealDetailUiState(
    val isLoading: Boolean = true,
    val meal: MealDetail? = null,
    val isMealLogged: Boolean = false,
    val showLoggedConfirmation: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MealDetailUiState())
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    fun loadMeal(mealId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val meal = dietRepository.getMealDetailById(mealId).first()
            _uiState.value = if (meal == null) {
                MealDetailUiState(isLoading = false, error = "Meal not found")
            } else {
                MealDetailUiState(isLoading = false, meal = meal)
            }
        }
    }

    fun loadMealByName(name: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val meal = dietRepository.getMealDetailByName(name).first()
            _uiState.value = if (meal == null) {
                MealDetailUiState(isLoading = false, error = "Meal not found")
            } else {
                MealDetailUiState(isLoading = false, meal = meal)
            }
        }
    }

    fun logMeal() {
        _uiState.value = _uiState.value.copy(
            isMealLogged = true,
            showLoggedConfirmation = true
        )
    }

    fun dismissLoggedConfirmation() {
        _uiState.value = _uiState.value.copy(showLoggedConfirmation = false)
    }
}