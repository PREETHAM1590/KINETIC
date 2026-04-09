package com.kinetic.app.domain

import com.kinetic.app.data.models.CoachInsightType
import com.kinetic.app.data.store.UserActivityState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoachEngineTest {

    private lateinit var engine: CoachEngine

    @Before
    fun setUp() {
        engine = CoachEngine()
    }

    @Test
    fun `no insights when no activity`() {
        val state = UserActivityState()
        val insights = engine.generateInsights(state)
        assertTrue(insights.isEmpty())
    }

    @Test
    fun `NUDGE shown when calories burned but still below surplus target`() {
        val state = UserActivityState(
            todayCaloriesBurned = 480,
            todayCaloriesConsumed = 1800,
            targetCalories = 2500
        )
        val insights = engine.generateInsights(state)
        val nudge = insights.find { it.type == CoachInsightType.NUDGE }
        assertNotNull(nudge)
        assertTrue(nudge!!.message.contains("480 cal"))
        assertEquals("diet", nudge.actionRoute)
    }

    @Test
    fun `no NUDGE when burned plus consumed meets target`() {
        val state = UserActivityState(
            todayCaloriesBurned = 700,
            todayCaloriesConsumed = 2000,
            targetCalories = 2500
        )
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.type == CoachInsightType.NUDGE })
    }

    @Test
    fun `WARNING shown after exactly 3 missed sessions`() {
        val state = UserActivityState(missedSessionsInRow = 3)
        val insights = engine.generateInsights(state)
        val warning = insights.find { it.type == CoachInsightType.WARNING && it.actionRoute == "workouts" }
        assertNotNull(warning)
        assertTrue(warning!!.message.contains("3"))
    }

    @Test
    fun `no missed-session WARNING before 3 misses`() {
        val state = UserActivityState(missedSessionsInRow = 2)
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.actionRoute == "workouts" })
    }

    @Test
    fun `CELEBRATION shown at 7-day streak`() {
        val state = UserActivityState(currentStreakDays = 7)
        val insights = engine.generateInsights(state)
        val cel = insights.find { it.type == CoachInsightType.CELEBRATION }
        assertNotNull(cel)
        assertTrue(cel!!.message.contains("7"))
    }

    @Test
    fun `CELEBRATION shown at 14-day streak`() {
        val state = UserActivityState(currentStreakDays = 14)
        val insights = engine.generateInsights(state)
        assertNotNull(insights.find { it.type == CoachInsightType.CELEBRATION })
    }

    @Test
    fun `no CELEBRATION at non-multiple of 7`() {
        val state = UserActivityState(currentStreakDays = 5)
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.type == CoachInsightType.CELEBRATION })
    }

    @Test
    fun `protein window WARNING when workout recent but no meal logged after`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 30 * 60 * 1000L,
            lastMealTimestampMs = now - 2 * 60 * 60 * 1000L
        )
        val insights = engine.generateInsights(state)
        val warning = insights.find { it.type == CoachInsightType.WARNING && it.actionRoute == "diet" }
        assertNotNull(warning)
        assertTrue(warning!!.message.contains("protein"))
    }

    @Test
    fun `no protein WARNING when meal logged after workout`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 60 * 60 * 1000L,
            lastMealTimestampMs = now - 30 * 60 * 1000L
        )
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.actionRoute == "diet" })
    }

    @Test
    fun `no protein WARNING when workout was more than 2 hours ago`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 3 * 60 * 60 * 1000L,
            lastMealTimestampMs = null
        )
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.actionRoute == "diet" })
    }
}
