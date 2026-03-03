package com.masdika.maungapain.ui.screen.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.theme.MauNgapainTheme

@Composable
fun TaskList(
    tasks: List<TaskEntity>,
    isActionLoading: Boolean,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 40.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            var isDismissed by remember { mutableStateOf(false) }

            val dismissState = rememberSwipeToDismissBoxState()
            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart && !isDismissed) {
                    onDeleteTask(task)
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromEndToStart = !isActionLoading,
                enableDismissFromStartToEnd = false,
                modifier = Modifier
                    .animateItem()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                backgroundContent = {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Delete\n${task.title}",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.End
                            )
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Finish task checkbox",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(end = 10.dp)
                            )
                        }
                    }
                }
            ) {
                TaskItem(
                    task = task,
                    onEditTask = { onEditTask(task) },
                    onToggleComplete = { onToggleComplete(task) }
                )
            }
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
                description = "Description Task 1 \nTest \nLong \nDescription",
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
            isActionLoading = false,
            onEditTask = {},
            onToggleComplete = {},
            onDeleteTask = {},
            listState = rememberLazyListState()
        )
    }
}
