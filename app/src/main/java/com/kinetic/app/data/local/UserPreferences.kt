package com.kinetic.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kinetic.app.data.models.ExperienceLevel
import com.kinetic.app.data.models.FitnessGoal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kinetic_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USE_METRIC_UNITS = booleanPreferencesKey("use_metric_units")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val CONSENT_GIVEN = booleanPreferencesKey("consent_given")
        val CONSENT_VERSION = intPreferencesKey("consent_version")
        val USER_ID = stringPreferencesKey("user_id")
        val FITNESS_GOAL = stringPreferencesKey("fitness_goal")
        val TARGET_CALORIES = intPreferencesKey("target_calories")
        val EXPERIENCE_LEVEL = stringPreferencesKey("experience_level")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: true
    }

    val useMetricUnits: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.USE_METRIC_UNITS] ?: false
    }

    val soundEffects: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SOUND_EFFECTS] ?: true
    }

    val consentGiven: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.CONSENT_GIVEN] ?: false
    }

    val consentVersion: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.CONSENT_VERSION] ?: 0
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    val fitnessGoal: Flow<FitnessGoal> = context.dataStore.data.map { prefs ->
        prefs[Keys.FITNESS_GOAL]?.let { FitnessGoal.valueOf(it) } ?: FitnessGoal.GENERAL_FITNESS
    }

    val targetCalories: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.TARGET_CALORIES] ?: 2500
    }

    val experienceLevel: Flow<ExperienceLevel> = context.dataStore.data.map { prefs ->
        prefs[Keys.EXPERIENCE_LEVEL]?.let { ExperienceLevel.valueOf(it) } ?: ExperienceLevel.BEGINNER
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.DARK_MODE] = enabled }
    }

    suspend fun setUseMetricUnits(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.USE_METRIC_UNITS] = enabled }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SOUND_EFFECTS] = enabled }
    }

    suspend fun setConsentGiven(given: Boolean, version: Int = CURRENT_CONSENT_VERSION) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CONSENT_GIVEN] = given
            prefs[Keys.CONSENT_VERSION] = version
        }
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { prefs -> prefs[Keys.USER_ID] = id }
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs -> prefs[Keys.ONBOARDING_COMPLETED] = true }
    }

    suspend fun setFitnessGoal(goal: FitnessGoal, targetCalories: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FITNESS_GOAL] = goal.name
            prefs[Keys.TARGET_CALORIES] = targetCalories
        }
    }

    suspend fun setExperienceLevel(level: ExperienceLevel) {
        context.dataStore.edit { prefs -> prefs[Keys.EXPERIENCE_LEVEL] = level.name }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }

    companion object {
        const val CURRENT_CONSENT_VERSION = 1
    }
}
