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
