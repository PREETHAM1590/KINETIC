package com.kinetic.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.fake.FakeDietData
import com.kinetic.app.data.models.DailyNutrition
import com.kinetic.app.data.models.MealDetail
import com.kinetic.app.data.models.MealItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDietRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : DietRepository {

    private val uid get() = auth.currentUser?.uid

    override fun getMeals(): Flow<List<MealItem>> = flow {
        val u = uid ?: run { emit(FakeDietData.todayMeals); return@flow }
        try {
            val snap = firestore.collection("users").document(u).collection("meals").get().await()
            val items = snap.documents.mapNotNull { it.toObject(MealItem::class.java) }
            emit(if (items.isEmpty()) FakeDietData.todayMeals else items)
        } catch (e: Exception) { emit(FakeDietData.todayMeals) }
    }

    override fun getDailyNutrition(): Flow<DailyNutrition> = flow {
        val u = uid ?: run { emit(FakeDietData.dailyNutrition); return@flow }
        try {
            val doc = firestore.collection("users").document(u)
                .collection("nutrition").document("today").get().await()
            emit(doc.toObject(DailyNutrition::class.java) ?: FakeDietData.dailyNutrition)
        } catch (e: Exception) { emit(FakeDietData.dailyNutrition) }
    }

    override fun getMealDetailById(id: String): Flow<MealDetail?> = flow {
        try {
            val doc = firestore.collection("meal_catalog").document(id).get().await()
            emit(doc.toObject(MealDetail::class.java) ?: FakeDietData.getMealDetail(id))
        } catch (e: Exception) { emit(FakeDietData.getMealDetail(id)) }
    }

    override fun getMealDetailByName(name: String): Flow<MealDetail?> = flow {
        try {
            val snap = firestore.collection("meal_catalog").whereEqualTo("name", name).get().await()
            val item = snap.documents.firstOrNull()?.toObject(MealDetail::class.java)
            emit(item ?: FakeDietData.mealDetails.find { it.name.equals(name, ignoreCase = true) })
        } catch (e: Exception) {
            emit(FakeDietData.mealDetails.find { it.name.equals(name, ignoreCase = true) })
        }
    }
}
