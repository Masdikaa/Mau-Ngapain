package com.masdika.maungapain.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            isLoading = state.loading,
            onCreateTask = { viewModel.onEvent(TaskUiEvent.FormNavigation.OpenCreateForm) },
            onEditTask = { task -> viewModel.onEvent(TaskUiEvent.FormNavigation.OpenUpdateForm(task)) },
            onToggleComplete = { task ->
                viewModel.onEvent(
                    TaskUiEvent.TaskAction.ToggleComplete(
                        task
                    )
                )
            },
            onDeleteTask = { task -> viewModel.onEvent(TaskUiEvent.TaskAction.Delete(task)) },
            modifier = modifier
        )
    }
}

@Composable
fun TaskContent(
    tasks: List<TaskEntity>,
    isLoading: Boolean,
    onCreateTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            if (!isLoading) {
                CreateTaskButton(
                    onCreateTask = onCreateTask
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                }

                else -> {
                    if (tasks.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "There's nothing Task to Show, Please create a Task! 😁",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = "Task List",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        TaskList(
                            tasks = tasks,
                            onEditTask = { task -> onEditTask(task) },
                            onToggleComplete = { task -> onToggleComplete(task) },
                            onDeleteTask = { task -> onDeleteTask(task) },
                        )
                    }
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    device = PIXEL_9_PRO,
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
            isLoading = false,
            onCreateTask = {},
            onEditTask = {},
            onToggleComplete = {},
            onDeleteTask = {}
        )
    }
}