package com.predictapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.predictapp.data.model.NewsAnalysisResult
import com.predictapp.ui.viewmodel.NewsAnalysisViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAnalysisScreen(navController: NavController) {
    // 使用ViewModel而不是直接创建Service
    val viewModel: NewsAnalysisViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val streamResult by viewModel.streamResult.collectAsState()

    // 新增：reasoning折叠状态
    var isReasoningCollapsed by remember { mutableStateOf(false) }

    // 监听推理完成自动折叠（isLoading变为false且reasoning有内容时）
    LaunchedEffect(isLoading, streamResult.reasoning) {
        if (!isLoading && streamResult.reasoning.isNotBlank()) {
            isReasoningCollapsed = true
        }
    }

    // 定义Markdown文本样式
    val markdownStyle = MaterialTheme.typography.bodyMedium.copy(
        lineHeight = 24.sp,
        fontSize = 16.sp,
        textAlign = TextAlign.Start // 改为Start对齐以更好地支持Markdown格式
    )

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
                viewModel.getNewsAnalysisStream()
                // 开始新的分析时，重置折叠状态
                isReasoningCollapsed = false
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

        // 实时显示流式结果（分区显示reasoning和content）
        if (isLoading && analysisResult == null && (streamResult.reasoning.isNotBlank() || streamResult.content.isNotBlank())) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "实时分析中...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Reasoning部分（可折叠）
                    if (streamResult.reasoning.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "推理过程",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { isReasoningCollapsed = !isReasoningCollapsed }) {
                                Text(if (isReasoningCollapsed) "展开" else "折叠")
                            }
                        }
                        // 流式输出时默认展开
                        if (!isReasoningCollapsed) {
                            MarkdownText(
                                markdown = streamResult.reasoning,
                                modifier = Modifier.fillMaxWidth(),
                                style = markdownStyle,
                                onLinkClicked = { _ -> }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    // Content部分
                    if (streamResult.content.isNotBlank()) {
                        Text(
                            "分析内容",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        MarkdownText(
                            markdown = streamResult.content,
                            modifier = Modifier.fillMaxWidth(),
                            style = markdownStyle,
                            onLinkClicked = { _ -> }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 当加载完成但没有analysisResult时显示已完成状态（分区显示）
        if (!isLoading && analysisResult == null && (streamResult.reasoning.isNotBlank() || streamResult.content.isNotBlank())) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "分析已完成",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Reasoning部分（可折叠）
                    if (streamResult.reasoning.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "推理过程",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { isReasoningCollapsed = !isReasoningCollapsed }) {
                                Text(if (isReasoningCollapsed) "展开" else "折叠")
                            }
                        }
                        // 加载完成后默认折叠，但用户可以手动展开
                        if (!isReasoningCollapsed) {
                            MarkdownText(
                                markdown = streamResult.reasoning,
                                modifier = Modifier.fillMaxWidth(),
                                style = markdownStyle,
                                onLinkClicked = { _ -> }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    // Content部分
                    if (streamResult.content.isNotBlank()) {
                        Text(
                            "分析内容",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        MarkdownText(
                            markdown = streamResult.content,
                            modifier = Modifier.fillMaxWidth(),
                            style = markdownStyle,
                            onLinkClicked = { _ -> }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 显示最终分析结果
        analysisResult?.let { result ->
            if (result.success) {
                // 仅展示 prediction 字段
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
                        MarkdownText(
                            markdown = result.prediction,
                            modifier = Modifier.fillMaxWidth(),
                            style = markdownStyle,
                            onLinkClicked = { _ -> }
                        )
                    }
                }
            } else {
                // 显示错误信息
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "分析失败",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result.message ?: "未知错误",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}