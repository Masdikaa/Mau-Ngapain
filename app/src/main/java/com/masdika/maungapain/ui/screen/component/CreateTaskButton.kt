package com.masdika.maungapain.ui.screen.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun CreateTaskButton(
    onCreateTask: () -> Unit,
) {
    FloatingActionButton(
        onClick = onCreateTask,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add task icon"
        )
    }
}