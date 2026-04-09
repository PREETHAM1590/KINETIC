package com.kinetic.app.data.models

data class MealItem(
    val id: String,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val isHealthy: Boolean = true
)

data class DailyNutrition(
    val totalCalories: Int,
    val targetCalories: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFats: Int,
    val meals: List<MealItem>
)

data class MealDetail(
    val id: String,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val tag: String,
    val proteinPercent: Float,
    val carbsPercent: Float,
    val fatsPercent: Float,
    val ingredients: List<String>,
    val steps: List<String>
)
