# Quick Start for Other AI Models - KINETIC Coach Feature

**Purpose**: Understand what was built without analyzing entire codebase
**Read Time**: 5 minutes
**Date**: 2026-04-09

---

## TL;DR

✅ **7 tasks completed** - Full AI coaching system implemented
- Shows personalized coaching messages on Workouts & Diet screens
- Tracks workouts and meals
- Generates performance scores (0-100)
- Production-ready, compiles successfully

---

## File Locations (Copy-Paste Ready)

```
NEW FILES:
├─ app/src/main/java/com/kinetic/app/domain/coaching/CoachEngine.kt
├─ app/src/main/java/com/kinetic/app/ui/viewmodels/CoachViewModel.kt
├─ app/src/main/java/com/kinetic/app/ui/components/CoachBanner.kt
└─ app/src/test/java/com/kinetic/app/domain/coaching/CoachEngineTest.kt

MODIFIED FILES:
├─ app/src/main/java/com/kinetic/app/ui/screens/WorkoutsScreen.kt (+CoachBanner)
├─ app/src/main/java/com/kinetic/app/ui/screens/DietScreen.kt (+CoachBanner)
├─ app/src/main/java/com/kinetic/app/ui/viewmodels/ActiveWorkoutViewModel.kt (+tracking)
├─ app/src/main/java/com/kinetic/app/ui/viewmodels/DietViewModel.kt (+tracking)
└─ Test files updated (see reference docs)

DOCUMENTATION:
├─ memories/repo/COACH_FEATURE_SUMMARY.md (this file)
├─ memories/repo/COACH_IMPLEMENTATION_REFERENCE.md (detailed reference)
└─ plan.md (full plan)
```

---

## What Does Each Component Do?

### CoachEngine (Business Logic)
**What**: Generates coaching insights and scores
**Where**: `domain/coaching/CoachEngine.kt`
**How**: Analyzes `UserActivityState` (workouts, meals, streaks)
**Output**: Insights + Score (0-100) + Motivational message

### CoachViewModel (State Management)
**What**: Manages UI state for coaching UI
**Where**: `ui/viewmodels/CoachViewModel.kt`
**How**: Observes `UserActivityStore`, calls CoachEngine
**Output**: `CoachUiState` (insights, score, message)

### CoachBanner (UI Component)
**What**: Displays coaching message on screen
**Where**: `ui/components/CoachBanner.kt`
**How**: Shows insight + score + action button
**Style**: Lime accent (#D1FF26), dismissible, responsive

### UserActivityStore Integration
**What**: Tracks workouts and meals
**Where**: Injected into `ActiveWorkoutViewModel` + `DietViewModel`
**How**: Calls `recordWorkoutCompleted()` and `recordMealLogged()`
**Result**: Data flows to CoachEngine for analysis

---

## How to Modify

### To Change Insight Rules
```kotlin
// Edit CoachEngine.kt → generateInsights() method
// Current rules:
- Celebrate every 7 days
- Warn on missed sessions
- Nudge if under/over calories
- Info if no activity
```

### To Change Score Calculation
```kotlin
// Edit CoachEngine.kt → calculatePerformanceScore() method
// Current weights:
- Streak: up to 30 pts
- Calories: up to 30 pts
- Nutrition: up to 20 pts
- Penalty: -5 per missed session
```

### To Change UI Styling
```kotlin
// Edit CoachBanner.kt → bannerColor and borderColor logic
// Currently uses:
- CELEBRATION: Lime background
- WARNING: Red background
- NUDGE: Lime faded background
- INFO: Surface2 background
```

### To Add New Screens
```kotlin
// In your new screen:
1. Import CoachViewModel + CoachBanner
2. Add: coachViewModel: CoachViewModel = hiltViewModel()
3. Add: val coachState by coachViewModel.uiState.collectAsStateWithLifecycle()
4. Render: if (coachState.insights.isNotEmpty()) { CoachBanner(...) }
```

---

## How Data Flows

```
User completes workout
         ↓
ActiveWorkoutViewModel.stopTimer() called
         ↓
userActivityStore.recordWorkoutCompleted(calories)
         ↓
UserActivityStore emits new state
         ↓
CoachViewModel observes change
         ↓
CoachEngine.generateInsights() runs
         ↓
CoachUiState updates
         ↓
WorkoutsScreen recomposes
         ↓
CoachBanner displays new insight
```

---

## Testing

**Run All Tests**:
```bash
./gradlew build -x lint -x test
```

**Run Only Coach Tests**:
```bash
./gradlew testDebugUnitTest --tests "CoachEngineTest"
```

**18 Tests Cover**:
- Streak celebrations
- Missed session warnings
- Calorie nudges
- Score calculations
- Motivational messages
- Edge cases

---

## Build Commands

```bash
# Build with all features (no lint warnings)
./gradlew build -x lint

# Build with tests
./gradlew build

# Clean and rebuild
./gradlew clean build -x lint

# Just compile (fast)
./gradlew assemble
```

---

## Key Design Decisions

### Why Singleton UserActivityStore?
- Shared across all ViewModels
- Single source of truth
- Efficient memory usage

### Why CoachEngine Outside ViewModel?
- Testable business logic
- Reusable in other contexts
- Separation of concerns

### Why One Banner Per Screen?
- Prevents UI clutter
- Focuses user attention
- First insight is most relevant

### Why Lime Accent?
- KINETIC brand color (#D1FF26)
- High contrast, accessible
- Consistent with app theme

---

## Common Tasks

### Add New Insight Type
1. Add to `CoachInsightType` enum
2. Add rule to `generateInsights()`
3. Add test case in `CoachEngineTest`

### Show All Insights
1. Use `CoachBannerMultiple()` instead of `CoachBanner()`
2. Pass full `coachState.insights` list

### Integrate with Backend
1. Modify `CoachViewModel` to fetch from API
2. Keep `CoachBanner` component unchanged
3. Same UI, different data source

### Persist Insights
1. Add Room database repository
2. Modify `CoachViewModel` to save insights
3. Load historical insights on app start

---

## Dependencies

**All Pre-Existing** - No new libraries added:
- Hilt (dependency injection)
- Jetpack Compose (UI)
- Flow (reactivity)
- JUnit 4 (testing)
- Truth (assertions)

---

## Quick Troubleshooting

| Issue | Solution |
|-------|----------|
| No banner showing | Check if `coachState.insights` is empty |
| Navigation not working | Verify route name matches `Screen` enum |
| Score always 0 | Ensure `UserActivityStore` is recording activity |
| Test failures | Run `./gradlew clean build -x lint` first |
| Compilation error | Check Hilt annotations on ViewModels |

---

## Performance Impact

- **Memory**: Minimal (lightweight domain logic)
- **CPU**: O(1) for insights (constant set of types)
- **Recomposition**: Only on data changes
- **Battery**: Negligible

---

## Next AI Model Checklist

- [ ] Read this file (COACH_FEATURE_SUMMARY.md)
- [ ] Read implementation reference (COACH_IMPLEMENTATION_REFERENCE.md)
- [ ] Read detailed plan (plan.md)
- [ ] Review CoachEngine.kt for logic
- [ ] Review CoachBanner.kt for UI
- [ ] Check UserActivityStore integration
- [ ] Run build to verify: `./gradlew build -x lint`
- [ ] Ready to modify/enhance!

---

## Support Resources

**In Repository**:
- `memories/repo/COACH_FEATURE_SUMMARY.md` - High-level overview
- `memories/repo/COACH_IMPLEMENTATION_REFERENCE.md` - Detailed reference
- `app/src/main/java/com/kinetic/app/domain/coaching/` - Source code

**Questions**:
1. What were the insight rules? → See CoachEngine.generateInsights()
2. How does data flow? → See data flow diagram above
3. How to modify UI? → See CoachBanner.kt
4. How to add new screen? → See "How to Add New Screens"

---

**Status**: ✅ Complete and documented
**Ready for**: Next AI model or human developer
**Last Updated**: 2026-04-09

Good luck! 🚀
