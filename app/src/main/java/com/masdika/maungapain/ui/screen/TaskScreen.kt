package com.masdika.maungapain.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.screen.component.CreateTaskButton
import com.masdika.maungapain.ui.screen.component.TaskInputForm
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import com.masdika.maungapain.ui.viewmodel.TaskUiEvent
import com.masdika.maungapain.ui.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val dummyTask = TaskEntity(
        title = "Dummy Title",
        description = "Dummy Description",
        priority = Priority.HIGH,
    )

    if (state.isFormVisible) {
        TaskInputForm(
            state = state,
            onEvent = viewModel::onEvent,
        )
    } else {
        TaskContent(
            onCreateTask = {
                viewModel.onEvent(TaskUiEvent.OnOpenCreateTaskForm)
//                viewModel.onEvent(TaskUiEvent.OnOpenUpdateTaskForm(dummyTask))
            }
        )
    }
}

@Composable
fun TaskContent(
    onCreateTask: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            CreateTaskButton(
                onCreateTask = onCreateTask
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(
                text = "Task Screen",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 425,
    heightDp = 944,
)
@Composable
private fun TaskContentPreview() {
    MauNgapainTheme {
        TaskContent(
            onCreateTask = {}
        )
    }
}