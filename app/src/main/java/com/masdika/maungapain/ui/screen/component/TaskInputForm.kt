package com.masdika.maungapain.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import com.masdika.maungapain.ui.viewmodel.TaskUiEvent
import com.masdika.maungapain.ui.viewmodel.TaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputForm(
    state: TaskUiState,
    onEvent: (TaskUiEvent) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isUpdateMode = state.selectedTask != null

    Dialog(
        onDismissRequest = { onEvent(TaskUiEvent.OnCloseForm) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 10.dp, vertical = 15.dp)
        ) {
            Text(
                text = if (isUpdateMode) "Edit Task" else "Create Task",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(15.dp))
            OutlinedTextField(
                value = state.taskTitleInput,
                onValueChange = { onEvent(TaskUiEvent.OnTitleInputChange(it)) },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.taskDescriptionInput,
                onValueChange = { onEvent(TaskUiEvent.OnDescriptionInputChange(it)) },
                label = { Text("Description") },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(10.dp))
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = state.taskPriorityInput.name,
                    onValueChange = {},
                    label = { Text("Priority") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    leadingIcon = {
                        PriorityIcon(priority = state.taskPriorityInput)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                            enabled = true
                        )
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    Priority.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    PriorityIcon(priority = priority)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(priority.name)
                                }
                            },
                            onClick = {
                                onEvent(TaskUiEvent.OnPriorityInputChange(priority))
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityIcon(priority: Priority) {
    val iconColor = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color.Green
    }

    Icon(
        imageVector = Icons.Default.Flag,
        contentDescription = "Priority ${priority.name}",
        tint = iconColor
    )
}


@Preview(
    showBackground = true,
    widthDp = 425,
    heightDp = 944,
)
@Composable
private fun TaskInputFormPreview() {
    MauNgapainTheme {
        TaskInputForm(
            state = TaskUiState(),
            onEvent = {}
        )
    }
}