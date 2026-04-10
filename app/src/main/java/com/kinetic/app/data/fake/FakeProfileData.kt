package com.kinetic.app.data.fake

import com.kinetic.app.data.models.Achievement
import com.kinetic.app.data.models.ExperienceLevel
import com.kinetic.app.data.models.FitnessGoal
import com.kinetic.app.data.models.UserProfile
import com.kinetic.app.data.models.UserStats

object FakeProfileData {
    val userProfile = UserProfile(
        uid = "fake_user_1",
        displayName = "Alex Thunder",
        email = "alex@kinetic.fit",
        membershipTierId = "pro",
        fitnessGoal = FitnessGoal.MUSCLE_GAIN,
        experienceLevel = ExperienceLevel.INTERMEDIATE,
        heightCm = 182f,
        weightKg = 84f,
        onboardingComplete = true
    )

    val userStats = UserStats(
        workoutsCompleted = 147,
        caloriesBurned = 68420,
        streakDays = 23,
        totalHours = 186.5f
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
