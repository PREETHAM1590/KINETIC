package com.kinetic.app.data.repository

import com.kinetic.app.data.fake.FakeProfileData
import com.kinetic.app.data.models.Achievement
import com.kinetic.app.data.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface ProfileRepository {
    fun getProfile(): Flow<UserProfile>
    fun getAchievements(): Flow<List<Achievement>>
}

@Singleton
class FakeProfileRepository @Inject constructor() : ProfileRepository {

    override fun getProfile(): Flow<UserProfile> = flow {
        delay(300)
        emit(FakeProfileData.userProfile)
    }

    override fun getAchievements(): Flow<List<Achievement>> = flow {
        delay(300)
        emit(FakeProfileData.achievements)
    }
}
