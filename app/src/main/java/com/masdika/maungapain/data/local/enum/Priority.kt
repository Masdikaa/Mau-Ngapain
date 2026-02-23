package com.masdika.maungapain.data.local.enum

enum class Priority(val code: Int) {
    HIGH(1),
    MEDIUM(2),
    LOW(3);

    companion object {
        fun fromInt(value: Int): Priority {
            return entries.find { it.code == value } ?: LOW
        }
    }
}