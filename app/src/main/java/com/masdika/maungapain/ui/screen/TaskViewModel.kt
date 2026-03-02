package com.masdika.maungapain.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    private val _uiSideEffect = Channel<TaskUiSideEffect>()
    val uiSideEffect = _uiSideEffect.receiveAsFlow()

    init {
        observeTasks()
    }

    // Single Entry-Point for Event
    fun onEvent(event: TaskUiEvent) {
        when (event) {
            is TaskUiEvent.InputForm -> {
                when (event) {
                    is TaskUiEvent.InputForm.TitleChanged -> handleTitleInput(event.title)
                    is TaskUiEvent.InputForm.DescriptionChanged -> handleDescriptionInput(event.description)
                    is TaskUiEvent.InputForm.PriorityChanged -> handlePriorityInput(event.priority)
                    is TaskUiEvent.InputForm.Save -> handleSaveAction()
                    is TaskUiEvent.InputForm.Cancel -> closeTaskInputForm()
                }
            }

            is TaskUiEvent.FormNavigation -> {
                when (event) {
                    is TaskUiEvent.FormNavigation.OpenCreateForm -> openCreateTaskInputForm()
                    is TaskUiEvent.FormNavigation.OpenUpdateForm -> openUpdateTaskInputForm(event.task)
                }
            }

            is TaskUiEvent.TaskAction -> {
                when (event) {
                    is TaskUiEvent.TaskAction.ToggleComplete -> toggleTaskComplete(event.task)
                    is TaskUiEvent.TaskAction.Delete -> deleteTask(event.task)
                }
            }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(loading = true) }

            repository.getAllTasks()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            isError = true,
                            errorMessage = exception.message
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { taskList ->
                    _uiState.update {
                        it.copy(
                            tasks = taskList,
                            loading = false,
                        )
                    }
                }
        }
    }

    private fun handleSaveAction() {
        _uiState.update { currentState ->
            if (currentState.selectedTask != null) {
                updateTask(
                    task = currentState.selectedTask,
                    title = currentState.taskTitleInput,
                    description = currentState.taskDescriptionInput,
                    priority = currentState.taskPriorityInput,
                )
            } else {
                saveTask(
                    title = currentState.taskTitleInput,
                    description = currentState.taskDescriptionInput,
                    priority = currentState.taskPriorityInput
                )
            }
            currentState
        }
    }

    private fun openCreateTaskInputForm() {
        _uiState.update {
            it.copy(
                isFormVisible = true,
                selectedTask = null
            )
        }
    }

    private fun openUpdateTaskInputForm(task: TaskEntity) {
        _uiState.update {
            it.copy(
                isFormVisible = true,
                selectedTask = task,
                taskTitleInput = task.title,
                taskDescriptionInput = task.description ?: "",
                taskPriorityInput = task.priority
            )
        }
    }

    private fun closeTaskInputForm() {
        _uiState.update {
            it.copy(
                isFormVisible = false,
                taskTitleInput = "",
                taskDescriptionInput = "",
                taskPriorityInput = Priority.MEDIUM,
                titleError = null,
                descriptionError = null,
                isInputValid = false
            )
        }
    }

    private fun toggleTaskComplete(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(
                isComplete = !task.isComplete,
                modifiedAt = System.currentTimeMillis()
            )
            repository.updateTask(updatedTask)
        }
    }

    private fun saveTask(
        title: String,
        description: String,
        priority: Priority
    ) {
        if (_uiState.value.actionLoading) return

        val titleTrim = title.trim()
        val descriptionTrim = description.trim().takeIf { it.isNotBlank() }

        val newTask = TaskEntity(
            title = titleTrim,
            description = descriptionTrim,
            priority = priority
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.insertTask(newTask)
                closeTaskInputForm()
                _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully saved task \"${newTask.title}\""))
            } catch (e: Exception) {
                _uiState.update { it.copy(isError = true, errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
        }
    }

    private fun updateTask(
        task: TaskEntity,
        title: String,
        description: String,
        priority: Priority,
        modifiedAt: Long = System.currentTimeMillis()
    ) {
        if (_uiState.value.actionLoading) return

        val titleTrim = title.trim()
        val descriptionTrim = description.trim().takeIf { it.isNotBlank() }

        val updatedTask = task.copy(
            title = titleTrim,
            description = descriptionTrim,
            priority = priority,
            modifiedAt = modifiedAt
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.updateTask(updatedTask)
                closeTaskInputForm()
                _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully updated task \"${updatedTask.title}\""))
            } catch (e: Exception) {
                _uiState.update { it.copy(isError = true, errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
        }
    }

    private fun deleteTask(task: TaskEntity) {
        if (_uiState.value.actionLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.deleteTask(task)
                _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully deleted task \"${task.title}\""))
            } catch (e: Exception) {
                _uiState.update { it.copy(isError = true, errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
        }
    }

    private fun handleTitleInput(newTitle: String) {
        if (newTitle.length <= 50) {
            _uiState.update {
                it.copy(
                    taskTitleInput = newTitle,
                    titleError = if (newTitle.isBlank()) "Title cannot be empty" else null
                )
            }
            validateInput()
        }
    }

    private fun handleDescriptionInput(newDescription: String) {
        if (newDescription.length <= 500) {
            _uiState.update {
                it.copy(
                    taskDescriptionInput = newDescription,
                )
            }
            validateInput()
        }
    }

    private fun handlePriorityInput(newPriority: Priority) {
        _uiState.update {
            it.copy(
                taskPriorityInput = newPriority
            )
        }
    }

    private fun validateInput() {
        _uiState.update {
            it.copy(
                isInputValid = it.taskTitleInput.isNotBlank() && it.titleError == null && it.descriptionError == null
            )
        }
    }
}