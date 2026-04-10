package com.kinetic.app.data.models

data class ScannedFood(
    val name: String,
    val confidence: Float,
    val portionGrams: Float = 100f,
    val caloriesPer100g: Float = 0f,
    val proteinPer100g: Float = 0f,
    val carbsPer100g: Float = 0f,
    val fatPer100g: Float = 0f,
    val ifctCode: String? = null
) {
    val calories: Int get() = (caloriesPer100g * portionGrams / 100f).toInt()
    val protein: Float get() = proteinPer100g * portionGrams / 100f
    val carbs: Float get() = carbsPer100g * portionGrams / 100f
    val fat: Float get() = fatPer100g * portionGrams / 100f
}

data class FoodScanResult(
    val foods: List<ScannedFood>,
    val rawImageDescription: String = "",
    val source: ScanSource = ScanSource.GEMINI_VISION
)

enum class ScanSource { GEMINI_VISION, LOCAL_MODEL }
