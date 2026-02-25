package com.masdika.maungapain.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.masdika.maungapain.ui.viewmodel.TaskUiEvent
import com.masdika.maungapain.ui.viewmodel.TaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputForm(
    state: TaskUiState,
    onEvent: (TaskUiEvent) -> Unit
) {
    Dialog(
        onDismissRequest = { onEvent(TaskUiEvent.OnCloseForm) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(
                    shape = RoundedCornerShape(percent = 8)
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 10.dp, vertical = 15.dp)
        ) {
            OutlinedTextField(
                value = state.taskTitleInput,
                onValueChange = { onEvent(TaskUiEvent.OnTitleInputChange(it)) },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
            OutlinedTextField(
                value = state.taskDescriptionInput,
                onValueChange = { onEvent(TaskUiEvent.OnDescriptionInputChange(it)) },
                label = { Text("Description") },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }
}