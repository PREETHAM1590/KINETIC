# KINETIC — Android Kotlin App

## Stack
- Kotlin + Jetpack Compose
- Material3, Navigation Compose
- minSdk 26, targetSdk 35
- Package: com.kinetic.app

## Brand
- Lime accent: #D1FF26
- Background: #09090b
- Surface1: #18181b
- TextPrimary: #f4f4f5
- TextMuted: #71717a
- Fonts: Lexend (headlines, Black/ExtraBold, Italic) + Inter (body)

## Architecture
- Screens: ui/screens/
- Components: ui/components/
- Theme: ui/theme/ (Color.kt, Type.kt, Theme.kt)
- Navigation: ui/navigation/ (Screen.kt, NavGraph.kt)

## Screens Built
- WorkoutsScreen ✅
- DietScreen ✅
- ReportsScreen ✅
- MembershipScreen ✅
- ProfileScreen ✅
- BookClassScreen ✅
- SupportScreen ✅
- SettingsScreen ✅

## Screens To Build
- OnboardingScreen
- WorkoutDetailScreen
- ActiveWorkoutScreen (timer, sets, reps)
- MealDetailScreen
- AICalorieScannerScreen

## Code Rules
- Always use KineticCard, LimeBadge, StartButton from CommonComponents.kt
- Never hardcode colors — use Color.kt tokens
- Every screen must scroll vertically via rememberScrollState()
- Use FontStyle.Italic + FontWeight.Black for all headlines
- FAB only on Workouts and BookClass screens

---

## 3. The Power Prompts — Copy These Exactly

### Build a complete new screen in one shot:
```
Build WorkoutDetailScreen.kt for KINETIC. When user taps a workout card 
it navigates here. Show: large hero with workout name in Lexend Black Italic, 
difficulty badge, duration/calories stats row, animated timer ring (Canvas), 
exercise list with sets/reps/weight inputs, a START WORKOUT lime FAB. 
Follow all patterns from WorkoutsScreen.kt exactly.
```

### Fix a bug without explaining:
```
The MembershipScreen tier cards are not showing the CURRENT badge 
on the Elite tier. Fix it. Don't explain, just fix the file.
```

### Add feature to existing screen:
```
Add a search bar to WorkoutsScreen.kt that filters the strength workout 
cards by title in real time. Use a TextField styled with Surface2 background, 
Lime focus border, search icon in Lime. Place it between HIIT and Strength sections.
```

### Wire up navigation:
```
Add navigation from every workout card in WorkoutsScreen to WorkoutDetailScreen, 
passing the workout title as a nav argument. Update NavGraph.kt and 
Screen.kt automatically.
```

### Add ViewModel + state:
```
Create WorkoutViewModel.kt with: current workout state, timer logic using 
coroutines (start/pause/stop), calories burned calculation, set/rep counter. 
Wire it into WorkoutsScreen and WorkoutDetailScreen using collectAsState().
```

---

## 4. Multi-File Operations — Claude Code's Superpower

Claude Code can edit **multiple files simultaneously**. Use this:
```
Refactor the entire app to add a ViewModel layer:
- Create ViewModels for each screen
- Move all data/state out of Composables
- Add a data/ package with Repository classes
- Update all screens to use viewModel()
Do it all at once.
```
```
Add dark/light theme toggle to the app:
- Update Theme.kt with both schemes
- Add ThemeViewModel.kt
- Wire toggle in SettingsScreen
- Persist preference with DataStore
Update all affected files.
```

---

## 5. The Iteration Loop — 100x Speed

Instead of writing code yourself, use this loop:
```
# Step 1: Describe what's wrong visually
"The Diet screen macro ring looks flat. 
 Make it animate from 0 to 82% on first composition 
 using animateFloatAsState with a 1.2s EaseOutCubic easing."

# Step 2: Ask for the next screen immediately after
"Good. Now build the ActiveWorkoutScreen with a 
 full-screen timer, exercise queue, and rest timer."

# Step 3: Connect everything
"Now wire these 3 screens together with proper 
 back stack handling and shared element transitions." use plugins mcp and agents also
```
