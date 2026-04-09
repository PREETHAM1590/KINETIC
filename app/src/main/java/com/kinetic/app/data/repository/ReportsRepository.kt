package com.kinetic.app.data.repository

import com.kinetic.app.data.fake.FakeReportsData
import com.kinetic.app.data.models.ChartDataPoint
import com.kinetic.app.data.models.PersonalRecord
import com.kinetic.app.data.models.WeeklyReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface ReportsRepository {
    fun getWeeklyReports(): Flow<List<WeeklyReport>>
    fun getPersonalRecords(): Flow<List<PersonalRecord>>
    fun getChartData(): Flow<List<ChartDataPoint>>
}

@Singleton
class FakeReportsRepository @Inject constructor() : ReportsRepository {

    override fun getWeeklyReports(): Flow<List<WeeklyReport>> = flow {
        delay(300)
        emit(FakeReportsData.weeklyReports)
    }

    override fun getPersonalRecords(): Flow<List<PersonalRecord>> = flow {
        delay(300)
        emit(FakeReportsData.personalRecords)
    }

    override fun getChartData(): Flow<List<ChartDataPoint>> = flow {
        delay(300)
        emit(FakeReportsData.weeklyWorkoutChart)
    }
}
