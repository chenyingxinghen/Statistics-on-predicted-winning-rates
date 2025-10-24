package com.predictapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.predictapp.data.model.NewsAnalysisResult
import com.predictapp.data.service.NewsAnalysisService
import kotlinx.coroutines.launch
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAnalysisScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<NewsAnalysisResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val newsService = remember { NewsAnalysisService() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
            Text(
                "新闻分析与预测",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 分析按钮
        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                analysisResult = null
                
                coroutineScope.launch {
                    try {
                        // 调用网络请求获取新闻分析结果
                        val result = newsService.getNewsAnalysis()
                        
                        if (result.success) {
                            analysisResult = result
                        } else {
                            errorMessage = result.message ?: "未知错误"
                        }
                        isLoading = false
                    } catch (e: Exception) {
                        Log.e("NewsAnalysis", "获取分析结果失败", e)
                        errorMessage = "获取分析结果失败: ${e.message}"
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "正在分析..." else "获取今日新闻分析")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 显示错误信息
        errorMessage?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "错误",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 显示分析结果
        analysisResult?.let { result ->
            if (result.success) {
                // 预测结果（使用Markdown渲染）
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "分析结果",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // 使用MarkdownText组件渲染LLM的原始响应
                        MarkdownText(
                            markdown = result.prediction,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}