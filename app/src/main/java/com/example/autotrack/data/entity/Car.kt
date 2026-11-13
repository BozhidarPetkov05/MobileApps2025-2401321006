package com.example.autotrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val mileage: Int,
    val imageUrl: String? = null
)
