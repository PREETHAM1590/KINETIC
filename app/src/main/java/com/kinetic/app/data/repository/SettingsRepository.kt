package com.kinetic.app.data.repository

import com.kinetic.app.data.local.UserPreferences
import com.kinetic.app.data.models.SettingsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    fun getSettings(): Flow<SettingsData>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setUseMetricUnits(enabled: Boolean)
    suspend fun setSoundEffects(enabled: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setConsentGiven(given: Boolean, version: Int)
    fun isOnboardingCompleted(): Flow<Boolean>
    fun isConsentGiven(): Flow<Boolean>
    suspend fun clearAllData()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : SettingsRepository {

    override fun getSettings(): Flow<SettingsData> = combine(
        userPreferences.notificationsEnabled,
        userPreferences.darkMode,
        userPreferences.useMetricUnits,
        userPreferences.soundEffects
    ) { notifications, darkMode, metricUnits, soundEffects ->
        SettingsData(
            notificationsEnabled = notifications,
            darkMode = darkMode,
            useMetricUnits = metricUnits,
            soundEffects = soundEffects
        )
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        userPreferences.setNotificationsEnabled(enabled)
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        userPreferences.setDarkMode(enabled)
    }

    override suspend fun setUseMetricUnits(enabled: Boolean) {
        userPreferences.setUseMetricUnits(enabled)
    }

    override suspend fun setSoundEffects(enabled: Boolean) {
        userPreferences.setSoundEffects(enabled)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        userPreferences.setOnboardingCompleted(completed)
    }

    override suspend fun setConsentGiven(given: Boolean, version: Int) {
        userPreferences.setConsentGiven(given, version)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = userPreferences.onboardingCompleted

    override fun isConsentGiven(): Flow<Boolean> = userPreferences.consentGiven

    override suspend fun clearAllData() {
        userPreferences.clearAll()
    }
}
