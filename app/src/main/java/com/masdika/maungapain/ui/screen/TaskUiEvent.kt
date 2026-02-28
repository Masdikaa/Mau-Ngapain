package com.masdika.maungapain.ui.screen

import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority

sealed interface TaskUiEvent {

    sealed interface InputForm : TaskUiEvent {
        data class TitleChanged(val title: String) : InputForm
        data class DescriptionChanged(val description: String) : InputForm
        data class PriorityChanged(val priority: Priority) : InputForm
        object Save : InputForm
        object Cancel : InputForm
    }

    sealed interface FormNavigation : TaskUiEvent {
        object OpenCreateForm : FormNavigation
        data class OpenUpdateForm(val task: TaskEntity) : FormNavigation
    }

    sealed interface TaskAction : TaskUiEvent {
        data class ToggleComplete(val task: TaskEntity) : TaskAction
        data class Delete(val task: TaskEntity) : TaskAction
    }
}