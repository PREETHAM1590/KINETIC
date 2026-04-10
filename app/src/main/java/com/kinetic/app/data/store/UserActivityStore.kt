package com.kinetic.app.data.store

import com.kinetic.app.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class UserActivityState(
    val todayCaloriesBurned: Int = 0,
    val todayCaloriesConsumed: Int = 0,
    val targetCalories: Int = 2500,
    val currentStreakDays: Int = 0,
    val lastWorkoutTimestampMs: Long? = null,
    val lastMealTimestampMs: Long? = null,
    val missedSessionsInRow: Int = 0
)

@Singleton
class UserActivityStore @Inject constructor(
    private val prefs: UserPreferences
) {
    private val _state = MutableStateFlow(UserActivityState())
    val state: StateFlow<UserActivityState> = _state.asStateFlow()

    val targetCalories: Flow<Int> = prefs.targetCalories

    fun recordWorkoutCompleted(caloriesBurned: Int) {
        val now = System.currentTimeMillis()
        _state.update { current ->
            val alreadyLoggedToday = current.lastWorkoutTimestampMs?.let {
                isSameCalendarDay(it, now)
            } ?: false
            current.copy(
                todayCaloriesBurned = current.todayCaloriesBurned + caloriesBurned,
                lastWorkoutTimestampMs = now,
                missedSessionsInRow = 0,
                currentStreakDays = if (alreadyLoggedToday) current.currentStreakDays
                                    else current.currentStreakDays + 1
            )
        }
    }

    fun recordMealLogged(calories: Int) {
        _state.update { current ->
            current.copy(
                todayCaloriesConsumed = current.todayCaloriesConsumed + calories,
                lastMealTimestampMs = System.currentTimeMillis()
            )
        }
    }

    fun recordSessionMissed() {
        _state.update { current ->
            current.copy(missedSessionsInRow = current.missedSessionsInRow + 1)
        }
    }

    fun setTargetCalories(target: Int) {
        _state.update { current -> current.copy(targetCalories = target) }
    }

    private fun isSameCalendarDay(tsA: Long, tsB: Long): Boolean {
        val calA = Calendar.getInstance().apply { timeInMillis = tsA }
        val calB = Calendar.getInstance().apply { timeInMillis = tsB }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
               calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
    }
}
