package com.predictapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "predictions")
data class Prediction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val industryId: Long,
    val date: Date,
    val predictedDirection: Direction, // 预测方向
    val actualDirection: Direction? = null // 实际方向（可为空，表示尚未更新结果）
)

enum class Direction {
    UP,    // 上涨
    DOWN,  // 下跌
    FLAT   // 平盘
}