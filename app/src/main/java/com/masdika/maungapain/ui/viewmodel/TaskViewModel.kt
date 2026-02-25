package com.masdika.maungapain.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

            is TaskUiEvent.OnSaveTask -> {}

            is TaskUiEvent.OnDeleteTask -> {}

            is TaskUiEvent.OnUpdateTask -> {}
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
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
                            task = taskList,
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
        val message: String = "Title Input: ${_uiState.value.taskTitleInput}\n" +
                "Description Input: ${_uiState.value.taskDescriptionInput}\n"
        Log.i("VIEWMODEL - openCreateTaskInputForm", message)
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
        val message: String = "Title Input: ${_uiState.value.taskTitleInput}\n" +
                "Description Input: ${_uiState.value.taskDescriptionInput}\n"
        Log.i("VIEWMODEL - openUpdateTaskInputForm", message)
    }

    private fun closeTaskInputForm() {
        _uiState.update {
            it.copy(
                isFormVisible = false,
                taskTitleInput = "",
                taskDescriptionInput = ""
            )
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