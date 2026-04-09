package com.kinetic.app.data.fake

import com.kinetic.app.data.models.DailyNutrition
import com.kinetic.app.data.models.MealDetail
import com.kinetic.app.data.models.MealItem

object FakeDietData {
    val todayMeals = listOf(
        MealItem("m1", "Oatmeal & Berries", 320, 12, 52, 8, "Breakfast"),
        MealItem("m2", "Grilled Chicken Salad", 450, 38, 18, 22, "Lunch"),
        MealItem("m3", "Protein Shake", 180, 30, 8, 4, "Snack"),
        MealItem("m4", "Grilled Salmon Bowl", 442, 42, 28, 18, "Dinner")
    )

    val dailyNutrition = DailyNutrition(
        totalCalories = 1392,
        targetCalories = 2200,
        totalProtein = 122,
        totalCarbs = 106,
        totalFats = 52,
        meals = todayMeals
    )

    val mealDetails = listOf(
        MealDetail(
            id = "m1",
            name = "Oatmeal & Berries",
            calories = 320,
            protein = 12,
            carbs = 52,
            fats = 8,
            tag = "BREAKFAST",
            proteinPercent = 0.15f,
            carbsPercent = 0.65f,
            fatsPercent = 0.20f,
            ingredients = listOf(
                "1/2 cup Rolled Oats",
                "1 cup Almond Milk",
                "1/2 cup Mixed Berries",
                "1 tbsp Honey",
                "1 tbsp Chia Seeds"
            ),
            steps = listOf(
                "Combine oats and almond milk in a saucepan.",
                "Bring to a simmer over medium heat for 5 minutes.",
                "Stir in chia seeds and let sit for 2 minutes.",
                "Top with mixed berries and drizzle honey."
            )
        ),
        MealDetail(
            id = "m2",
            name = "Grilled Chicken Salad",
            calories = 450,
            protein = 38,
            carbs = 18,
            fats = 22,
            tag = "LUNCH",
            proteinPercent = 0.60f,
            carbsPercent = 0.15f,
            fatsPercent = 0.25f,
            ingredients = listOf(
                "200g Chicken Breast",
                "2 cups Mixed Greens",
                "1/4 cup Cherry Tomatoes",
                "1/4 Cucumber, sliced",
                "2 tbsp Balsamic Vinaigrette"
            ),
            steps = listOf(
                "Season chicken breast with salt, pepper, and herbs.",
                "Grill over medium-high heat for 6-7 minutes per side.",
                "Let chicken rest for 5 minutes, then slice.",
                "Arrange mixed greens, tomatoes, and cucumber on a plate.",
                "Top with sliced chicken and drizzle with vinaigrette."
            )
        ),
        MealDetail(
            id = "m3",
            name = "Protein Shake",
            calories = 180,
            protein = 30,
            carbs = 8,
            fats = 4,
            tag = "SNACK",
            proteinPercent = 0.70f,
            carbsPercent = 0.15f,
            fatsPercent = 0.15f,
            ingredients = listOf(
                "1 scoop Whey Protein",
                "1 cup Almond Milk",
                "1/2 Banana",
                "1 tbsp Peanut Butter",
                "Ice cubes"
            ),
            steps = listOf(
                "Add almond milk to blender first.",
                "Add protein powder, banana, and peanut butter.",
                "Add a handful of ice cubes.",
                "Blend on high for 30 seconds until smooth."
            )
        ),
        MealDetail(
            id = "m4",
            name = "Grilled Salmon Bowl",
            calories = 442,
            protein = 42,
            carbs = 28,
            fats = 18,
            tag = "HEALTHY",
            proteinPercent = 0.45f,
            carbsPercent = 0.30f,
            fatsPercent = 0.25f,
            ingredients = listOf(
                "200g Fresh Atlantic Salmon",
                "1/2 cup Quinoa",
                "1 cup Steamed Broccoli",
                "1/4 Avocado",
                "1 tbsp Olive Oil",
                "Lemon & Herbs"
            ),
            steps = listOf(
                "Season the salmon with salt, pepper, and herbs.",
                "Heat olive oil in a pan over medium-high heat.",
                "Sear salmon for 4-5 minutes per side until cooked through.",
                "Cook quinoa according to package instructions.",
                "Steam broccoli for 3 minutes until bright green.",
                "Assemble bowl with quinoa, salmon, broccoli, and avocado slices."
            )
        )
    )

    fun getMealDetail(mealId: String): MealDetail? {
        return mealDetails.find { it.id == mealId }
    }
}
