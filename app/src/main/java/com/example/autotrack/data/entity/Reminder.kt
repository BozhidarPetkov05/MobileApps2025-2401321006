package com.example.autotrack.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId")]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val title: String,
    val dueDate: Long,
    val dueMileage: Int? = null,
    val isCompleted: Boolean = false
)
