package com.kinetic.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.fake.FakeProfileData
import com.kinetic.app.data.models.Achievement
import com.kinetic.app.data.models.UserProfile
import com.kinetic.app.data.models.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProfileRepository {

    private val uid get() = auth.currentUser?.uid

    override fun getProfile(): Flow<UserProfile> = flow {
        val u = uid ?: run { emit(FakeProfileData.userProfile); return@flow }
        try {
            val doc = firestore.collection("users").document(u).get().await()
            emit(doc.toObject(UserProfile::class.java) ?: FakeProfileData.userProfile)
        } catch (e: Exception) { emit(FakeProfileData.userProfile) }
    }

    override fun getAchievements(): Flow<List<Achievement>> = flow {
        val u = uid ?: run { emit(FakeProfileData.achievements); return@flow }
        try {
            val snap = firestore.collection("users").document(u)
                .collection("achievements").get().await()
            val items = snap.documents.mapNotNull { it.toObject(Achievement::class.java) }
            emit(if (items.isEmpty()) FakeProfileData.achievements else items)
        } catch (e: Exception) { emit(FakeProfileData.achievements) }
    }

    override fun getStats(): Flow<UserStats> = flow {
        val u = uid ?: run { emit(FakeProfileData.userStats); return@flow }
        try {
            val doc = firestore.collection("users").document(u)
                .collection("stats").document("current").get().await()
            emit(doc.toObject(UserStats::class.java) ?: FakeProfileData.userStats)
        } catch (e: Exception) { emit(FakeProfileData.userStats) }
    }
}
