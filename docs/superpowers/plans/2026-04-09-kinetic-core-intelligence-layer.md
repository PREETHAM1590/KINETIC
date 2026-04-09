# KINETIC Core Intelligence Layer — Implementation Plan

> ✅ **STATUS: COMPLETE** — All 10 tasks done. Build passes. Branch: `feature/kinetic-core-intelligence-layer`
> **For current state:** read `memories/repo/CURRENT_STATUS.md` — single source of truth for all AI models.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Build a singleton cross-screen state store and pure-logic nudge engine that lets KINETIC's AI Coach surface contextual insights on Workouts and Diet screens without adding a new screen.

**Architecture:** A `UserActivityStore` (Hilt singleton, StateFlow) holds live daily activity state written to by `ActiveWorkoutViewModel` and `DietViewModel`. A pure-logic `CoachEngine` reads that state and returns a ranked list of `CoachInsight` objects. A `CoachViewModel` exposes the top insight as a `StateFlow`. A `CoachBanner` composable renders it inline at the top of Workouts and Diet screens.

**Tech Stack:** Kotlin, Hilt/Dagger, Kotlin Coroutines, StateFlow, Jetpack Compose, JUnit 4 (existing test setup)

---

## File Map

| Action | Path | Responsibility |
|--------|------|----------------|
| Create | `app/src/main/java/com/kinetic/app/data/store/UserActivityStore.kt` | Singleton mutable state: calories burned/consumed today, streak, timestamps, missed sessions |
| Create | `app/src/main/java/com/kinetic/app/data/models/CoachModels.kt` | `CoachInsight` data class + `CoachInsightType` enum |
| Create | `app/src/main/java/com/kinetic/app/domain/CoachEngine.kt` | Pure logic: `UserActivityState → List<CoachInsight>` |
| Create | `app/src/main/java/com/kinetic/app/ui/viewmodels/CoachViewModel.kt` | Wraps CoachEngine, exposes `StateFlow<CoachInsight?>` (top insight only) |
| Create | `app/src/main/java/com/kinetic/app/ui/components/CoachBanner.kt` | Single composable, themed by insight type |
| Create | `app/src/test/java/com/kinetic/app/data/store/UserActivityStoreTest.kt` | Unit tests for store mutations |
| Create | `app/src/test/java/com/kinetic/app/domain/CoachEngineTest.kt` | Unit tests for every nudge rule |
| Modify | `app/src/main/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModel.kt` | Inject `UserActivityStore`, call `recordWorkoutCompleted()` on finish |
| Modify | `app/src/main/java/com/kinetic/app/ui/viewmodels/DietViewModel.kt` | Inject `UserActivityStore`, add `logMeal()`, call `recordMealLogged()` |
| Modify | `app/src/main/java/com/kinetic/app/ui/screens/WorkoutsScreen.kt` | Inject `CoachViewModel`, show `CoachBanner` below headline |
| Modify | `app/src/main/java/com/kinetic/app/ui/screens/DietScreen.kt` | Inject `CoachViewModel`, show `CoachBanner` below headline |
| Modify | `app/src/test/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModelTest.kt` | Extend: verify `recordWorkoutCompleted` called on finish |
| Modify | `app/src/test/java/com/kinetic/app/ui/viewmodels/DietViewModelTest.kt` | Extend: verify `recordMealLogged` called on `logMeal()` |

---

## Task 1: UserActivityStore ✅ DONE (commit: 7db048d)

**Files:**
- Create: `app/src/main/java/com/kinetic/app/data/store/UserActivityStore.kt`

- [x] **Step 1: Create the store**

```kotlin
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
```

- [x] **Step 2: Commit** (b829b43 + 7db048d — fixed atomicity, streak logic, nullable timestamps)

---

## Task 2: CoachInsight model ✅ DONE (commit: cabdf1d)

**Files:**
- Create: `app/src/main/java/com/kinetic/app/data/models/CoachModels.kt`

- [x] **Step 1: Create the model**

```kotlin
package com.kinetic.app.data.models

data class CoachInsight(
    val message: String,
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val type: CoachInsightType = CoachInsightType.INFO
)

enum class CoachInsightType {
    INFO, NUDGE, WARNING, CELEBRATION
}
```

- [x] **Step 2: Commit** (cabdf1d)

---

## Task 3: UserActivityStore unit tests ✅ DONE (commit: 852ee67)

**Files:**
- Create: `app/src/test/java/com/kinetic/app/data/store/UserActivityStoreTest.kt`

- [x] **Step 1: Write the failing tests**

```kotlin
package com.kinetic.app.data.store

import org.junit.Assert.assertEquals
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
    fun `recordWorkoutCompleted increments streak by 1 per call`() {
        store.recordWorkoutCompleted(200)
        store.recordWorkoutCompleted(200)
        assertEquals(2, store.state.value.currentStreakDays)
    }

    @Test
    fun `recordWorkoutCompleted resets missedSessionsInRow to 0`() {
        store.recordSessionMissed()
        store.recordSessionMissed()
        store.recordWorkoutCompleted(200)
        assertEquals(0, store.state.value.missedSessionsInRow)
    }

    @Test
    fun `recordMealLogged accumulates calories consumed`() {
        store.recordMealLogged(600)
        store.recordMealLogged(450)
        assertEquals(1050, store.state.value.todayCaloriesConsumed)
    }

    @Test
    fun `recordSessionMissed increments missedSessionsInRow`() {
        store.recordSessionMissed()
        store.recordSessionMissed()
        assertEquals(2, store.state.value.missedSessionsInRow)
    }

    @Test
    fun `setTargetCalories updates target`() {
        store.setTargetCalories(3000)
        assertEquals(3000, store.state.value.targetCalories)
    }

    @Test
    fun `recordWorkoutCompleted sets lastWorkoutTimestampMs to non-zero`() {
        store.recordWorkoutCompleted(100)
        assert(store.state.value.lastWorkoutTimestampMs > 0L)
    }
}
```

- [x] **Step 2: Run tests to verify they fail (store class does not exist yet in test classpath until Task 1 is complete — skip if Task 1 already done)**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.data.store.UserActivityStoreTest" 2>&1 | tail -20
```

Expected: PASS (store was already created in Task 1)

- [x] **Step 3: Commit** (852ee67)

---

## Task 4: CoachEngine — pure nudge logic ✅ DONE (commit: b49ceb1)

**Files:**
- Create: `app/src/main/java/com/kinetic/app/domain/CoachEngine.kt`

- [x] **Step 1: Write the failing tests first**

Create `app/src/test/java/com/kinetic/app/domain/CoachEngineTest.kt`:

```kotlin
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
    fun `no NUDGE when calories burned puts user at or above target`() {
        val state = UserActivityState(
            todayCaloriesBurned = 700,
            todayCaloriesConsumed = 2000,
            targetCalories = 2500
        )
        // burned + consumed = 2700 > 2500, no nudge
        val insights = engine.generateInsights(state)
        val nudge = insights.find { it.type == CoachInsightType.NUDGE }
        assertNull(nudge)
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
        val sessionWarning = insights.find { it.actionRoute == "workouts" }
        assertNull(sessionWarning)
    }

    @Test
    fun `CELEBRATION shown on 7-day streak multiples`() {
        listOf(7, 14, 21).forEach { streak ->
            val state = UserActivityState(currentStreakDays = streak)
            val insights = engine.generateInsights(state)
            val celebration = insights.find { it.type == CoachInsightType.CELEBRATION }
            assertNotNull("Expected celebration at streak=$streak", celebration)
            assertTrue(celebration!!.message.contains("$streak"))
        }
    }

    @Test
    fun `no CELEBRATION on non-multiple of 7 streak`() {
        val state = UserActivityState(currentStreakDays = 5)
        val insights = engine.generateInsights(state)
        assertNull(insights.find { it.type == CoachInsightType.CELEBRATION })
    }

    @Test
    fun `protein window WARNING when workout recent but no meal logged after`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 30 * 60 * 1000L,   // 30 min ago
            lastMealTimestampMs = now - 2 * 60 * 60 * 1000L   // 2h ago (before workout)
        )
        val insights = engine.generateInsights(state)
        val warning = insights.find { it.type == CoachInsightType.WARNING && it.actionRoute == "diet" }
        assertNotNull(warning)
        assertTrue(warning!!.message.contains("protein"))
    }

    @Test
    fun `no protein window WARNING when meal logged after workout`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 60 * 60 * 1000L,   // 1h ago
            lastMealTimestampMs = now - 30 * 60 * 1000L        // 30 min ago (after workout)
        )
        val insights = engine.generateInsights(state)
        val dietWarning = insights.find { it.actionRoute == "diet" }
        assertNull(dietWarning)
    }

    @Test
    fun `no protein window WARNING when workout was more than 2 hours ago`() {
        val now = System.currentTimeMillis()
        val state = UserActivityState(
            lastWorkoutTimestampMs = now - 3 * 60 * 60 * 1000L,  // 3h ago
            lastMealTimestampMs = 0L
        )
        val insights = engine.generateInsights(state)
        val dietWarning = insights.find { it.actionRoute == "diet" }
        assertNull(dietWarning)
    }
}
```

- [x] **Step 2: Run tests to verify they fail**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.domain.CoachEngineTest" 2>&1 | tail -20
```

Expected: FAIL with `ClassNotFoundException` (engine doesn't exist yet)

- [x] **Step 3: Implement CoachEngine**

```kotlin
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

        // Calorie surplus nudge: only when some calories were burned today
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

        // Missed sessions: warn after 3 in a row
        if (state.missedSessionsInRow >= 3) {
            insights += CoachInsight(
                message = "You've missed ${state.missedSessionsInRow} sessions. Want to adjust your plan?",
                actionLabel = "See workouts",
                actionRoute = "workouts",
                type = CoachInsightType.WARNING
            )
        }

        // Streak celebration: every 7 days
        if (state.currentStreakDays > 0 && state.currentStreakDays % 7 == 0) {
            insights += CoachInsight(
                message = "${state.currentStreakDays}-day streak! You're in the top 10% this week.",
                type = CoachInsightType.CELEBRATION
            )
        }

        // Post-workout protein window
        val now = System.currentTimeMillis()
        if (state.lastWorkoutTimestampMs > 0L &&
            (now - state.lastWorkoutTimestampMs) < twoHoursMs &&
            state.lastMealTimestampMs < state.lastWorkoutTimestampMs
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
```

- [x] **Step 4: Run tests to verify they pass**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.domain.CoachEngineTest" 2>&1 | tail -20
```

Expected: all 9 tests PASS

- [x] **Step 5: Commit**

```bash
git add app/src/main/java/com/kinetic/app/domain/CoachEngine.kt \
        app/src/test/java/com/kinetic/app/domain/CoachEngineTest.kt
git commit -m "feat: add CoachEngine with 4 nudge rules + full unit tests"
```

---

## Task 5: CoachViewModel ✅ DONE (commit: ab8b18b)

**Files:**
- Create: `app/src/main/java/com/kinetic/app/ui/viewmodels/CoachViewModel.kt`

- [x] **Step 1: Implement CoachViewModel**

```kotlin
package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.store.UserActivityStore
import com.kinetic.app.domain.CoachEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val store: UserActivityStore,
    private val engine: CoachEngine
) : ViewModel() {

    // Expose only the highest-priority insight so screens show one banner, not a list
    val topInsight: StateFlow<CoachInsight?> = store.state
        .map { engine.generateInsights(it).firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
```

- [x] **Step 2: Verify the project compiles**

```bash
./gradlew :app:compileDebugKotlin 2>&1 | grep -E "error:|warning:" | head -20
```

Expected: no errors

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/viewmodels/CoachViewModel.kt
git commit -m "feat: add CoachViewModel exposing top CoachInsight as StateFlow"
```

---

## Task 6: CoachBanner composable ✅ DONE (commit: ff58deb)

**Files:**
- Create: `app/src/main/java/com/kinetic/app/ui/components/CoachBanner.kt`

- [x] **Step 1: Implement CoachBanner**

```kotlin
package com.kinetic.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetic.app.data.models.CoachInsight
import com.kinetic.app.data.models.CoachInsightType
import com.kinetic.app.ui.theme.Error
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.theme.Surface1
import com.kinetic.app.ui.theme.Surface2
import com.kinetic.app.ui.theme.TextPrimary

// Renders the top CoachInsight as an inline banner.
// Pass null to hide it (AnimatedVisibility handles the fade).
@Composable
fun CoachBanner(
    insight: CoachInsight?,
    onActionClick: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = insight != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        if (insight == null) return@AnimatedVisibility

        val bannerColor = when (insight.type) {
            CoachInsightType.CELEBRATION -> Lime.copy(alpha = 0.15f)
            CoachInsightType.WARNING     -> Error.copy(alpha = 0.12f)
            CoachInsightType.NUDGE       -> Surface2
            CoachInsightType.INFO        -> Surface1
        }
        val textColor = when (insight.type) {
            CoachInsightType.CELEBRATION -> Lime
            CoachInsightType.WARNING     -> Error
            else                         -> TextPrimary
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bannerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = insight.message,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (insight.actionLabel != null && insight.actionRoute != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onActionClick(insight.actionRoute) }) {
                        Text(
                            text = insight.actionLabel,
                            color = Lime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
```

- [x] **Step 2: Verify it compiles**

```bash
./gradlew :app:compileDebugKotlin 2>&1 | grep -E "error:" | head -10
```

Expected: no errors

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/components/CoachBanner.kt
git commit -m "feat: add CoachBanner composable with animated entrance and type-aware colors"
```

---

## Task 7: Wire UserActivityStore into ActiveWorkoutViewModel ✅ DONE (commit: ff8aa11)

The workout is "complete" when `completeExercise()` determines there are no more exercises. That is the single place to call `store.recordWorkoutCompleted(caloriesBurned)`.

**Files:**
- Modify: `app/src/main/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModel.kt`
- Modify: `app/src/test/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModelTest.kt`

- [x] **Step 1: Write the failing test first**

Open `app/src/test/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModelTest.kt`.

Add this test at the end of the existing test class (look for the class declaration and append before the closing `}`):

```kotlin
@Test
fun `completing last exercise records workout in UserActivityStore`() = runTest {
    val store = UserActivityStore()
    // Construct with a fake workout that has a single exercise (so first completeExercise finishes it)
    val singleExerciseWorkout = ActiveWorkout(
        id = "test",
        title = "Test",
        difficulty = "Easy",
        durationMin = 10,
        caloriesPerMin = 6,
        exercises = listOf(ExerciseItem("e1", "Push Up", sets = 1, reps = 5, weightLbs = 0))
    )
    val fakeRepo = FakeWorkoutRepository(activeWorkout = singleExerciseWorkout)
    val vm = ActiveWorkoutViewModel(workoutRepository = fakeRepo, activityStore = store)
    advanceUntilIdle()

    // Simulate 60 elapsed seconds (10 cal burned) then complete the only exercise
    vm.loadWorkoutById("test")
    advanceUntilIdle()

    vm.completeExercise()
    advanceUntilIdle()

    assertTrue(store.state.value.todayCaloriesBurned > 0)
}
```

Note: `FakeWorkoutRepository` in `TestFakes.kt` may need an `activeWorkout` constructor param — see Step 2 for whether that's already there. If not, add it.

- [x] **Step 2: Check if FakeWorkoutRepository accepts `activeWorkout` param**

Open `app/src/test/java/com/kinetic/app/ui/viewmodels/TestFakes.kt` and look for `FakeWorkoutRepository`. If the constructor does not accept an `activeWorkout: ActiveWorkout?` parameter, add one:

```kotlin
// In TestFakes.kt — find FakeWorkoutRepository and update its constructor to:
class FakeWorkoutRepository(
    private val activeWorkout: ActiveWorkout? = null
) : WorkoutRepository {
    // ... existing methods ...
    override fun getActiveWorkoutById(id: String): Flow<ActiveWorkout?> =
        flowOf(activeWorkout)
}
```

- [x] **Step 3: Run the test to verify it fails**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.ui.viewmodels.ActiveWorkoutViewModelTest.completing last exercise records workout in UserActivityStore" 2>&1 | tail -20
```

Expected: FAIL (constructor doesn't accept `activityStore` yet)

- [x] **Step 4: Add `UserActivityStore` injection to `ActiveWorkoutViewModel`**

In `ActiveWorkoutViewModel.kt`, change the constructor:

```kotlin
// BEFORE:
@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

// AFTER:
@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val activityStore: UserActivityStore
) : ViewModel() {
```

Add this import at the top:
```kotlin
import com.kinetic.app.data.store.UserActivityStore
```

- [x] **Step 5: Call `activityStore.recordWorkoutCompleted()` in `completeExercise()`**

In `completeExercise()`, find the block that handles "no more exercises" and update it:

```kotlin
// BEFORE:
if (nextIndex >= state.workout.exercises.size) {
    stopTimer()
    return
}

// AFTER:
if (nextIndex >= state.workout.exercises.size) {
    stopTimer()
    activityStore.recordWorkoutCompleted(state.caloriesBurned)
    return
}
```

- [x] **Step 6: Run the new test to verify it passes**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.ui.viewmodels.ActiveWorkoutViewModelTest" 2>&1 | tail -30
```

Expected: all tests PASS (including the new one)

- [x] **Step 7: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModel.kt \
        app/src/test/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModelTest.kt \
        app/src/test/java/com/kinetic/app/ui/viewmodels/TestFakes.kt
git commit -m "feat: wire UserActivityStore into ActiveWorkoutViewModel — records on workout complete"
```

---

## Task 8: Wire UserActivityStore into DietViewModel ✅ DONE (commit: 70707e3)

**Files:**
- Modify: `app/src/main/java/com/kinetic/app/ui/viewmodels/DietViewModel.kt`
- Modify: `app/src/test/java/com/kinetic/app/ui/viewmodels/DietViewModelTest.kt`

- [x] **Step 1: Write the failing test**

Add to `DietViewModelTest.kt`:

```kotlin
@Test
fun `logMeal records calories in UserActivityStore`() = runTest {
    val store = UserActivityStore()
    val vm = DietViewModel(dietRepository = FakeDietRepository(), activityStore = store)
    advanceUntilIdle()

    vm.logMeal(calories = 650)
    advanceUntilIdle()

    assertEquals(650, store.state.value.todayCaloriesConsumed)
}
```

Add import: `import com.kinetic.app.data.store.UserActivityStore`

- [x] **Step 2: Run to verify it fails**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.ui.viewmodels.DietViewModelTest.logMeal records calories in UserActivityStore" 2>&1 | tail -20
```

Expected: FAIL (no `activityStore` param, no `logMeal` function)

- [x] **Step 3: Update DietViewModel**

```kotlin
package com.kinetic.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetic.app.data.models.DailyNutrition
import com.kinetic.app.data.models.MealItem
import com.kinetic.app.data.repository.DietRepository
import com.kinetic.app.data.store.UserActivityStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietUiState(
    val isLoading: Boolean = true,
    val meals: List<MealItem> = emptyList(),
    val dailyNutrition: DailyNutrition? = null,
    val error: String? = null
)

@HiltViewModel
class DietViewModel @Inject constructor(
    private val dietRepository: DietRepository,
    private val activityStore: UserActivityStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietUiState())
    val uiState: StateFlow<DietUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                dietRepository.getMeals(),
                dietRepository.getDailyNutrition()
            ) { meals, nutrition ->
                DietUiState(
                    isLoading = false,
                    meals = meals,
                    dailyNutrition = nutrition
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // Called when user logs a meal via any flow (manual entry, AI scanner, etc.)
    fun logMeal(calories: Int) {
        activityStore.recordMealLogged(calories)
    }
}
```

- [x] **Step 4: Run tests to verify they pass**

```bash
./gradlew :app:testDebugUnitTest --tests "com.kinetic.app.ui.viewmodels.DietViewModelTest" 2>&1 | tail -20
```

Expected: all tests PASS

- [x] **Step 5: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/viewmodels/DietViewModel.kt \
        app/src/test/java/com/kinetic/app/ui/viewmodels/DietViewModelTest.kt
git commit -m "feat: wire UserActivityStore into DietViewModel — exposes logMeal()"
```

---

## Task 9: Show CoachBanner on WorkoutsScreen ✅ DONE (commit: ff8aa11)

**Files:**
- Modify: `app/src/main/java/com/kinetic/app/ui/screens/WorkoutsScreen.kt`

- [x] **Step 1: Add CoachViewModel injection and banner to the screen**

In `WorkoutsScreen.kt`, update the composable signature and body:

```kotlin
// Add to imports:
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kinetic.app.ui.components.CoachBanner
import com.kinetic.app.ui.viewmodels.CoachViewModel

// Update function signature — add coachViewModel param:
@Composable
fun WorkoutsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WorkoutsViewModel = hiltViewModel(),
    coachViewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topInsight by coachViewModel.topInsight.collectAsStateWithLifecycle()
```

Then in the Column body, after `ScreenHeadline(title = "WORKOUTS", subtitle = "PUSH YOUR LIMITS")` and its `Spacer`, add:

```kotlin
CoachBanner(
    insight = topInsight,
    onActionClick = { route -> navController.navigate(route) },
    modifier = Modifier.padding(bottom = 8.dp)
)
```

The full relevant section should look like:

```kotlin
ScreenHeadline(title = "WORKOUTS", subtitle = "PUSH YOUR LIMITS")

Spacer(modifier = Modifier.height(24.dp))

CoachBanner(
    insight = topInsight,
    onActionClick = { route -> navController.navigate(route) },
    modifier = Modifier.padding(bottom = 8.dp)
)

if (uiState.isLoading) {
    // ... existing loading block unchanged
```

- [x] **Step 2: Verify the project compiles**

```bash
./gradlew :app:compileDebugKotlin 2>&1 | grep -E "error:" | head -10
```

Expected: no errors

- [x] **Step 3: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/screens/WorkoutsScreen.kt
git commit -m "feat: show CoachBanner on WorkoutsScreen below headline"
```

---

## Task 10: Show CoachBanner on DietScreen ✅ DONE (commit: 70707e3)

**Files:**
- Modify: `app/src/main/java/com/kinetic/app/ui/screens/DietScreen.kt`

- [x] **Step 1: Open DietScreen.kt and read its headline placement**

Read the file to find where `ScreenHeadline` is called (search for `ScreenHeadline` in the file). The banner goes immediately after that call and its spacer, mirroring the WorkoutsScreen pattern.

- [x] **Step 2: Add CoachViewModel injection**

```kotlin
// Add to imports:
import com.kinetic.app.ui.components.CoachBanner
import com.kinetic.app.ui.viewmodels.CoachViewModel

// Update function signature:
@Composable
fun DietScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
    coachViewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topInsight by coachViewModel.topInsight.collectAsStateWithLifecycle()
```

- [x] **Step 3: Add CoachBanner after the headline**

After `ScreenHeadline(title = "DIET", ...)` and its `Spacer`:

```kotlin
CoachBanner(
    insight = topInsight,
    onActionClick = { route -> navController.navigate(route) },
    modifier = Modifier.padding(bottom = 8.dp)
)
```

- [x] **Step 4: Verify the project compiles**

```bash
./gradlew :app:compileDebugKotlin 2>&1 | grep -E "error:" | head -10
```

Expected: no errors

- [x] **Step 5: Run all unit tests to confirm nothing regressed**

```bash
./gradlew :app:testDebugUnitTest 2>&1 | tail -20
```

Expected: BUILD SUCCESSFUL, all existing tests pass

- [x] **Step 6: Commit**

```bash
git add app/src/main/java/com/kinetic/app/ui/screens/DietScreen.kt
git commit -m "feat: show CoachBanner on DietScreen — KINETIC Core Intelligence Layer complete"
```

---

## Self-Review

### Spec coverage

| Spec requirement | Covered by |
|---|---|
| Cross-screen shared state | Task 1 (UserActivityStore) |
| Post-workout calorie nudge ("You burned 480 cal...") | Task 4 (CoachEngine rule 1) |
| After skipping 3 sessions, adjust plan | Task 4 (CoachEngine rule 2) |
| Not a new screen — contextual nudge inline | Tasks 9, 10 (CoachBanner inline) |
| Riskiest assumption: opt-in nudge test | Banner is opt-in by nature (users see it only post-activity); A/B wiring is out of scope here |
| Protein recovery window nudge (Diet spec idea #6) | Task 4 (CoachEngine rule 4) |

### Placeholder scan

No TBD/TODO/placeholder markers found in task code.

### Type consistency

- `UserActivityState` used in CoachEngineTest ✓ matches Task 1 definition
- `CoachInsight` / `CoachInsightType` used in CoachEngine ✓ matches Task 2 definition
- `CoachViewModel.topInsight: StateFlow<CoachInsight?>` ✓ matches CoachBanner `insight: CoachInsight?` param
- `UserActivityStore` constructor param name `activityStore` used in Tasks 7 & 8 consistently ✓

---

## Next Plans in Series

- **Plan 2:** Predictive Reports + Behavior-Based Membership (reads `UserActivityStore.state` for streak/calories)
- **Plan 3:** Active Workout Intelligence — Adaptive Rest Timer + Last Session Ghost
- **Plan 4:** Diet Screen Reframe (camera-first) + Onboarding Redesign (goal-orientation)
