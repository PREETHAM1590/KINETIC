package com.kinetic.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.fake.FakeMembershipData
import com.kinetic.app.data.models.ClassItem
import com.kinetic.app.data.models.MembershipTier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMembershipRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : MembershipRepository {

    override fun getTiers(): Flow<List<MembershipTier>> = flow {
        try {
            val snap = firestore.collection("membership_tiers").get().await()
            val items = snap.documents.mapNotNull { it.toObject(MembershipTier::class.java) }
            emit(if (items.isEmpty()) FakeMembershipData.tiers else items)
        } catch (e: Exception) { emit(FakeMembershipData.tiers) }
    }

    override fun getClasses(): Flow<List<ClassItem>> = flow {
        try {
            val snap = firestore.collection("gym_classes").get().await()
            val items = snap.documents.mapNotNull { it.toObject(ClassItem::class.java) }
            emit(if (items.isEmpty()) FakeMembershipData.classes else items)
        } catch (e: Exception) { emit(FakeMembershipData.classes) }
    }

    override fun getFilteredClasses(category: String): Flow<List<ClassItem>> = flow {
        try {
            val query = if (category == "All") firestore.collection("gym_classes").get().await()
            else firestore.collection("gym_classes").whereEqualTo("category", category).get().await()
            val items = query.documents.mapNotNull { it.toObject(ClassItem::class.java) }
            if (items.isEmpty()) {
                val all = FakeMembershipData.classes
                emit(if (category == "All") all else all.filter { it.category.equals(category, ignoreCase = true) })
            } else emit(items)
        } catch (e: Exception) {
            val all = FakeMembershipData.classes
            emit(if (category == "All") all else all.filter { it.category.equals(category, ignoreCase = true) })
        }
    }
}
