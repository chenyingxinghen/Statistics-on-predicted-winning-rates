package com.predictapp.data.api

import com.predictapp.data.model.NewsAnalysisResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface NewsAnalysisApi {
    @GET("api/news/analyze")
    suspend fun getNewsAnalysis(): Response<NewsAnalysisResult>
    
    // 添加流式分析接口
    @Streaming
    @GET("api/news/analyze/stream")
    fun getNewsAnalysisStream(): Flow<String> // 改为非suspend函数并返回Flow
}