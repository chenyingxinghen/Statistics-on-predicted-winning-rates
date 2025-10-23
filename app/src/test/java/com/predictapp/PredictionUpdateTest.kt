package com.predictapp

import com.predictapp.data.model.Direction
import com.predictapp.data.model.Prediction
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class PredictionUpdateTest {
    
    @Test
    fun testPredictionUpdate() {
        // 创建一个预测记录
        val prediction = Prediction(
            id = 1L,
            industryId = 1L,
            date = Date(),
            predictedDirection = Direction.UP
        )
        
        // 验证初始状态
        assertNull(prediction.actualDirection)
        
        // 更新实际方向
        val updatedPrediction = prediction.copy(actualDirection = Direction.DOWN)
        
        // 验证更新后的状态
        assertNotNull(updatedPrediction.actualDirection)
        assertEquals(Direction.DOWN, updatedPrediction.actualDirection)
        assertEquals(prediction.id, updatedPrediction.id)
        assertEquals(prediction.industryId, updatedPrediction.industryId)
        assertEquals(prediction.date, updatedPrediction.date)
        assertEquals(prediction.predictedDirection, updatedPrediction.predictedDirection)
    }
    
    @Test
    fun testDirectionEnum() {
        // 验证方向枚举值
        assertEquals(3, Direction.values().size)
        assertTrue(Direction.values().contains(Direction.UP))
        assertTrue(Direction.values().contains(Direction.DOWN))
        assertTrue(Direction.values().contains(Direction.FLAT))
    }
}