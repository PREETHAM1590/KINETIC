package com.kinetic.app.data.models

data class MembershipTier(
    val id: String,
    val name: String,
    val price: String,
    val features: List<String>,
    val isCurrent: Boolean = false
)

data class ClassItem(
    val id: String,
    val name: String,
    val instructor: String,
    val time: String,
    val spotsAvailable: Int,
    val totalSpots: Int,
    val category: String, // Yoga, HIIT, Spin, Boxing
    val durationMin: Int
)
