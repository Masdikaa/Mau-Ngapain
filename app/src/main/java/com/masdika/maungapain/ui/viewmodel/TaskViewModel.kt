package com.masdika.maungapain.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    // Single Entry-Point for Event
    fun onEvent(event: TaskUiEvent) {
        when (event) {
            is TaskUiEvent.OnOpenCreateTaskForm -> openCreateTaskInputForm()

            is TaskUiEvent.OnOpenUpdateTaskForm -> openUpdateTaskInputForm(event.task)

            is TaskUiEvent.OnPriorityInputChange -> handlePriorityInput(event.priority)

            is TaskUiEvent.OnCloseForm -> closeTaskInputForm()

            is TaskUiEvent.OnTitleInputChange -> handleTitleInput(event.value)

            is TaskUiEvent.OnDescriptionInputChange -> handleDescriptionInput(event.value)

            is TaskUiEvent.OnSaveTask -> saveTask(
                title = event.title,
                description = event.description,
                priority = event.priority
            )

            is TaskUiEvent.OnUpdateTask -> updateTask(
                task = event.task,
                title = event.title,
                description = event.description,
                priority = event.priority,
            )

            is TaskUiEvent.OnDeleteTask -> {}
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
                }.collect { taskList ->
                    _uiState.update {
                        it.copy(
                            tasks = taskList,
                            loading = false,
                            isError = false,
                            errorMessage = null
                        )
                    }
                }
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
                taskPriorityInput = Priority.MEDIUM
            )
        }
    }

    private fun saveTask(
        title: String,
        description: String,
        priority: Priority
    ) {
        val newTask = TaskEntity(
            title = title,
            description = description,
            priority = priority
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(newTask)
            closeTaskInputForm()
        }
    }

    private fun updateTask(
        task: TaskEntity,
        title: String,
        description: String,
        priority: Priority,
        isComplete: Boolean = true,
        modifiedAt: Long = System.currentTimeMillis()
    ) {
        val updatedTask = task.copy(
            title = title,
            description = description,
            priority = priority,
            isComplete = isComplete,
            modifiedAt = modifiedAt
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(updatedTask)
            closeTaskInputForm()
        }
    }

    private fun handleTitleInput(newTitle: String) {
        _uiState.update {
            it.copy(
                taskTitleInput = newTitle
            )
        }
    }

    private fun handleDescriptionInput(newTitle: String) {
        _uiState.update {
            it.copy(
                taskDescriptionInput = newTitle
            )
        }
    }

    private fun handlePriorityInput(newPriority: Priority) {
        _uiState.update {
            it.copy(
                taskPriorityInput = newPriority
            )
        }
    }
}