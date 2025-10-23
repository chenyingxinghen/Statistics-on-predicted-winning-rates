package com.predictapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.predictapp.data.PredictDatabase
import com.predictapp.data.model.Industry
import com.predictapp.data.repository.IndustryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class IndustryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IndustryRepository

    init {
        val industryDao = PredictDatabase.getDatabase(application).industryDao()
        repository = IndustryRepository(industryDao)
    }

    val allIndustries: Flow<List<Industry>> = repository.getAllIndustries()

    fun insert(industry: Industry) = viewModelScope.launch {
        repository.insert(industry)
    }

    fun update(industry: Industry) = viewModelScope.launch {
        repository.update(industry)
    }

    fun delete(industry: Industry) = viewModelScope.launch {
        repository.delete(industry)
    }
}