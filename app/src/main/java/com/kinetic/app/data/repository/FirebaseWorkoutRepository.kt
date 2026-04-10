package com.kinetic.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.fake.FakeWorkoutsData
import com.kinetic.app.data.models.ActiveWorkout
import com.kinetic.app.data.models.ExerciseItem
import com.kinetic.app.data.models.HiitItem
import com.kinetic.app.data.models.WorkoutItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseWorkoutRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : WorkoutRepository {

    private val uid get() = auth.currentUser?.uid

    override fun getWorkouts(): Flow<List<WorkoutItem>> = flow {
        val u = uid ?: run { emit(FakeWorkoutsData.workouts); return@flow }
        try {
            val snap = firestore.collection("workouts").get().await()
            val items = snap.documents.mapNotNull { it.toObject(WorkoutItem::class.java) }
            emit(if (items.isEmpty()) FakeWorkoutsData.workouts else items)
        } catch (e: Exception) {
            emit(FakeWorkoutsData.workouts)
        }
    }

    override fun getHiitWorkouts(): Flow<List<HiitItem>> = flow {
        try {
            val snap = firestore.collection("hiit_workouts").get().await()
            val items = snap.documents.mapNotNull { it.toObject(HiitItem::class.java) }
            emit(if (items.isEmpty()) FakeWorkoutsData.hiitWorkouts else items)
        } catch (e: Exception) {
            emit(FakeWorkoutsData.hiitWorkouts)
        }
    }

    override fun getWorkoutById(id: String): Flow<WorkoutItem?> = flow {
        try {
            val doc = firestore.collection("workouts").document(id).get().await()
            emit(doc.toObject(WorkoutItem::class.java) ?: FakeWorkoutsData.workouts.find { it.id == id })
        } catch (e: Exception) {
            emit(FakeWorkoutsData.workouts.find { it.id == id })
        }
    }

    override fun getWorkoutByTitle(title: String): Flow<WorkoutItem?> = flow {
        try {
            val snap = firestore.collection("workouts").whereEqualTo("title", title).get().await()
            val item = snap.documents.firstOrNull()?.toObject(WorkoutItem::class.java)
            emit(item ?: FakeWorkoutsData.workouts.find { it.title.equals(title, ignoreCase = true) })
        } catch (e: Exception) {
            emit(FakeWorkoutsData.workouts.find { it.title.equals(title, ignoreCase = true) })
        }
    }

    override fun getExercisesForWorkout(workoutId: String): Flow<List<ExerciseItem>> = flow {
        try {
            val snap = firestore.collection("workouts").document(workoutId)
                .collection("exercises").get().await()
            val items = snap.documents.mapNotNull { it.toObject(ExerciseItem::class.java) }
            emit(if (items.isEmpty()) FakeWorkoutsData.workoutExercises[workoutId] ?: emptyList() else items)
        } catch (e: Exception) {
            emit(FakeWorkoutsData.workoutExercises[workoutId] ?: emptyList())
        }
    }

    override fun getActiveWorkoutById(id: String): Flow<ActiveWorkout?> = flow {
        emit(FakeWorkoutsData.activeWorkouts.find { it.id == id } ?: FakeWorkoutsData.activeWorkouts.firstOrNull())
    }

    override fun getFilteredWorkouts(category: String): Flow<List<WorkoutItem>> = flow {
        try {
            val query = if (category == "All") firestore.collection("workouts").get().await()
            else firestore.collection("workouts").whereEqualTo("category", category).get().await()
            val items = query.documents.mapNotNull { it.toObject(WorkoutItem::class.java) }
            if (items.isEmpty()) {
                val all = FakeWorkoutsData.workouts
                emit(if (category == "All") all else all.filter { it.category.equals(category, ignoreCase = true) })
            } else emit(items)
        } catch (e: Exception) {
            val all = FakeWorkoutsData.workouts
            emit(if (category == "All") all else all.filter { it.category.equals(category, ignoreCase = true) })
        }
    }
}
