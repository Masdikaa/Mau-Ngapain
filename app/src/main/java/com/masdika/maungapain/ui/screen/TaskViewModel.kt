package com.masdika.maungapain.ui.screen

import android.util.Log
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
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
                taskPriorityInput = Priority.MEDIUM
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

        val newTask = TaskEntity(
            title = title,
            description = description,
            priority = priority
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.insertTask(newTask)
                closeTaskInputForm()
                Log.i("ViewModel-saveTask", "Saved ${newTask.title} - ${Date()}")
            } catch (e: Exception) {
                Log.e("ViewModel-saveTask", e.message.toString())
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
            _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully saved task \"${newTask.title}\""))
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

        val updatedTask = task.copy(
            title = title,
            description = description,
            priority = priority,
            modifiedAt = modifiedAt
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.updateTask(updatedTask)
                closeTaskInputForm()
                Log.i(
                    "ViewModel-updateTask",
                    "Updated ${task.title} to ${updatedTask.title} - ${Date()}"
                )
            } catch (e: Exception) {
                Log.e("ViewModel-updateTask", e.message.toString())
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
            _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully updated task \"${updatedTask.title}\""))
        }
    }

    private fun deleteTask(task: TaskEntity) {
        if (_uiState.value.actionLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(actionLoading = true) }
            try {
                repository.deleteTask(task)
                Log.i("ViewModel-deleteTask", "Deleted ${task.title} - ${Date()}")
            } catch (e: Exception) {
                Log.e("ViewModel-deleteTask", e.message.toString())
            } finally {
                _uiState.update { it.copy(actionLoading = false) }
            }
            _uiSideEffect.trySend(TaskUiSideEffect.ShowSnackBar("Successfully deleted task \"${task.title}\""))
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