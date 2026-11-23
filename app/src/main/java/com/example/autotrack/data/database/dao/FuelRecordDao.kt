package com.example.autotrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autotrack.data.entity.FuelRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelRecordDao {
    @Query("SELECT * FROM fuel_records WHERE carId = :carId ORDER BY date DESC")
    fun getFuelRecordsForCar(carId: Int): Flow<List<FuelRecord>>

    @Query("SELECT * FROM fuel_records WHERE id = :id")
    suspend fun getFuelRecordById(id: Int): FuelRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelRecord(fuelRecord: FuelRecord)

    @Update
    suspend fun updateFuelRecord(fuelRecord: FuelRecord)

    @Delete
    suspend fun deleteFuelRecord(fuelRecord: FuelRecord)
}
