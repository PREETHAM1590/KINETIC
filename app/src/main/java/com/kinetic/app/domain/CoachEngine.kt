package com.kinetic.app.domain

import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.models.CoachInsightType
import com.kinetic.app.data.store.UserActivityState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachEngine @Inject constructor() {

    private val twoHoursMs = 2 * 60 * 60 * 1000L

    fun generateInsights(state: UserActivityState): List<CoachInsight> {
        val insights = mutableListOf<CoachInsight>()

        // Rule 1: calorie surplus nudge
        if (state.todayCaloriesBurned > 0) {
            val remaining = state.targetCalories - state.todayCaloriesConsumed - state.todayCaloriesBurned
            if (remaining > 0) {
                insights += CoachInsight(
                    message = "You burned ${state.todayCaloriesBurned} cal — you're $remaining short of today's surplus goal.",
                    actionLabel = "Log a meal",
                    actionRoute = "diet",
                    type = CoachInsightType.NUDGE
                )
            }
        }

        // Rule 2: missed sessions warning
        if (state.missedSessionsInRow >= 3) {
            insights += CoachInsight(
                message = "You've missed ${state.missedSessionsInRow} sessions. Want to adjust your plan?",
                actionLabel = "See workouts",
                actionRoute = "workouts",
                type = CoachInsightType.WARNING
            )
        }

        // Rule 3: streak celebration (every 7 days)
        if (state.currentStreakDays > 0 && state.currentStreakDays % 7 == 0) {
            insights += CoachInsight(
                message = "${state.currentStreakDays}-day streak! You're in the top 10% this week.",
                type = CoachInsightType.CELEBRATION
            )
        }

        // Rule 4: post-workout protein window
        val now = System.currentTimeMillis()
        val lastWorkout = state.lastWorkoutTimestampMs
        val lastMeal = state.lastMealTimestampMs
        if (lastWorkout != null &&
            (now - lastWorkout) < twoHoursMs &&
            (lastMeal == null || lastMeal < lastWorkout)
        ) {
            insights += CoachInsight(
                message = "Recovery window closing — have protein in the next 30 min.",
                actionLabel = "Log meal",
                actionRoute = "diet",
                type = CoachInsightType.WARNING
            )
        }

        return insights
    }
}
