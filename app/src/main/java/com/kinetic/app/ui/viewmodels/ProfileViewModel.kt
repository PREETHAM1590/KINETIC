package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.Achievement
import com.kinetic.app.data.models.UserProfile
import com.kinetic.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val achievements: List<Achievement> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                profileRepository.getProfile(),
                profileRepository.getAchievements()
            ) { profile, achievements ->
                ProfileUiState(
                    isLoading = false,
                    profile = profile,
                    achievements = achievements
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
