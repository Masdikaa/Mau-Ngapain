package com.masdika.maungapain.data.local

import androidx.room.TypeConverter
import com.masdika.maungapain.data.local.enum.Priority
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromPriorityToInt(priority: Priority): Int {
        return priority.code
    }

    @TypeConverter
    fun fromIntToPriority(value: Int): Priority {
        return Priority.fromInt(value)
    }

    @TypeConverter
    fun fromUUIDToString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun fromStringToUUID(value: String): UUID {
        return UUID.fromString(value)
    }
}