package com.masdika.maungapain.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masdika.maungapain.data.local.enum.Priority
import java.util.UUID

@Entity(tableName = "task-table")
data class TaskEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String?,
    val priority: Priority,
    val isComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)