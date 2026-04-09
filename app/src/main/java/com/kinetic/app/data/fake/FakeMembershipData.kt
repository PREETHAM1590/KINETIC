package com.kinetic.app.data.fake

import com.kinetic.app.data.models.ClassItem
import com.kinetic.app.data.models.MembershipTier

object FakeMembershipData {
    val tiers = listOf(
        MembershipTier(
            id = "basic",
            name = "BASIC",
            price = "$19/mo",
            features = listOf(
                "Access to gym floor",
                "2 group classes/week",
                "Basic workout tracking",
                "Community forum access"
            ),
            isCurrent = false
        ),
        MembershipTier(
            id = "pro",
            name = "PRO",
            price = "$39/mo",
            features = listOf(
                "Everything in Basic",
                "Unlimited group classes",
                "AI Calorie Scanner",
                "Personal diet plans",
                "Advanced analytics"
            ),
            isCurrent = true
        ),
        MembershipTier(
            id = "elite",
            name = "ELITE",
            price = "$79/mo",
            features = listOf(
                "Everything in Pro",
                "1-on-1 personal trainer",
                "Custom meal prep",
                "Priority class booking",
                "Recovery room access",
                "Guest passes (2/month)"
            ),
            isCurrent = false
        )
    )

    val classes = listOf(
        ClassItem("c1", "Power Yoga", "Sarah Kim", "7:00 AM", 5, 20, "Yoga", 60),
        ClassItem("c2", "HIIT Blast", "Mike Johnson", "8:30 AM", 3, 15, "HIIT", 45),
        ClassItem("c3", "Spin Force", "Lisa Chen", "10:00 AM", 8, 25, "Spin", 45),
        ClassItem("c4", "Boxing Basics", "James Rivera", "12:00 PM", 10, 12, "Boxing", 60),
        ClassItem("c5", "Vinyasa Flow", "Sarah Kim", "2:00 PM", 7, 20, "Yoga", 75),
        ClassItem("c6", "Tabata Fire", "Mike Johnson", "4:00 PM", 1, 15, "HIIT", 30),
        ClassItem("c7", "Rhythm Ride", "Lisa Chen", "5:30 PM", 12, 25, "Spin", 45),
        ClassItem("c8", "Fight Club", "James Rivera", "7:00 PM", 6, 12, "Boxing", 60)
    )
}
