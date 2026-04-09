package com.kinetic.app.data.models

data class WeeklyReport(
    val weekLabel: String,
    val workoutsCompleted: Int,
    val caloriesBurned: Int,
    val totalMinutes: Int,
    val topExercise: String
)

data class PersonalRecord(
    val exercise: String,
    val value: String,
    val date: String
)

data class ChartDataPoint(
    val label: String,
    val value: Float
)
