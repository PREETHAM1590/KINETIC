package com.kinetic.app.data.models

data class WorkoutItem(
    val id: String,
    val title: String,
    val category: String,
    val durationMin: Int,
    val calories: Int,
    val difficulty: String
)

data class HiitItem(
    val id: String,
    val title: String,
    val durationMin: Int,
    val calories: Int
)

data class ExerciseItem(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightLbs: Int,
    val restSeconds: Int = 60
)

data class ActiveWorkout(
    val id: String,
    val title: String,
    val difficulty: String,
    val durationMin: Int,
    val caloriesPerMin: Int = 8,
    val exercises: List<ExerciseItem>
)
