package com.predictapp.data.api

import com.predictapp.data.model.NewsAnalysisResult
import retrofit2.Response
import retrofit2.http.GET

interface NewsAnalysisApi {
    @GET("api/news/analyze")
    suspend fun getNewsAnalysis(): Response<NewsAnalysisResult>
}