package com.kinetic.app.data.models

data class SettingsData(
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = true,
    val useMetricUnits: Boolean = false,
    val soundEffects: Boolean = true
)

sealed class AuthResult {
    data class Success(val userId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
