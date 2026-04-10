package com.kinetic.app.data.models

data class DailySteps(val date: String, val steps: Int, val source: StepSource = StepSource.MANUAL)
enum class StepSource { HEALTH_CONNECT, TREADMILL_SCAN, MANUAL }
data class TreadmillSession(
    val durationMins: Int,
    val speedKmh: Float,
    val inclinePct: Float,
    val distanceKm: Float = speedKmh * durationMins / 60f,
    val caloriesBurned: Int = 0,
    val estimatedSteps: Int = (distanceKm * 1312).toInt()
)
