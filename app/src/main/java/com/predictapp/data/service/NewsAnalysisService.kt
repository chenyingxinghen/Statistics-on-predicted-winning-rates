package com.predictapp.data.service

import com.google.gson.JsonParser
import com.predictapp.data.api.NewsAnalysisApi
import com.predictapp.data.model.NewsAnalysisResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewsAnalysisService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.147.19.1:8080/") // 修复Android模拟器访问本地服务器的地址
        .client(
            OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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

    // 修改流式分析方法
    fun getNewsAnalysisStream(): Flow<String> = callbackFlow {
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://10.147.19.1:8080/api/news/analyze/stream")
            .addHeader("Accept", "text/event-stream")
            .addHeader("Connection", "keep-alive")
            .addHeader("Cache-Control", "no-cache")
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val jsonElement = JsonParser.parseString(data)
                    if (!jsonElement.isJsonObject) {
                        // 如果不是 JSON 对象，跳过处理
                        return
                    }
                    val jsonObject = jsonElement.asJsonObject

                    when (type) {
                        "content" -> {
                            // 兼容后端字段名，reasoning_content 可能为 reasoning
                            val reasoning = jsonObject.get("reasoning_content")?.asString
                                ?: jsonObject.get("reasoning")?.asString ?: ""
                            val content = jsonObject.get("content")?.asString ?: ""
                            // 输出结构化JSON字符串，保证字段为字符串且不为 null
                            val resultJson = buildString {
                                append("{")
                                append("\"reasoning\":\"")
                                append(reasoning.replace("\"", "\\\""))
                                append("\",\"content\":\"")
                                append(content.replace("\"", "\\\""))
                                append("\"}")
                            }
                            trySend(resultJson)
                        }
                        "error" -> {
                            val error = jsonObject.get("error")?.asString
                            close(Exception(error ?: "未知错误"))
                        }
                        "complete" -> close()
                        else -> trySend(data)
                    }
                } catch (e: Exception) {
                    trySend(data)
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                close(t)
            }
        }

        val eventSource = EventSources.createFactory(client).newEventSource(request, listener)

        awaitClose {
            eventSource.cancel()
        }
    }
}