package com.predictapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.predictapp.data.model.Direction
import com.predictapp.data.model.Industry
import com.predictapp.data.model.Prediction
import com.predictapp.ui.viewmodel.IndustryViewModel
import com.predictapp.ui.viewmodel.PredictionViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@Composable
fun StatisticsScreen(
    industryViewModel: IndustryViewModel = viewModel(),
    predictionViewModel: PredictionViewModel = viewModel(),
    navController: NavHostController? = null
) {
    val industries by industryViewModel.allIndustries.collectAsState(initial = emptyList())
    val predictions by predictionViewModel.allPredictions.collectAsState(initial = emptyList())
    
    val (correctCount, totalCount, accuracyRate) = calculateStats(predictions)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("统计信息", style = MaterialTheme.typography.headlineMedium)
            
            // 添加更新预测结果按钮
            navController?.let {
                IconButton(onClick = { navController.navigate("update_prediction") }) {
                    Icon(Icons.Default.Edit, contentDescription = "更新预测结果")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 总体统计卡片
        OverallStatsCard(correctCount, totalCount, accuracyRate)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("各行业预测统计", style = MaterialTheme.typography.headlineSmall)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 各行业统计
        LazyColumn {
            items(industries) { industry ->
                val industryPredictions = predictions.filter { it.industryId == industry.id }
                if (industryPredictions.isNotEmpty()) {
                    IndustryStatsItem(industry, industryPredictions)
                }
            }
        }
    }
}

@Composable
fun OverallStatsCard(correctCount: Int, totalCount: Int, accuracyRate: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "总体准确率",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "${DecimalFormat("#.##%").format(accuracyRate)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "正确预测: $correctCount / 总预测: $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IndustryStatsItem(industry: Industry, predictions: List<Prediction>) {
    // 过滤出已设置实际方向的预测
    val predictionsWithActual = predictions.filter { it.actualDirection != null }
    val correctCount = predictionsWithActual.count { it.predictedDirection == it.actualDirection }
    val totalCount = predictionsWithActual.size
    val accuracyRate = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(industry.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${DecimalFormat("#.##%").format(accuracyRate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = accuracyRate,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "正确: $correctCount | 总计: $totalCount",
                style = MaterialTheme.typography.bodySmall
            )
            
            // 显示最近的预测记录
            if (predictionsWithActual.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("最近预测:", style = MaterialTheme.typography.bodyMedium)
                
                predictionsWithActual.take(3).forEach { prediction ->
                    val dateFormat = SimpleDateFormat("MM-dd", java.util.Locale.getDefault())
                    val dateStr = dateFormat.format(prediction.date)
                    val predictedStr = when (prediction.predictedDirection) {
                        Direction.UP -> "涨"
                        Direction.DOWN -> "跌"
                        Direction.FLAT -> "平"
                    }
                    val actualStr = when (prediction.actualDirection) {
                        Direction.UP -> "涨"
                        Direction.DOWN -> "跌"
                        Direction.FLAT -> "平"
                        null -> "未定" // 这行实际上不会执行，因为我们已经过滤了
                    }
                    
                    val isCorrect = prediction.predictedDirection == prediction.actualDirection
                    val resultColor = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    
                    Text(
                        "$dateStr 预测:$predictedStr 实际:$actualStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = resultColor
                    )
                }
            }
        }
    }
}

fun calculateStats(predictions: List<Prediction>): Triple<Int, Int, Float> {
    // 过滤出已设置实际方向的预测
    val predictionsWithActual = predictions.filter { it.actualDirection != null }
    val correctCount = predictionsWithActual.count { it.predictedDirection == it.actualDirection }
    val totalCount = predictionsWithActual.size
    val accuracyRate = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
    return Triple(correctCount, totalCount, accuracyRate)
}