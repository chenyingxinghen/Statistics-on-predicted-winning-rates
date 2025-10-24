package com.predictapp.data.service

import com.predictapp.data.api.NewsAnalysisApi
import com.predictapp.data.model.NewsAnalysisResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewsAnalysisService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.147.19.1:8080/") // Android模拟器访问本地服务器的地址
        .client(
            OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(60, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NewsAnalysisApi::class.java)

    suspend fun getNewsAnalysis(): NewsAnalysisResult {
        return try {
            val response = api.getNewsAnalysis()
            if (response.isSuccessful) {
                response.body() ?: NewsAnalysisResult(
                    success = false,
                    message = "服务器返回空响应"
                )
            } else {
                NewsAnalysisResult(
                    success = false,
                    message = "服务器错误: ${response.code()} ${response.message()}"
                )
            }
        } catch (e: Exception) {
            NewsAnalysisResult.error("网络请求失败: ${e.message}")
        }
    }
}