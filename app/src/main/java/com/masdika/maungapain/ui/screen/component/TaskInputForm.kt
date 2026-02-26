package com.masdika.maungapain.ui.screen.component

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
    val isUpdateMode = state.selectedTask != null
    val selectedPriorityIndex = state.taskPriorityInput.ordinal

    Dialog(
        onDismissRequest = { onEvent(TaskUiEvent.OnCloseForm) },
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
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
            Text(
                text = "Priority",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp)
            )
            Spacer(Modifier.height(2.dp))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Priority.entries.forEachIndexed { index, priority ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = Priority.entries.size,
                            baseShape = RoundedCornerShape(8.dp)
                        ),
                        onClick = {
                            onEvent(TaskUiEvent.OnPriorityInputChange(priority))
                            val logMessage = """
                               selectedPriorityIndex : $selectedPriorityIndex
                               currentTaskIndex      : ${state.selectedTask?.priority?.ordinal}
                               index and priority    : $index | $priority
                            """
                            Log.i("PrioritySelection", logMessage)
                        },
                        selected = index == selectedPriorityIndex,
                        enabled = true,
                        icon = {
                            if (index == selectedPriorityIndex) {
                                PriorityIcon(priority)
                            }
                        }
                    ) {
                        Text(text = priority.name)
                    }
                }
            }

            Spacer(Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { onEvent(TaskUiEvent.OnCloseForm) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(15.dp))
                Button(
                    onClick = {
                        if (isUpdateMode) {
                            onEvent(
                                TaskUiEvent.OnUpdateTask(
                                    task = state.selectedTask,
                                    title = state.taskTitleInput,
                                    priority = state.taskPriorityInput,
                                    description = state.taskDescriptionInput,
                                )
                            )
                            Log.i("Update Button", "${state.selectedTask}")
                        } else {
                            onEvent(
                                TaskUiEvent.OnSaveTask(
                                    title = state.taskTitleInput,
                                    priority = state.taskPriorityInput,
                                    description = state.taskDescriptionInput
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isUpdateMode) "Save Update" else "Save"
                    )
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