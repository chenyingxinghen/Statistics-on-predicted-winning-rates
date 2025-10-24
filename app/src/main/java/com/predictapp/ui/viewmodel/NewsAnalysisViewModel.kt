package com.predictapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.predictapp.data.model.NewsAnalysisResult
import com.predictapp.data.service.NewsAnalysisService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 新增：流式分析状态数据类
data class StreamAnalysisState(
    val reasoning: String = "",
    val content: String = ""
)

class NewsAnalysisViewModel : ViewModel() {
    private val newsService = NewsAnalysisService()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 分析结果
    private val _analysisResult = MutableStateFlow<NewsAnalysisResult?>(null)
    val analysisResult: StateFlow<NewsAnalysisResult?> = _analysisResult.asStateFlow()

    // 流式结果（结构化）
    private val _streamResult = MutableStateFlow(StreamAnalysisState())
    val streamResult: StateFlow<StreamAnalysisState> = _streamResult.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 获取新闻分析结果（流式）
     */
    fun getNewsAnalysisStream() {
        viewModelScope.launch {
            Log.d("NewsAnalysisViewModel", "Starting stream analysis")
            _isLoading.value = true
            _errorMessage.value = null
            _analysisResult.value = null
            _streamResult.value = StreamAnalysisState() // 清空流式结果

            try {
                val stream = newsService.getNewsAnalysisStream()

                stream.collect { data ->
                    try {
                        Log.d("NewsAnalysisViewModel", "Received data chunk: $data")
                        val trimmed = data.trim()
                        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                            val jsonElement = try { JsonParser.parseString(data) } catch (e: Exception) { null }
                            if (jsonElement != null && jsonElement.isJsonObject) {
                                val jsonObject = jsonElement.asJsonObject
                                if (jsonObject.has("error")) {
                                    _errorMessage.value = jsonObject.get("error")?.asString ?: "未知错误"
                                    _isLoading.value = false
                                } else {
                                    // 兼容后端输出的reasoning和content，健壮性处理
                                    val reasoning = if (jsonObject.has("reasoning")) jsonObject.get("reasoning")?.asString else null
                                    val content = if (jsonObject.has("content")) jsonObject.get("content")?.asString else null
                                    val prev = _streamResult.value
                                    _streamResult.value = StreamAnalysisState(
                                        reasoning = prev.reasoning + (reasoning ?: ""),
                                        content = prev.content + (content ?: "")
                                    )
                                    Log.d("NewsAnalysisViewModel", "Stream reasoning: ${_streamResult.value.reasoning.length}, content: ${_streamResult.value.content.length}")
                                }
                            } // 不是JSON对象直接跳过
                        } else {
                            // 非 JSON 内容直接拼接到content
                            val prev = _streamResult.value
                            _streamResult.value = prev.copy(content = prev.content + data)
                        }
                    } catch (e: Exception) {
                        Log.e("NewsAnalysisViewModel", "Error processing data chunk", e)
                        _errorMessage.value = "处理响应数据时出错: ${e.message}"
                        _isLoading.value = false
                    }
                }

                // 只有在没有错误的情况下才设置最终结果
                if (_errorMessage.value == null) {
                    _analysisResult.value = NewsAnalysisResult(
                        prediction = _streamResult.value.content,
                        success = true
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsAnalysisViewModel", "Error getting stream analysis", e)
                _errorMessage.value = "获取分析结果失败: ${e.message}"
            } finally {
                Log.d("NewsAnalysisViewModel", "Stream analysis completed, setting loading to false")
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 重置状态
     */
    fun reset() {
        _isLoading.value = false
        _analysisResult.value = null
        _streamResult.value = StreamAnalysisState()
        _errorMessage.value = null
    }
}