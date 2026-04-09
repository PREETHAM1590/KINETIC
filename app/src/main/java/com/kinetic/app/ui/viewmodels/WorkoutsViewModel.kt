package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem
import com.kinetic.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutsUiState(
    val isLoading: Boolean = true,
    val workouts: List<WorkoutItem> = emptyList(),
    val hiitWorkouts: List<HiitItem> = emptyList(),
    val currentFilter: String = "All",
    val error: String? = null
)

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsUiState())
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    private val _filterCategory = MutableStateFlow("All")

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                workoutRepository.getWorkouts(),
                workoutRepository.getHiitWorkouts(),
                _filterCategory
            ) { workouts, hiit, filter ->
                val filtered = if (filter == "All") workouts
                else workouts.filter { it.category.equals(filter, ignoreCase = true) }
                WorkoutsUiState(
                    isLoading = false,
                    workouts = filtered,
                    hiitWorkouts = hiit,
                    currentFilter = filter
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(category: String) {
        _filterCategory.value = category
    }
}
