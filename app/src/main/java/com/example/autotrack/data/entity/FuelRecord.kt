package com.example.autotrack.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuel_records",
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
data class FuelRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val date: Long = System.currentTimeMillis(),
    val liters: Double = 0.0,
    val fuelType: String = "Gasoline",
    val price: Double = 0.0,
    val odometer: Int = 0
)
