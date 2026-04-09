package com.kinetic.app.data

import com.google.common.truth.Truth.assertThat
import com.kinetic.app.data.fake.FakeDietData
import com.kinetic.app.data.fake.FakeMembershipData
import com.kinetic.app.data.fake.FakeProfileData
import com.kinetic.app.data.fake.FakeReportsData
import com.kinetic.app.data.fake.FakeWorkoutsData
import org.junit.Test

class FakeDataConsistencyTest {

    @Test
    fun fakeWorkoutsData_workouts_isNotEmpty() {
        assertThat(FakeWorkoutsData.workouts).isNotEmpty()
    }

    @Test
    fun fakeWorkoutsData_hiitWorkouts_isNotEmpty() {
        assertThat(FakeWorkoutsData.hiitWorkouts).isNotEmpty()
    }

    @Test
    fun fakeWorkoutsData_activeWorkouts_isNotEmpty() {
        assertThat(FakeWorkoutsData.activeWorkouts).isNotEmpty()
    }

    @Test
    fun fakeWorkoutsData_workouts_havePositiveCalories() {
        assertThat(FakeWorkoutsData.workouts.all { it.calories > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_workouts_havePositiveDuration() {
        assertThat(FakeWorkoutsData.workouts.all { it.durationMin > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_workouts_haveNonEmptyIds() {
        assertThat(FakeWorkoutsData.workouts.all { it.id.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_workouts_haveNonEmptyTitles() {
        assertThat(FakeWorkoutsData.workouts.all { it.title.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_workouts_haveNonEmptyCategories() {
        assertThat(FakeWorkoutsData.workouts.all { it.category.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_hiitWorkouts_havePositiveCalories() {
        assertThat(FakeWorkoutsData.hiitWorkouts.all { it.calories > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_hiitWorkouts_havePositiveDuration() {
        assertThat(FakeWorkoutsData.hiitWorkouts.all { it.durationMin > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_activeWorkouts_haveExercises() {
        assertThat(FakeWorkoutsData.activeWorkouts.all { it.exercises.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_activeWorkouts_havePositiveCaloriesPerMin() {
        assertThat(FakeWorkoutsData.activeWorkouts.all { it.caloriesPerMin > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_exercises_havePositiveSets() {
        val allExercises = FakeWorkoutsData.activeWorkouts.flatMap { it.exercises }
        assertThat(allExercises.all { it.sets > 0 }).isTrue()
    }

    @Test
    fun fakeWorkoutsData_exercises_havePositiveReps() {
        val allExercises = FakeWorkoutsData.activeWorkouts.flatMap { it.exercises }
        assertThat(allExercises.all { it.reps > 0 }).isTrue()
    }

    @Test
    fun fakeDietData_todayMeals_isNotEmpty() {
        assertThat(FakeDietData.todayMeals).isNotEmpty()
    }

    @Test
    fun fakeDietData_meals_havePositiveCalories() {
        assertThat(FakeDietData.todayMeals.all { it.calories > 0 }).isTrue()
    }

    @Test
    fun fakeDietData_meals_haveNonEmptyNames() {
        assertThat(FakeDietData.todayMeals.all { it.name.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeDietData_meals_haveNonEmptyIds() {
        assertThat(FakeDietData.todayMeals.all { it.id.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeDietData_dailyNutrition_targetCaloriesPositive() {
        assertThat(FakeDietData.dailyNutrition.targetCalories).isGreaterThan(0)
    }

    @Test
    fun fakeDietData_dailyNutrition_totalCaloriesPositive() {
        assertThat(FakeDietData.dailyNutrition.totalCalories).isGreaterThan(0)
    }

    @Test
    fun fakeDietData_mealDetails_isNotEmpty() {
        assertThat(FakeDietData.mealDetails).isNotEmpty()
    }

    @Test
    fun fakeDietData_mealDetails_haveIngredients() {
        assertThat(FakeDietData.mealDetails.all { it.ingredients.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeDietData_mealDetails_haveSteps() {
        assertThat(FakeDietData.mealDetails.all { it.steps.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeReportsData_weeklyReports_isNotEmpty() {
        assertThat(FakeReportsData.weeklyReports).isNotEmpty()
    }

    @Test
    fun fakeReportsData_personalRecords_isNotEmpty() {
        assertThat(FakeReportsData.personalRecords).isNotEmpty()
    }

    @Test
    fun fakeReportsData_chartData_isNotEmpty() {
        assertThat(FakeReportsData.weeklyWorkoutChart).isNotEmpty()
    }

    @Test
    fun fakeReportsData_reports_haveNonEmptyWeekLabels() {
        assertThat(FakeReportsData.weeklyReports.all { it.weekLabel.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeReportsData_reports_havePositiveWorkoutsCompleted() {
        assertThat(FakeReportsData.weeklyReports.all { it.workoutsCompleted > 0 }).isTrue()
    }

    @Test
    fun fakeReportsData_reports_havePositiveCaloriesBurned() {
        assertThat(FakeReportsData.weeklyReports.all { it.caloriesBurned > 0 }).isTrue()
    }

    @Test
    fun fakeProfileData_userProfile_hasNonEmptyName() {
        assertThat(FakeProfileData.userProfile.name).isNotEmpty()
    }

    @Test
    fun fakeProfileData_userProfile_hasNonEmptyEmail() {
        assertThat(FakeProfileData.userProfile.email).isNotEmpty()
    }

    @Test
    fun fakeProfileData_achievements_isNotEmpty() {
        assertThat(FakeProfileData.achievements).isNotEmpty()
    }

    @Test
    fun fakeProfileData_achievements_haveNonEmptyTitles() {
        assertThat(FakeProfileData.achievements.all { it.title.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeProfileData_stats_positiveWorkoutsCompleted() {
        assertThat(FakeProfileData.userProfile.stats.workoutsCompleted).isGreaterThan(0)
    }

    @Test
    fun fakeMembershipData_tiers_isNotEmpty() {
        assertThat(FakeMembershipData.tiers).isNotEmpty()
    }

    @Test
    fun fakeMembershipData_classes_isNotEmpty() {
        assertThat(FakeMembershipData.classes).isNotEmpty()
    }

    @Test
    fun fakeMembershipData_tiers_haveNonEmptyNames() {
        assertThat(FakeMembershipData.tiers.all { it.name.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeMembershipData_tiers_haveFeatures() {
        assertThat(FakeMembershipData.tiers.all { it.features.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeMembershipData_classes_havePositiveDuration() {
        assertThat(FakeMembershipData.classes.all { it.durationMin > 0 }).isTrue()
    }

    @Test
    fun fakeMembershipData_classes_haveNonEmptyNames() {
        assertThat(FakeMembershipData.classes.all { it.name.isNotEmpty() }).isTrue()
    }

    @Test
    fun fakeMembershipData_exactlyOneTierIsCurrent() {
        assertThat(FakeMembershipData.tiers.count { it.isCurrent }).isEqualTo(1)
    }

    @Test
    fun fakeWorkoutsData_workoutExercises_hasCorrectKeys() {
        val workoutIds = FakeWorkoutsData.workouts.map { it.id }
        assertThat(FakeWorkoutsData.workoutExercises.keys).containsExactlyElementsIn(workoutIds)
    }
}
