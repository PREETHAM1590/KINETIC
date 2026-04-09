package com.kinetic.app.data.store

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserActivityStoreTest {

    private lateinit var store: UserActivityStore

    @Before
    fun setUp() {
        store = UserActivityStore()
    }

    @Test
    fun `recordWorkoutCompleted accumulates calories burned`() {
        store.recordWorkoutCompleted(300)
        store.recordWorkoutCompleted(180)
        assertEquals(480, store.state.value.todayCaloriesBurned)
    }

    @Test
    fun `recordWorkoutCompleted increments streak only once per day`() {
        store.recordWorkoutCompleted(200)
        store.recordWorkoutCompleted(200)
        // Both calls happen in the same test run (same millisecond day), so streak = 1
        assertEquals(1, store.state.value.currentStreakDays)
    }

    @Test
    fun `recordWorkoutCompleted resets missedSessionsInRow to 0`() {
        store.recordSessionMissed()
        store.recordSessionMissed()
        store.recordWorkoutCompleted(200)
        assertEquals(0, store.state.value.missedSessionsInRow)
    }

    @Test
    fun `recordWorkoutCompleted sets lastWorkoutTimestampMs to non-null`() {
        store.recordWorkoutCompleted(100)
        assertNotNull(store.state.value.lastWorkoutTimestampMs)
    }

    @Test
    fun `recordMealLogged accumulates calories consumed`() {
        store.recordMealLogged(600)
        store.recordMealLogged(450)
        assertEquals(1050, store.state.value.todayCaloriesConsumed)
    }

    @Test
    fun `recordMealLogged does not affect todayCaloriesBurned`() {
        store.recordMealLogged(500)
        assertEquals(0, store.state.value.todayCaloriesBurned)
    }

    @Test
    fun `recordSessionMissed increments missedSessionsInRow`() {
        store.recordSessionMissed()
        store.recordSessionMissed()
        assertEquals(2, store.state.value.missedSessionsInRow)
    }

    @Test
    fun `setTargetCalories updates targetCalories`() {
        store.setTargetCalories(3000)
        assertEquals(3000, store.state.value.targetCalories)
    }

    @Test
    fun `recordWorkoutCompleted does not affect todayCaloriesConsumed`() {
        store.recordWorkoutCompleted(500)
        assertEquals(0, store.state.value.todayCaloriesConsumed)
    }

    @Test
    fun `initial state has null timestamps`() {
        assertNull(store.state.value.lastWorkoutTimestampMs)
        assertNull(store.state.value.lastMealTimestampMs)
    }
}
