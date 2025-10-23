package com.predictapp.utils

import androidx.room.TypeConverter
import com.predictapp.data.model.Direction
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDirection(value: Int): Direction {
        return when (value) {
            0 -> Direction.UP
            1 -> Direction.DOWN
            2 -> Direction.FLAT
            else -> Direction.FLAT
        }
    }

    @TypeConverter
    fun fromDirection(direction: Direction): Int {
        return when (direction) {
            Direction.UP -> 0
            Direction.DOWN -> 1
            Direction.FLAT -> 2
        }
    }
}