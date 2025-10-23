package com.predictapp.data.dao

import androidx.room.*
import com.predictapp.data.model.Industry
import kotlinx.coroutines.flow.Flow

@Dao
interface IndustryDao {
    @Query("SELECT * FROM industries ORDER BY name ASC")
    fun getAllIndustries(): Flow<List<Industry>>

    @Query("SELECT * FROM industries WHERE id = :id")
    suspend fun getIndustryById(id: Long): Industry?

    @Insert
    suspend fun insert(industry: Industry): Long

    @Update
    suspend fun update(industry: Industry)

    @Delete
    suspend fun delete(industry: Industry)
}