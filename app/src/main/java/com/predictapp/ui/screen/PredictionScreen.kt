package com.predictapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.predictapp.data.model.Direction
import com.predictapp.data.model.Industry
import com.predictapp.data.model.Prediction
import com.predictapp.ui.viewmodel.IndustryViewModel
import com.predictapp.ui.viewmodel.PredictionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PredictionScreen(industryViewModel: IndustryViewModel = viewModel(),
                     predictionViewModel: PredictionViewModel = viewModel()) {
    val industries by industryViewModel.allIndustries.collectAsState(initial = emptyList())
    val predictions by predictionViewModel.allPredictions.collectAsState(initial = emptyList())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val todaysPredictions = predictions.filter { 
        val cal = Calendar.getInstance()
        cal.time = it.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.time == today
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("今日预测", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("选择行业进行预测:", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 显示还没有预测的行业
        val predictedIndustryIds = todaysPredictions.map { it.industryId }.toSet()
        val remainingIndustries = industries.filter { it.id !in predictedIndustryIds }
        
        LazyColumn {
            items(remainingIndustries) { industry ->
                IndustryPredictionItem(
                    industry = industry,
                    onPredict = { direction ->
                        val prediction = Prediction(
                            industryId = industry.id,
                            date = Date(),
                            predictedDirection = direction
                        )
                        predictionViewModel.insert(prediction)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("今日已预测:", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 显示今天的预测
        LazyColumn {
            items(todaysPredictions) { prediction ->
                val industry = industries.find { it.id == prediction.industryId }
                if (industry != null) {
                    TodaysPredictionItem(
                        industry = industry,
                        prediction = prediction,
                        onUpdate = { actualDirection ->
                            val updatedPrediction = prediction.copy(actualDirection = actualDirection)
                            predictionViewModel.update(updatedPrediction)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IndustryPredictionItem(
    industry: Industry,
    onPredict: (Direction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(industry.name, style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onPredict(Direction.UP) }) {
                    Text("上涨")
                }
                Button(onClick = { onPredict(Direction.FLAT) }) {
                    Text("平盘")
                }
                Button(onClick = { onPredict(Direction.DOWN) }) {
                    Text("下跌")
                }
            }
        }
    }
}

@Composable
fun TodaysPredictionItem(
    industry: Industry,
    prediction: Prediction,
    onUpdate: (Direction) -> Unit = { _ -> }
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedDirection by remember { mutableStateOf<Direction?>(null) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (prediction.predictedDirection) {
                Direction.UP -> MaterialTheme.colorScheme.primaryContainer
                Direction.DOWN -> MaterialTheme.colorScheme.errorContainer
                Direction.FLAT -> MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(industry.name, style = MaterialTheme.typography.bodyLarge)
                
                Text(
                    when (prediction.predictedDirection) {
                        Direction.UP -> "上涨"
                        Direction.DOWN -> "下跌"
                        Direction.FLAT -> "平盘"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 如果还没有设置实际方向，显示更新按钮
            if (prediction.actualDirection == null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { showUpdateDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("更新结果")
                }
            } else {
                // 如果已经设置了实际方向，显示实际方向
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("实际方向:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        when (prediction.actualDirection) {
                            Direction.UP -> "上涨"
                            Direction.DOWN -> "下跌"
                            Direction.FLAT -> "平盘"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (prediction.predictedDirection == prediction.actualDirection) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    // 更新预测结果对话框
    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("更新${industry.name}的实际涨跌方向") },
            text = {
                Column {
                    Text("预测方向: ${
                        when (prediction.predictedDirection) {
                            Direction.UP -> "上涨"
                            Direction.DOWN -> "下跌"
                            Direction.FLAT -> "平盘"
                        }
                    }")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("请选择实际涨跌方向:")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Direction.values().forEach { direction ->
                            Button(
                                onClick = { selectedDirection = direction },
                                colors = if (selectedDirection == direction) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            ) {
                                Text(
                                    when (direction) {
                                        Direction.UP -> "上涨"
                                        Direction.DOWN -> "下跌"
                                        Direction.FLAT -> "平盘"
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDirection?.let { 
                            onUpdate(it)
                            showUpdateDialog = false
                        }
                    },
                    enabled = selectedDirection != null
                ) {
                    Text("确认更新")
                }
            },
            dismissButton = {
                Button(onClick = { showUpdateDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}