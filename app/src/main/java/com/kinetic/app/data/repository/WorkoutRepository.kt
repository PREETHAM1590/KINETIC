package com.kinetic.app.data.repository

import com.kinetic.app.data.fake.FakeWorkoutsData
import com.kinetic.app.data.models.ActiveWorkout
import com.kinetic.app.data.models.ExerciseItem
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface WorkoutRepository {
    fun getWorkouts(): Flow<List<WorkoutItem>>
    fun getHiitWorkouts(): Flow<List<HiitItem>>
    fun getWorkoutById(id: String): Flow<WorkoutItem?>
    fun getWorkoutByTitle(title: String): Flow<WorkoutItem?>
    fun getExercisesForWorkout(workoutId: String): Flow<List<ExerciseItem>>
    fun getActiveWorkoutById(id: String): Flow<ActiveWorkout?>
    fun getFilteredWorkouts(category: String): Flow<List<WorkoutItem>>
}

@Singleton
class FakeWorkoutRepository @Inject constructor() : WorkoutRepository {

    override fun getWorkouts(): Flow<List<WorkoutItem>> = flow {
        delay(300)
        emit(FakeWorkoutsData.workouts)
    }

    override fun getHiitWorkouts(): Flow<List<HiitItem>> = flow {
        delay(200)
        emit(FakeWorkoutsData.hiitWorkouts)
    }

    override fun getWorkoutById(id: String): Flow<WorkoutItem?> = flow {
        delay(200)
        emit(FakeWorkoutsData.workouts.find { it.id == id })
    }

    override fun getWorkoutByTitle(title: String): Flow<WorkoutItem?> = flow {
        delay(200)
        emit(FakeWorkoutsData.workouts.find { it.title.equals(title, ignoreCase = true) })
    }

    override fun getExercisesForWorkout(workoutId: String): Flow<List<ExerciseItem>> = flow {
        delay(200)
        emit(FakeWorkoutsData.workoutExercises[workoutId] ?: emptyList())
    }

    override fun getActiveWorkoutById(id: String): Flow<ActiveWorkout?> = flow {
        delay(200)
        val directMatch = FakeWorkoutsData.activeWorkouts.find { it.id == id }
        if (directMatch != null) {
            emit(directMatch)
            return@flow
        }

        val sourceWorkoutTitle = FakeWorkoutsData.workouts.find { it.id == id }?.title
        val titleMatch = FakeWorkoutsData.activeWorkouts.find {
            it.title.equals(sourceWorkoutTitle, ignoreCase = true)
        }

        emit(titleMatch ?: FakeWorkoutsData.activeWorkouts.firstOrNull())
    }

    override fun getFilteredWorkouts(category: String): Flow<List<WorkoutItem>> = flow {
        delay(200)
        val result = if (category == "All") FakeWorkoutsData.workouts
        else FakeWorkoutsData.workouts.filter { it.category.equals(category, ignoreCase = true) }
        emit(result)
    }
}
