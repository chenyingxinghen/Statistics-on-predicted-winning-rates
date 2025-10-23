package com.predictapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.predictapp.data.PredictDatabase
import com.predictapp.data.model.Direction
import com.predictapp.data.model.Prediction
import com.predictapp.data.repository.PredictionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class PredictionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PredictionRepository

    init {
        val predictionDao = PredictDatabase.getDatabase(application).predictionDao()
        repository = PredictionRepository(predictionDao)
    }

    val allPredictions: Flow<List<Prediction>> = repository.getAllPredictions()

    fun insert(prediction: Prediction) = viewModelScope.launch {
        try {
            repository.insert(prediction)
        } catch (e: Exception) {
            // Handle exception (e.g., log error, show user message)
            e.printStackTrace()
        }
    }

    fun update(prediction: Prediction) = viewModelScope.launch {
        try {
            // 检查是否已经设置过实际方向
            val existingPrediction = repository.getPredictionById(prediction.id)
            if (existingPrediction?.actualDirection != null && prediction.actualDirection != existingPrediction.actualDirection) {
                // 已经设置过实际方向，不允许修改
                throw IllegalStateException("预测结果已设置，不允许修改")
            }
            repository.update(prediction)
        } catch (e: Exception) {
            // Handle exception (e.g., log error, show user message)
            e.printStackTrace()
        }
    }

    fun delete(prediction: Prediction) = viewModelScope.launch {
        try {
            repository.delete(prediction)
        } catch (e: Exception) {
            // Handle exception (e.g., log error, show user message)
            e.printStackTrace()
        }
    }

    suspend fun getCorrectPredictionsCount(): Int = repository.getCorrectPredictionsCount()

    suspend fun getTotalPredictionsCount(): Int = repository.getTotalPredictionsCount()

    suspend fun getAccuracyRate(): Float = repository.getAccuracyRate()
    
    suspend fun getPredictionById(id: Long): Prediction? = repository.getPredictionById(id)
}