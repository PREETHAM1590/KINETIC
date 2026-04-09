package com.kinetic.app.data.fake

import com.kinetic.app.data.models.ChartDataPoint
import com.kinetic.app.data.models.PersonalRecord
import com.kinetic.app.data.models.WeeklyReport

object FakeReportsData {
    val weeklyReports = listOf(
        WeeklyReport("This Week", 5, 2100, 285, "Deadlifts"),
        WeeklyReport("Last Week", 4, 1800, 240, "Bench Press"),
        WeeklyReport("2 Weeks Ago", 6, 2450, 330, "Squats"),
        WeeklyReport("3 Weeks Ago", 3, 1350, 180, "Pull-ups")
    )

    val personalRecords = listOf(
        PersonalRecord("Deadlift", "315 lbs", "Mar 28"),
        PersonalRecord("Bench Press", "225 lbs", "Mar 22"),
        PersonalRecord("Squat", "275 lbs", "Mar 15"),
        PersonalRecord("5K Run", "22:45", "Mar 10")
    )

    val weeklyWorkoutChart = listOf(
        ChartDataPoint("Mon", 1f),
        ChartDataPoint("Tue", 1f),
        ChartDataPoint("Wed", 0f),
        ChartDataPoint("Thu", 1f),
        ChartDataPoint("Fri", 1f),
        ChartDataPoint("Sat", 1f),
        ChartDataPoint("Sun", 0f)
    )
}
