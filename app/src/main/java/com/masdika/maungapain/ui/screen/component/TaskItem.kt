package com.masdika.maungapain.ui.screen.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masdika.maungapain.data.local.entity.TaskEntity
import com.masdika.maungapain.data.local.enum.Priority
import com.masdika.maungapain.ui.theme.DarkGreen
import com.masdika.maungapain.ui.theme.LightGreen
import com.masdika.maungapain.ui.theme.MauNgapainTheme

@Composable
fun TaskItem(
    task: TaskEntity,
    onEditTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
) {
    val checkBackgroundColor = if (task.isComplete) LightGreen else Color.Gray
    val checkIconTint = if (task.isComplete) DarkGreen else Color.LightGray
    var isExpanded by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEditTask(task) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            PriorityIcon(
                priority = task.priority,
                modifier = Modifier
                    .size(26.dp)
                    .offset(y = 4.dp)
            )
            Spacer(Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(0.78f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (task.isComplete) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (task.description != null) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textLayoutResult ->
                                if (!isExpanded) {
                                    isClickable = textLayoutResult.didOverflowHeight
                                }
                            }
                        )
                    }
                }
            }
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.weight(0.22f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isClickable || isExpanded) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(
                                    shape = CircleShape
                                )
                                .clickable(
                                    onClick = { isExpanded = !isExpanded }
                                )
                                .aspectRatio(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand icon",
                                modifier = Modifier
                                    .size(32.dp)
                                    .rotate(rotationState)
                            )
                        }
                    }
                    Spacer(Modifier.width(5.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(
                                shape = CircleShape
                            )
                            .clickable(
                                onClick = { onToggleComplete(task) }
                            )
                            .background(checkBackgroundColor)
                            .aspectRatio(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "Finish task check icon",
                            modifier = Modifier.size(26.dp),
                            tint = checkIconTint
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskItemPreview() {
    MauNgapainTheme {
        val task = TaskEntity(
            title = "Finishing Android Project Mau Ngapain Today",
            description = "Implement delete logic in ViewModel \nTest \nLong \nDescription",
            priority = Priority.HIGH,
            isComplete = true
        )
        TaskItem(
            task = task,
            onEditTask = {},
            onToggleComplete = {}
        )
    }
}