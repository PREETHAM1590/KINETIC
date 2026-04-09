package com.kinetic.app.data.fake

import com.kinetic.app.data.models.Achievement
import com.kinetic.app.data.models.UserProfile
import com.kinetic.app.data.models.UserStats

object FakeProfileData {
    val userProfile = UserProfile(
        name = "Alex Thunder",
        email = "alex@kinetic.fit",
        memberSince = "Jan 2025",
        currentTier = "PRO",
        stats = UserStats(
            workoutsCompleted = 147,
            caloriesBurned = 68420,
            streakDays = 23,
            totalHours = 186.5f
        )
    )

    val achievements = listOf(
        Achievement("a1", "FIRST BLOOD", "Complete your first workout", "🏋️", true),
        Achievement("a2", "WEEK WARRIOR", "7-day workout streak", "🔥", true),
        Achievement("a3", "IRON WILL", "Complete 100 workouts", "💪", true),
        Achievement("a4", "CALORIE CRUSHER", "Burn 50,000 calories", "⚡", true),
        Achievement("a5", "MONTH MASTER", "30-day workout streak", "🏆", false),
        Achievement("a6", "CENTURY CLUB", "Log 100 hours of workouts", "💎", false)
    )
}
