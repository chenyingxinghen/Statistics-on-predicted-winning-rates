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

class NewsAnalysisViewModel : ViewModel() {
    private val newsService = NewsAnalysisService()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 分析结果
    private val _analysisResult = MutableStateFlow<NewsAnalysisResult?>(null)
    val analysisResult: StateFlow<NewsAnalysisResult?> = _analysisResult.asStateFlow()

    // 流式结果
    private val _streamResult = MutableStateFlow("")
    val streamResult: StateFlow<String> = _streamResult.asStateFlow()

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
            _streamResult.value = "" // 清空流式结果

            try {
                // 调用网络请求获取新闻分析结果（流式）
                val stream = newsService.getNewsAnalysisStream()

                stream.collect { data ->
                    try {
                        Log.d("NewsAnalysisViewModel", "Received data chunk: $data")
                        // 判断是否为 JSON 对象
                        val trimmed = data.trim()
                        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                            val jsonElement = JsonParser.parseString(data)
                            if (jsonElement.isJsonObject) {
                                val jsonObject = jsonElement.asJsonObject
                                if (jsonObject.has("error")) {
                                    _errorMessage.value = jsonObject.get("error").asString
                                    _isLoading.value = false
                                } else {
                                    // 支持 reasoning_content 和 content
                                    val reasoning = jsonObject.get("reasoning_content")?.asString
                                    val content = jsonObject.get("content")?.asString
                                    val sb = StringBuilder()
                                    if (!reasoning.isNullOrBlank()) {
                                        sb.append(reasoning)
                                        if (!content.isNullOrBlank()) sb.append("\n")
                                    }
                                    if (!content.isNullOrBlank()) {
                                        sb.append(content)
                                    }
                                    val merged = sb.toString()
                                    if (merged.isNotBlank()) {
                                        _streamResult.value += merged
                                        _streamResult.value = StringBuilder(_streamResult.value).toString()
                                        Log.d("NewsAnalysisViewModel", "Stream result updated, length: ${_streamResult.value.length}")
                                    }
                                }
                            } else {
                                // 不是 JSON 对象，直接拼接原始内容
                                _streamResult.value += data
                            }
                        } else {
                            // 非 JSON 内容直接拼接
                            _streamResult.value += data
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
                        prediction = _streamResult.value,
                        success = true
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsAnalysisViewModel", "Error getting stream analysis", e)
                _errorMessage.value = "获取分析结果失败: ${e.message}"
            } finally {
                // 确保在任何情况下都设置isLoading为false
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
        _streamResult.value = ""
        _errorMessage.value = null
    }
}