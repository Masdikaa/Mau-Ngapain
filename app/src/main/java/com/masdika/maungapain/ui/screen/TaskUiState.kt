package com.masdika.maungapain.ui.screen

import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority

data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val selectedTask: TaskEntity? = null,
    val taskTitleInput: String = "",
    val taskDescriptionInput: String = "",
    val taskPriorityInput: Priority = Priority.MEDIUM,
    val isFormVisible: Boolean = false,
    val loading: Boolean = false,
    val actionLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)