package com.kinetic.app.data.models

enum class FitnessGoal { WEIGHT_LOSS, MUSCLE_GAIN, ENDURANCE, REHABILITATION, GENERAL_FITNESS }
enum class ExperienceLevel { BEGINNER, INTERMEDIATE, ADVANCED }
enum class Gender { MALE, FEMALE, OTHER }

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

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: Gender = Gender.OTHER,
    val dateOfBirthMs: Long = 0L,
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val fitnessGoal: FitnessGoal = FitnessGoal.GENERAL_FITNESS,
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val gymId: String = "",
    val trainerId: String? = null,
    val membershipTierId: String = "basic",
    val onboardingComplete: Boolean = false,
    val profilePhotoUrl: String? = null
) {
    val bmi: Float get() = if (heightCm > 0) weightKg / ((heightCm / 100f) * (heightCm / 100f)) else 0f
    val ageYears: Int get() {
        if (dateOfBirthMs == 0L) return 0
        val now = System.currentTimeMillis()
        return ((now - dateOfBirthMs) / (365.25 * 24 * 3600 * 1000)).toInt()
    }
}
