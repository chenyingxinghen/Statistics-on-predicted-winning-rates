package com.predictapp.data.repository

import com.predictapp.data.dao.IndustryDao
import com.predictapp.data.model.Industry
import kotlinx.coroutines.flow.Flow

class IndustryRepository(private val industryDao: IndustryDao) {
    fun getAllIndustries(): Flow<List<Industry>> = industryDao.getAllIndustries()

    suspend fun getIndustryById(id: Long): Industry? = industryDao.getIndustryById(id)

    suspend fun insert(industry: Industry): Long = industryDao.insert(industry)

    suspend fun update(industry: Industry) = industryDao.update(industry)

    suspend fun delete(industry: Industry) = industryDao.delete(industry)
}