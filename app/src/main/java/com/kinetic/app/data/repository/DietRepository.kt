package com.kinetic.app.data.repository

import com.kinetic.app.data.fake.FakeDietData
import com.kinetic.app.data.models.DailyNutrition
import com.kinetic.app.data.models.MealDetail
import com.kinetic.app.data.models.MealItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface DietRepository {
    fun getMeals(): Flow<List<MealItem>>
    fun getDailyNutrition(): Flow<DailyNutrition>
    fun getMealDetailById(id: String): Flow<MealDetail?>
    fun getMealDetailByName(name: String): Flow<MealDetail?>
}

@Singleton
class FakeDietRepository @Inject constructor() : DietRepository {

    override fun getMeals(): Flow<List<MealItem>> = flow {
        delay(300)
        emit(FakeDietData.todayMeals)
    }

    override fun getDailyNutrition(): Flow<DailyNutrition> = flow {
        delay(300)
        emit(FakeDietData.dailyNutrition)
    }

    override fun getMealDetailById(id: String): Flow<MealDetail?> = flow {
        delay(200)
        emit(FakeDietData.getMealDetail(id))
    }

    override fun getMealDetailByName(name: String): Flow<MealDetail?> = flow {
        delay(200)
        emit(FakeDietData.mealDetails.find { it.name.equals(name, ignoreCase = true) })
    }
}
