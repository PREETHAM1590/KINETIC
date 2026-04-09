package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.MembershipTier
import com.kinetic.app.data.repository.MembershipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MembershipUiState(
    val isLoading: Boolean = true,
    val tiers: List<MembershipTier> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MembershipViewModel @Inject constructor(
    private val membershipRepository: MembershipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MembershipUiState())
    val uiState: StateFlow<MembershipUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            membershipRepository.getTiers()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
                .collect { tiers ->
                    _uiState.value = MembershipUiState(isLoading = false, tiers = tiers)
                }
        }
    }
}
