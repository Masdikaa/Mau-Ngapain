package com.masdika.maungapain.ui.viewmodel

import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority

sealed interface TaskUiEvent {

    data class OnTitleInputChange(val value: String) : TaskUiEvent
    data class OnDescriptionInputChange(val value: String) : TaskUiEvent
    data class OnPriorityInputChange(val priority: Priority) : TaskUiEvent

    object OnOpenCreateTaskForm : TaskUiEvent
    data class OnOpenUpdateTaskForm(val task: TaskEntity) : TaskUiEvent
    object OnCloseForm : TaskUiEvent

    data class OnSaveTask(
        val title: String,
        val priority: Priority,
        val description: String,
    ) : TaskUiEvent

    data class OnDeleteTask(
        val task: TaskEntity
    ) : TaskUiEvent

    data class OnUpdateTask(
        val task: TaskEntity,
        val title: String,
        val priority: Priority,
        val description: String,
    ) : TaskUiEvent
}