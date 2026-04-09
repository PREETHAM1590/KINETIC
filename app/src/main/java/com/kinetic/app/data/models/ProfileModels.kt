package com.kinetic.app.data.models

data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
    val currentTier: String,
    val stats: UserStats
)

data class UserStats(
    val workoutsCompleted: Int,
    val caloriesBurned: Int,
    val streakDays: Int,
    val totalHours: Float
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)
