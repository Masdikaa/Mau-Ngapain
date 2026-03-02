package com.masdika.maungapain.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.screen.component.CreateTaskButton
import com.masdika.maungapain.ui.screen.component.EmptyState
import com.masdika.maungapain.ui.screen.component.ErrorState
import com.masdika.maungapain.ui.screen.component.TaskInputForm
import com.masdika.maungapain.ui.screen.component.TaskList
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is TaskUiSideEffect.ShowSnackBar -> {
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    // Single Scaffold Principle
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    shape = MaterialTheme.shapes.small,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    snackbarData = data
                )
            }
        },
        floatingActionButton = {
            if (!state.isError && !state.isFormVisible && !state.actionLoading) {
                CreateTaskButton(
                    onCreateTask = { viewModel.onEvent(TaskUiEvent.FormNavigation.OpenCreateForm) }
                )
            }
        },
    ) { innerPadding ->
        if (state.isError) {
            ErrorState(
                errorMessage = state.errorMessage,
                modifier = Modifier.padding(innerPadding)
            )
        } else if (state.isFormVisible) {
            TaskInputForm(
                state = state,
                onTitleChange = { title ->
                    viewModel.onEvent(TaskUiEvent.InputForm.TitleChanged(title))
                },
                onDescriptionChange = { description ->
                    viewModel.onEvent(
                        TaskUiEvent.InputForm.DescriptionChanged(
                            description
                        )
                    )
                },
                onPriorityChange = { priority ->
                    viewModel.onEvent(TaskUiEvent.InputForm.PriorityChanged(priority))
                },
                onCancel = { viewModel.onEvent(TaskUiEvent.InputForm.Cancel) },
                onSave = { viewModel.onEvent(TaskUiEvent.InputForm.Save) },
                isUpdateMode = state.selectedTask != null,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            TaskContent(
                tasks = state.tasks,
                isLoading = state.loading,
                isActionLoading = state.actionLoading,
                onEditTask = { task ->
                    viewModel.onEvent(
                        TaskUiEvent.FormNavigation.OpenUpdateForm(
                            task
                        )
                    )
                },
                onToggleComplete = { task ->
                    viewModel.onEvent(
                        TaskUiEvent.TaskAction.ToggleComplete(
                            task
                        )
                    )
                },
                onDeleteTask = { task ->
                    viewModel.onEvent(
                        TaskUiEvent.TaskAction.Delete(
                            task
                        )
                    )
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun TaskContent(
    tasks: List<TaskEntity>,
    isLoading: Boolean,
    isActionLoading: Boolean,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        if (isLoading && tasks.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        } else {
            AnimatedContent(
                targetState = tasks.isEmpty(),
                transitionSpec = {
                    (
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                },
                label = "TaskContentTransition"
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyState()
                } else {
                    Column {
                        Text(
                            text = "Task List",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        ) {
                            if (isActionLoading) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                HorizontalDivider(
                                    thickness = 2.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        TaskList(
                            tasks = tasks,
                            isActionLoading = isActionLoading,
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
        Scaffold(modifier = Modifier.fillMaxSize()) {
            TaskContent(
                tasks = tasks,
                isLoading = false,
                isActionLoading = false,
                onEditTask = {},
                onToggleComplete = {},
                onDeleteTask = {},
                modifier = Modifier.padding(it)
            )
        }
    }
}