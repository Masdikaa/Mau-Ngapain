package com.masdika.maungapain.ui.screen

sealed interface TaskUiSideEffect {
    data class ShowSnackBar(val message: String) : TaskUiSideEffect
}