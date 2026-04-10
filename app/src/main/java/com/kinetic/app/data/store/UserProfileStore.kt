package com.kinetic.app.data.store

import com.kinetic.app.data.models.FitnessGoal
import com.kinetic.app.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileStore {
    val profile: Flow<UserProfile>
    suspend fun updateProfile(profile: UserProfile)
    suspend fun setFitnessGoal(goal: FitnessGoal)
    suspend fun setOnboardingComplete()
    suspend fun clearProfile()
}
