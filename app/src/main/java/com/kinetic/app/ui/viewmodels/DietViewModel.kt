package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.DailyNutrition
import com.kinetic.app.data.models.MealItem
import com.kinetic.app.data.repository.DietRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietUiState(
    val isLoading: Boolean = true,
    val meals: List<MealItem> = emptyList(),
    val dailyNutrition: DailyNutrition? = null,
    val error: String? = null
)

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietUiState())
    val uiState: StateFlow<DietUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                dietRepository.getMeals(),
                dietRepository.getDailyNutrition()
            ) { meals, nutrition ->
                DietUiState(
                    isLoading = false,
                    meals = meals,
                    dailyNutrition = nutrition
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
