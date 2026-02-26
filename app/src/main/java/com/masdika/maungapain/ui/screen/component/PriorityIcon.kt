package com.masdika.maungapain.ui.screen.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.masdika.maungapain.data.local.enum.Priority

@Composable
fun PriorityIcon(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val iconColor = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color.Green
    }

    Icon(
        imageVector = Icons.Default.Flag,
        contentDescription = "Priority ${priority.name}",
        tint = iconColor,
        modifier = modifier
    )
}
