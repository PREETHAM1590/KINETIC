package com.kinetic.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Consent : Screen("consent")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")

    object Workouts : Screen("workouts")
    object Diet : Screen("diet")
    object Reports : Screen("reports")
    object Membership : Screen("membership")
    object Profile : Screen("profile")
    object BookClass : Screen("book_class")
    object Support : Screen("support")
    object Settings : Screen("settings")
    object AICalorieScanner : Screen("ai_calorie_scanner")
    object ActiveWorkout : Screen("active_workout/{workoutId}") {
        fun createRoute(workoutId: String) = "active_workout/$workoutId"
    }
    object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }
    object MealDetail : Screen("meal_detail/{mealId}") {
        fun createRoute(mealId: String) = "meal_detail/$mealId"
    }
    object PrivacyPolicy : Screen("privacy_policy")
    object DataDeletion : Screen("data_deletion")
}
