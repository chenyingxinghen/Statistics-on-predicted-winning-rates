package com.predictapp.data.repository

import com.predictapp.data.dao.PredictionDao
import com.predictapp.data.model.Prediction
import kotlinx.coroutines.flow.Flow
import java.util.Date

class PredictionRepository(private val predictionDao: PredictionDao) {
    fun getAllPredictions(): Flow<List<Prediction>> = predictionDao.getAllPredictions()

    fun getPredictionsByIndustry(industryId: Long): Flow<List<Prediction>> =
        predictionDao.getPredictionsByIndustry(industryId)

    fun getPredictionsByDate(date: Date): Flow<List<Prediction>> =
        predictionDao.getPredictionsByDate(date)

    suspend fun getPredictionById(id: Long): Prediction? = predictionDao.getPredictionById(id)

    suspend fun insert(prediction: Prediction): Long = predictionDao.insert(prediction)

    suspend fun update(prediction: Prediction) = predictionDao.update(prediction)

    suspend fun delete(prediction: Prediction) = predictionDao.delete(prediction)

    suspend fun getAccuracyRate(): Float {
        val correctCount = predictionDao.getCorrectPredictionsCount()
        val totalCount = predictionDao.getTotalPredictionsCount()
        return if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
    }

    suspend fun getCorrectPredictionsCount(): Int = predictionDao.getCorrectPredictionsCount()

    suspend fun getTotalPredictionsCount(): Int = predictionDao.getTotalPredictionsCount()
}