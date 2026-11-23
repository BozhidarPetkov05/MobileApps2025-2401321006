package com.example.autotrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autotrack.data.entity.Maintenance
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId ORDER BY date DESC")
    fun getMaintenanceForCar(carId: Int): Flow<List<Maintenance>>

    @Query("SELECT * FROM maintenance_records WHERE carId = :carId")
    suspend fun getMaintenanceList(carId: Int): List<Maintenance>

    @Query("SELECT * FROM maintenance_records WHERE id = :id")
    suspend fun getMaintenanceById(id: Int): Maintenance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: Maintenance)

    @Update
    suspend fun updateMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)
}
