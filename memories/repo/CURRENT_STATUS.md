# KINETIC — AI Handoff Status File

> **READ THIS FIRST** before touching any code.
> Works for Claude, Copilot, Gemini, or any AI model picking up this project.
> **Token tip:** Read ONLY this file + the specific task section from the plan. Do NOT scan the full codebase.

---

## PROJECT
- **App:** Android Kotlin (Hilt, Jetpack Compose, Material3, StateFlow)
- **Package:** `com.kinetic.app`
- **Min SDK:** 26 | **Target SDK:** 35
- **Tests:** JUnit 4 + `org.junit.Assert.*` (no Truth library)
- **Run tests:** `./gradlew :app:testDebugUnitTest`
- **Compile check:** `./gradlew :app:compileDebugKotlin 2>&1 | grep "error:"`

---

## ACTIVE BRANCH
`feature/kinetic-core-intelligence-layer`

---

## PLAN 1 STATUS — Core Intelligence Layer
**Plan file:** `docs/superpowers/plans/2026-04-09-kinetic-core-intelligence-layer.md`

| # | Task | Status | Commit |
|---|------|--------|--------|
| 1 | UserActivityStore singleton | ✅ DONE | 7db048d |
| 2 | CoachInsight model + enum | ✅ DONE | cabdf1d |
| 3 | UserActivityStore unit tests | ✅ DONE | 852ee67 |
| 4 | CoachEngine (TDD, 4 rules) | ✅ DONE | b49ceb1 |
| 5 | CoachViewModel | ✅ DONE | ab8b18b |
| 6 | CoachBanner composable | ✅ DONE | ff58deb |
| 7 | Wire store → ActiveWorkoutViewModel | ✅ DONE | ff8aa11 |
| 8 | Wire store → DietViewModel + logMeal() | ✅ DONE | 70707e3 |
| 9 | CoachBanner on WorkoutsScreen | ✅ DONE | ff8aa11 |
| 10 | CoachBanner on DietScreen | ✅ DONE | 70707e3 |

**Build:** ✅ SUCCESSFUL | **Tests:** ✅ ALL PASS | **Uncommitted changes:** None

---

## WHAT WAS BUILT (Plan 1)

### Architecture
```
UserActivityStore (@Singleton, StateFlow)
    ↑ written by ActiveWorkoutViewModel.completeExercise()
    ↑ written by DietViewModel.logMeal()
    ↓
CoachEngine (@Singleton, pure logic)
    → generateInsights(UserActivityState): List<CoachInsight>
    ↓
CoachViewModel (@HiltViewModel)
    → topInsight: StateFlow<CoachInsight?>
    ↓
CoachBanner (@Composable, animated)
    → shown on WorkoutsScreen + DietScreen below headline
```

### Files created
```
app/src/main/java/com/kinetic/app/data/store/UserActivityStore.kt
app/src/main/java/com/kinetic/app/data/models/CoachModels.kt
app/src/main/java/com/kinetic/app/domain/CoachEngine.kt
app/src/main/java/com/kinetic/app/ui/viewmodels/CoachViewModel.kt
app/src/main/java/com/kinetic/app/ui/components/CoachBanner.kt
app/src/test/java/com/kinetic/app/data/store/UserActivityStoreTest.kt
app/src/test/java/com/kinetic/app/domain/CoachEngineTest.kt
```

### Files modified
```
ActiveWorkoutViewModel.kt  → injects UserActivityStore, calls recordWorkoutCompleted() on finish
DietViewModel.kt           → injects UserActivityStore, exposes logMeal(calories: Int)
WorkoutsScreen.kt          → CoachBanner below ScreenHeadline
DietScreen.kt              → CoachBanner below ScreenHeadline
WorkoutRepository.kt       → FakeWorkoutRepository made open (for test subclassing)
BookClassViewModel.kt      → removed invalid distinctUntilChanged() on StateFlow
```

### Key types (copy-paste ready for subagents)
```kotlin
// UserActivityState — in data/store/UserActivityStore.kt
data class UserActivityState(
    val todayCaloriesBurned: Int = 0,
    val todayCaloriesConsumed: Int = 0,
    val targetCalories: Int = 2500,      // overridden post-onboarding
    val currentStreakDays: Int = 0,
    val lastWorkoutTimestampMs: Long? = null,
    val lastMealTimestampMs: Long? = null,
    val missedSessionsInRow: Int = 0
)

// CoachInsight — in data/models/CoachModels.kt
data class CoachInsight(
    val message: String,
    val actionLabel: String? = null,
    val actionRoute: String? = null,
    val type: CoachInsightType = CoachInsightType.INFO
)
enum class CoachInsightType { INFO, NUDGE, WARNING, CELEBRATION }
```

### CoachEngine rules
1. **NUDGE** — `todayCaloriesBurned > 0` AND deficit > 0 → "You burned X cal..." → route `"diet"`
2. **WARNING** — `missedSessionsInRow >= 3` → "You've missed N sessions..." → route `"workouts"`
3. **CELEBRATION** — `currentStreakDays % 7 == 0` → "N-day streak!" → no route
4. **WARNING** — workout < 2h ago AND no meal after → "Recovery window..." → route `"diet"`

---

## NEXT PLANS (not started)

### Plan 2 — Predictive Reports + Behavior-Based Membership
- Flip ReportsScreen from past→future ("You're on track to hit 10k cal this month")
- Add streak progress bar + streak-locked features to MembershipScreen

### Plan 3 — Active Workout Intelligence
- Adaptive rest timer (based on set completion quality)
- Last Session Ghost (show previous session numbers in muted text)

### Plan 4 — Diet Reframe + Onboarding Redesign
- Camera-first Diet screen (scan = primary action)
- Goal-orientation-first Onboarding (performance vs aesthetics vs health)

---

## HOW TO ADD CoachBanner TO A NEW SCREEN

```kotlin
// 1. Add to composable params:
coachViewModel: CoachViewModel = hiltViewModel()

// 2. Collect state:
val topInsight by coachViewModel.topInsight.collectAsStateWithLifecycle()

// 3. Render below ScreenHeadline:
CoachBanner(
    insight = topInsight,
    onActionClick = { route -> navController.navigate(route) },
    modifier = Modifier.padding(bottom = 8.dp)
)
```

---

## REPO STRUCTURE QUICK REF
```
ui/screens/      — Composable screens
ui/components/   — Shared UI (KineticCard, LimeBadge, CoachBanner, etc.)
ui/viewmodels/   — @HiltViewModel classes
ui/navigation/   — Screen.kt (routes), NavGraph.kt
data/models/     — Data classes
data/store/      — UserActivityStore (singleton cross-screen state)
data/repository/ — Repository interfaces + Fake impls
domain/          — Pure business logic (CoachEngine)
di/              — AppModule.kt (Hilt bindings)
```

## BRAND TOKENS (never hardcode colors)
```kotlin
import com.kinetic.app.ui.theme.*
// Lime, LimeDark, Background, Surface1, Surface2, TextPrimary, TextMuted, Error
```
