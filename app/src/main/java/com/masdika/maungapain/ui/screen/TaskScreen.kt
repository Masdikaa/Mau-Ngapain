package com.masdika.maungapain.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.screen.component.CreateTaskButton
import com.masdika.maungapain.ui.screen.component.TaskInputForm
import com.masdika.maungapain.ui.screen.component.TaskList
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import com.masdika.maungapain.ui.viewmodel.TaskUiEvent
import com.masdika.maungapain.ui.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isFormVisible) {
        TaskInputForm(
            state = state,
            onTitleChange = { title ->
                viewModel.onEvent(TaskUiEvent.InputForm.TitleChanged(title))
            },
            onDescriptionChange = { description ->
                viewModel.onEvent(TaskUiEvent.InputForm.DescriptionChanged(description))
            },
            onPriorityChange = { priority ->
                viewModel.onEvent(TaskUiEvent.InputForm.PriorityChanged(priority))
            },
            onCancel = { viewModel.onEvent(TaskUiEvent.InputForm.Cancel) },
            onSave = { viewModel.onEvent(TaskUiEvent.InputForm.Save) },
            isUpdateMode = state.selectedTask != null,
        )
    } else {
        TaskContent(
            tasks = state.tasks,
            onCreateTask = { viewModel.onEvent(TaskUiEvent.FormNavigation.OpenCreateForm) },
            onEditTask = { task -> viewModel.onEvent(TaskUiEvent.FormNavigation.OpenUpdateForm(task)) },
            onToggleComplete = { task ->
                viewModel.onEvent(
                    TaskUiEvent.TaskAction.ToggleComplete(
                        task
                    )
                )
            },
            modifier = modifier
        )
    }
}

@Composable
fun TaskContent(
    tasks: List<TaskEntity>,
    onCreateTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            CreateTaskButton(
                onCreateTask = onCreateTask
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(10.dp)
        ) {
            TaskList(
                tasks = tasks,
                onEditTask = { task -> onEditTask(task) },
                onToggleComplete = { task -> onToggleComplete(task) },
            )
        }
    }
}


@Preview(
    showBackground = true,
    device = PIXEL_9_PRO,
    showSystemUi = true
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
            onEditTask = {},
            onToggleComplete = {}
        )
    }
}