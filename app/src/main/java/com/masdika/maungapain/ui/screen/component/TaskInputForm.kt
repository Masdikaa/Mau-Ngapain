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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import com.masdika.maungapain.ui.viewmodel.TaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputForm(
    state: TaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isUpdateMode: Boolean
) {
//    val isUpdateMode = state.selectedTask != null
    val selectedPriorityIndex = state.taskPriorityInput.ordinal

    Dialog(
        onDismissRequest = onCancel,
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
                onValueChange = onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.taskDescriptionInput,
                onValueChange = onDescriptionChange,
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
                        onClick = { onPriorityChange(priority) },
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
                    onClick = onCancel,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(15.dp))
                Button(
                    onClick = onSave,
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
            onTitleChange = {},
            onDescriptionChange = {},
            onPriorityChange = {},
            onSave = {},
            onCancel = {},
            isUpdateMode = false,
        )
    }
}