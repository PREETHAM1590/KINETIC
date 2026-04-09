package com.kinetic.app.data.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class UserActivityState(
    val todayCaloriesBurned: Int = 0,
    val todayCaloriesConsumed: Int = 0,
    val targetCalories: Int = 2500,
    val currentStreakDays: Int = 0,
    val lastWorkoutTimestampMs: Long = 0L,
    val lastMealTimestampMs: Long = 0L,
    val missedSessionsInRow: Int = 0
)

@Singleton
class UserActivityStore @Inject constructor() {
    private val _state = MutableStateFlow(UserActivityState())
    val state: StateFlow<UserActivityState> = _state.asStateFlow()

    fun recordWorkoutCompleted(caloriesBurned: Int) {
        _state.value = _state.value.copy(
            todayCaloriesBurned = _state.value.todayCaloriesBurned + caloriesBurned,
            lastWorkoutTimestampMs = System.currentTimeMillis(),
            missedSessionsInRow = 0,
            currentStreakDays = _state.value.currentStreakDays + 1
        )
    }

    fun recordMealLogged(calories: Int) {
        _state.value = _state.value.copy(
            todayCaloriesConsumed = _state.value.todayCaloriesConsumed + calories,
            lastMealTimestampMs = System.currentTimeMillis()
        )
    }

    fun recordSessionMissed() {
        _state.value = _state.value.copy(
            missedSessionsInRow = _state.value.missedSessionsInRow + 1
        )
    }

    fun setTargetCalories(target: Int) {
        _state.value = _state.value.copy(targetCalories = target)
    }
}
