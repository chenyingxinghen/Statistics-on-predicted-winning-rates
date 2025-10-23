package com.predictapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.predictapp.data.model.Direction
import com.predictapp.data.model.Industry
import com.predictapp.data.model.Prediction
import com.predictapp.ui.viewmodel.IndustryViewModel
import com.predictapp.ui.viewmodel.PredictionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePredictionScreen(
    navController: NavController,
    industryViewModel: IndustryViewModel = viewModel(),
    predictionViewModel: PredictionViewModel = viewModel()
) {
    val industries by industryViewModel.allIndustries.collectAsState(initial = emptyList())
    val predictions by predictionViewModel.allPredictions.collectAsState(initial = emptyList())
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // 过滤出已有预测但未设置实际方向的记录
    val predictionsToUpdate = predictions.filter { 
        it.actualDirection == null 
    }.sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("更新预测结果") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (showSuccessMessage) {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = {
                        TextButton(onClick = { showSuccessMessage = false }) {
                            Text("关闭")
                        }
                    }
                ) {
                    Text("预测结果更新成功！")
                }
                
                // 3秒后自动隐藏消息
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showSuccessMessage = false
                }
            }
            
            if (predictionsToUpdate.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无需要更新的预测记录", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Text("请选择需要更新的预测记录:", style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(predictionsToUpdate) { prediction ->
                        val industry = industries.find { it.id == prediction.industryId }
                        if (industry != null) {
                            UpdatePredictionItem(
                                industry = industry,
                                prediction = prediction,
                                onUpdate = { actualDirection ->
                                    val updatedPrediction = prediction.copy(actualDirection = actualDirection)
                                    predictionViewModel.update(updatedPrediction)
                                    showSuccessMessage = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdatePredictionItem(
    industry: Industry,
    prediction: Prediction,
    onUpdate: (Direction) -> Unit
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(industry.name, style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Text(
                        "预测日期: ${dateFormat.format(prediction.date)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Button(onClick = { showUpdateDialog = true }) {
                    Text("更新结果")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "预测方向: ${
                        when (prediction.predictedDirection) {
                            Direction.UP -> "上涨"
                            Direction.DOWN -> "下跌"
                            Direction.FLAT -> "平盘"
                        }
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    if (showUpdateDialog) {
        UpdatePredictionDialog(
            industry = industry,
            prediction = prediction,
            onDismiss = { showUpdateDialog = false },
            onConfirm = { actualDirection ->
                onUpdate(actualDirection)
                showUpdateDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePredictionDialog(
    industry: Industry,
    prediction: Prediction,
    onDismiss: () -> Unit,
    onConfirm: (Direction) -> Unit
) {
    var selectedDirection by remember { mutableStateOf<Direction?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("更新${industry.name}的实际涨跌方向") },
        text = {
            Column {
                Text("预测日期: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(prediction.date)}")
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
                onClick = { selectedDirection?.let { onConfirm(it) } },
                enabled = selectedDirection != null
            ) {
                Text("确认更新")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}