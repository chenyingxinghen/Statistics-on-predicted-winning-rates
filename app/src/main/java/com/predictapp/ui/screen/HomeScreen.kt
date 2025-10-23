package com.predictapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.predictapp.data.model.Industry
import com.predictapp.ui.viewmodel.IndustryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: IndustryViewModel = viewModel()) {
    val industries by viewModel.allIndustries.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var industryToEdit by remember { mutableStateOf<Industry?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 标题和添加按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("行业管理", style = MaterialTheme.typography.headlineMedium)
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加行业")
            }
        }

        // 行业列表
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(industries) { industry ->
                IndustryItem(
                    industry = industry,
                    onEdit = {
                        industryToEdit = industry
                        showAddDialog = true
                    },
                    onDelete = { viewModel.delete(industry) }
                )
            }
        }
    }

    // 添加/编辑行业对话框
    if (showAddDialog) {
        AddIndustryDialog(
            industry = industryToEdit,
            onDismiss = {
                showAddDialog = false
                industryToEdit = null
            },
            onConfirm = { name ->
                if (industryToEdit != null) {
                    viewModel.update(industryToEdit!!.copy(name = name))
                } else {
                    viewModel.insert(Industry(name = name))
                }
                showAddDialog = false
                industryToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndustryItem(
    industry: Industry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(industry.name, style = MaterialTheme.typography.bodyLarge)
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIndustryDialog(
    industry: Industry?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(industry?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (industry == null) "添加行业" else "编辑行业") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("行业名称") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}