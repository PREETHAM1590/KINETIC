package com.kinetic.app.data.fake

import com.kinetic.app.data.models.ActiveWorkout
import com.kinetic.app.data.models.ExerciseItem
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem

object FakeWorkoutsData {
    val workouts = listOf(
        WorkoutItem("w1", "Upper Body Blast", "Strength", 45, 300, "Intermediate"),
        WorkoutItem("w2", "Core Crusher", "Core", 20, 150, "Beginner"),
        WorkoutItem("w3", "Leg Day Hero", "Strength", 60, 450, "Advanced"),
        WorkoutItem("w4", "Cardio Sprint", "Cardio", 30, 350, "Intermediate")
    )

    val hiitWorkouts = listOf(
        HiitItem("h1", "Morning Burn", 15, 200),
        HiitItem("h2", "Lunch Break Sweat", 20, 250),
        HiitItem("h3", "Extreme Intervals", 30, 400)
    )

    val activeWorkouts = listOf(
        ActiveWorkout(
            id = "aw1",
            title = "Full Body Crusher",
            difficulty = "Intermediate",
            durationMin = 45,
            caloriesPerMin = 8,
            exercises = listOf(
                ExerciseItem("e1", "Barbell Squat", 4, 10, 135, 90),
                ExerciseItem("e2", "Leg Press", 3, 12, 200, 60),
                ExerciseItem("e3", "Calf Raises", 3, 15, 80, 45),
                ExerciseItem("e4", "Lunges", 3, 10, 50, 60)
            )
        ),
        ActiveWorkout(
            id = "aw2",
            title = "Upper Body Blast",
            difficulty = "Intermediate",
            durationMin = 40,
            caloriesPerMin = 8,
            exercises = listOf(
                ExerciseItem("e5", "Deadlifts", 4, 8, 185, 120),
                ExerciseItem("e6", "Bench Press", 4, 10, 135, 90),
                ExerciseItem("e7", "Pull-ups", 3, 8, 0, 60),
                ExerciseItem("e8", "Overhead Press", 3, 10, 85, 60)
            )
        )
    )

    val workoutExercises = mapOf(
        "w1" to listOf(
            ExerciseItem("e5", "Deadlifts", 4, 8, 185, 120),
            ExerciseItem("e6", "Bench Press", 4, 10, 135, 90),
            ExerciseItem("e7", "Pull-ups", 3, 8, 0, 60)
        ),
        "w2" to listOf(
            ExerciseItem("e9", "Planks", 3, 60, 0, 30),
            ExerciseItem("e10", "Russian Twists", 3, 20, 25, 45),
            ExerciseItem("e11", "Leg Raises", 3, 15, 0, 30)
        ),
        "w3" to listOf(
            ExerciseItem("e1", "Barbell Squat", 4, 10, 135, 90),
            ExerciseItem("e2", "Leg Press", 3, 12, 200, 60),
            ExerciseItem("e3", "Calf Raises", 3, 15, 80, 45),
            ExerciseItem("e4", "Lunges", 3, 10, 50, 60)
        ),
        "w4" to listOf(
            ExerciseItem("e12", "Sprint Intervals", 6, 60, 0, 30),
            ExerciseItem("e13", "Burpees", 4, 15, 0, 45),
            ExerciseItem("e14", "Jump Rope", 3, 120, 0, 30)
        )
    )
}
