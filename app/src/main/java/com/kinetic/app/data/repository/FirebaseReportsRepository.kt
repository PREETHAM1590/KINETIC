package com.kinetic.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.fake.FakeReportsData
import com.kinetic.app.data.models.ChartDataPoint
import com.kinetic.app.data.models.PersonalRecord
import com.kinetic.app.data.models.WeeklyReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseReportsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ReportsRepository {

    private val uid get() = auth.currentUser?.uid

    override fun getWeeklyReports(): Flow<List<WeeklyReport>> = flow {
        val u = uid ?: run { emit(FakeReportsData.weeklyReports); return@flow }
        try {
            val snap = firestore.collection("users").document(u)
                .collection("weekly_reports").get().await()
            val items = snap.documents.mapNotNull { it.toObject(WeeklyReport::class.java) }
            emit(if (items.isEmpty()) FakeReportsData.weeklyReports else items)
        } catch (e: Exception) { emit(FakeReportsData.weeklyReports) }
    }

    override fun getPersonalRecords(): Flow<List<PersonalRecord>> = flow {
        val u = uid ?: run { emit(FakeReportsData.personalRecords); return@flow }
        try {
            val snap = firestore.collection("users").document(u)
                .collection("personal_records").get().await()
            val items = snap.documents.mapNotNull { it.toObject(PersonalRecord::class.java) }
            emit(if (items.isEmpty()) FakeReportsData.personalRecords else items)
        } catch (e: Exception) { emit(FakeReportsData.personalRecords) }
    }

    override fun getChartData(): Flow<List<ChartDataPoint>> = flow {
        val u = uid ?: run { emit(FakeReportsData.weeklyWorkoutChart); return@flow }
        try {
            val snap = firestore.collection("users").document(u)
                .collection("chart_data").get().await()
            val items = snap.documents.mapNotNull { it.toObject(ChartDataPoint::class.java) }
            emit(if (items.isEmpty()) FakeReportsData.weeklyWorkoutChart else items)
        } catch (e: Exception) { emit(FakeReportsData.weeklyWorkoutChart) }
    }
}
