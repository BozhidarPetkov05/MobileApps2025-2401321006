package com.example.autotrack.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_records",
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
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val title: String,
    val description: String? = null,
    val date: Long,
    val cost: Double,
    val odometer: Int
)
