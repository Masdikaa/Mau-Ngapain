package com.masdika.maungapain.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.screen.component.CreateTaskButton
import com.masdika.maungapain.ui.screen.component.TaskInputForm
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import com.masdika.maungapain.ui.viewmodel.TaskUiEvent
import com.masdika.maungapain.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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
            tasks = state.tasks,
            onCreateTask = {
                viewModel.onEvent(TaskUiEvent.OnOpenCreateTaskForm)
//                viewModel.onEvent(TaskUiEvent.OnOpenUpdateTaskForm(dummyTask))
            },
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun TaskContent(
    tasks: List<TaskEntity>,
    onCreateTask: () -> Unit,
    onEvent: (TaskUiEvent) -> Unit
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
                .padding(10.dp)
        ) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(
                    items = tasks,
                    key = { task -> task.id }
                ) { task ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = { onEvent(TaskUiEvent.OnOpenUpdateTaskForm(task)) }
                            ),
                    ) {
                        Text("ID: ${task.id}")
                        Text("Title: ${task.title}")
                        Text("Description: ${task.description}")
                        Text("Priority: ${task.priority}")
                        Text("Is Complete: ${task.isComplete}")
                        Text("Date Created: ${sdf.format(task.createdAt)}")
                        Text("Date Modified: ${sdf.format(task.modifiedAt)}")
                    }
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
private fun TaskContentPreview() {
    MauNgapainTheme {
        val tasks = listOf(
            TaskEntity(
                title = "Task 1",
                description = "Description Task 1",
                priority = Priority.LOW
            ),
            TaskEntity(
                title = "Task 2",
                description = "Description Task 2",
                priority = Priority.HIGH
            ),
            TaskEntity(
                title = "Task 3",
                description = "Description Task 3",
                priority = Priority.MEDIUM
            )
        )
        TaskContent(
            tasks = tasks,
            onCreateTask = {},
            onEvent = {}
        )
    }
}