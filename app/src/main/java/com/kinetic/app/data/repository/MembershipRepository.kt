package com.kinetic.app.data.repository

import com.kinetic.app.data.fake.FakeMembershipData
import com.kinetic.app.data.models.ClassItem
import com.kinetic.app.data.models.MembershipTier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface MembershipRepository {
    fun getTiers(): Flow<List<MembershipTier>>
    fun getClasses(): Flow<List<ClassItem>>
    fun getFilteredClasses(category: String): Flow<List<ClassItem>>
}

@Singleton
class FakeMembershipRepository @Inject constructor() : MembershipRepository {

    override fun getTiers(): Flow<List<MembershipTier>> = flow {
        delay(300)
        emit(FakeMembershipData.tiers)
    }

    override fun getClasses(): Flow<List<ClassItem>> = flow {
        delay(300)
        emit(FakeMembershipData.classes)
    }

    override fun getFilteredClasses(category: String): Flow<List<ClassItem>> = flow {
        delay(200)
        val result = if (category == "All") FakeMembershipData.classes
        else FakeMembershipData.classes.filter { it.category.equals(category, ignoreCase = true) }
        emit(result)
    }
}
