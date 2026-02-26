package com.masdika.maungapain.ui.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.theme.MauNgapainTheme

@Composable
fun TaskList(
    tasks: List<TaskEntity>,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            TaskItem(
                task = task,
                onEditTask = { onEditTask(task) },
                onToggleComplete = { onToggleComplete(task) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListPreview() {
    MauNgapainTheme {
        val tasks = listOf(
            TaskEntity(
                title = "Task 1",
                description = "Description Task 1",
                priority = Priority.LOW
            ),
            TaskEntity(
                title = "Task 2",
                description = "Description Task 2",
                priority = Priority.HIGH
            ),
            TaskEntity(
                title = "Task 3",
                description = null,
                priority = Priority.MEDIUM
            )
        )
        TaskList(
            tasks = tasks,
            onEditTask = {},
            onToggleComplete = {}
        )
    }
}
