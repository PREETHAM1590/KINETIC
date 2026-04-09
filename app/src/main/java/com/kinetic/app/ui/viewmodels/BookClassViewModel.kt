package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.ClassItem
import com.kinetic.app.data.repository.MembershipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookClassUiState(
    val isLoading: Boolean = true,
    val classes: List<ClassItem> = emptyList(),
    val selectedCategory: String = "All",
    val error: String? = null
)

@HiltViewModel
class BookClassViewModel @Inject constructor(
    private val membershipRepository: MembershipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookClassUiState())
    val uiState: StateFlow<BookClassUiState> = _uiState.asStateFlow()

    private val selectedCategory = MutableStateFlow(BookClassUiState().selectedCategory)

    val categories = listOf("All", "Yoga", "HIIT", "Spin", "Boxing")

    init {
        observeClasses()
    }

    private fun observeClasses() {
        viewModelScope.launch {
            selectedCategory
                .distinctUntilChanged()
                .onEach { category ->
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            selectedCategory = category,
                            error = null
                        )
                    }
                }
                .flatMapLatest { category ->
                    membershipRepository.getFilteredClasses(category)
                        .map { classes -> CategoryClasses(category = category, classes = classes) }
                        .catch { e ->
                            emit(
                                CategoryClasses(
                                    category = category,
                                    classes = emptyList(),
                                    error = e.message ?: "Failed to load classes"
                                )
                            )
                        }
                }
                .collect { result ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            classes = if (result.error == null) result.classes else state.classes,
                            selectedCategory = result.category,
                            error = result.error
                        )
                    }
                }
        }
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun bookClass(classId: String) {
        // In real app, would call repository to book the class
    }

    private data class CategoryClasses(
        val category: String,
        val classes: List<ClassItem>,
        val error: String? = null
    )
}