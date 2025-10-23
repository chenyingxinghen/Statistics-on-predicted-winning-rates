package com.predictapp.data.dao

import androidx.room.*
import com.predictapp.data.model.Direction
import com.predictapp.data.model.Prediction
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions ORDER BY date DESC")
    fun getAllPredictions(): Flow<List<Prediction>>

    @Query("SELECT * FROM predictions WHERE industryId = :industryId ORDER BY date DESC")
    fun getPredictionsByIndustry(industryId: Long): Flow<List<Prediction>>

    @Query("SELECT * FROM predictions WHERE date = :date")
    fun getPredictionsByDate(date: Date): Flow<List<Prediction>>

    @Query("SELECT * FROM predictions WHERE id = :id")
    suspend fun getPredictionById(id: Long): Prediction?

    @Insert
    suspend fun insert(prediction: Prediction): Long

    @Update
    suspend fun update(prediction: Prediction)

    @Delete
    suspend fun delete(prediction: Prediction)

    @Query("SELECT COUNT(*) FROM predictions WHERE predictedDirection = actualDirection AND actualDirection IS NOT NULL")
    suspend fun getCorrectPredictionsCount(): Int

    @Query("SELECT COUNT(*) FROM predictions WHERE actualDirection IS NOT NULL")
    suspend fun getTotalPredictionsCount(): Int
}