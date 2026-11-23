package com.example.autotrack.data.repository

import com.example.autotrack.data.database.dao.FuelRecordDao
import com.example.autotrack.data.entity.FuelRecord
import kotlinx.coroutines.flow.Flow

class FuelRepository(private val fuelRecordDao: FuelRecordDao) {
    fun getFuelRecordsForCar(carId: Int): Flow<List<FuelRecord>> {
        return fuelRecordDao.getFuelRecordsForCar(carId)
    }

    suspend fun getFuelRecordById(id: Int): FuelRecord? {
        return fuelRecordDao.getFuelRecordById(id)
    }

    suspend fun insertFuelRecord(fuelRecord: FuelRecord) {
        fuelRecordDao.insertFuelRecord(fuelRecord)
    }

    suspend fun updateFuelRecord(fuelRecord: FuelRecord) {
        fuelRecordDao.updateFuelRecord(fuelRecord)
    }

    suspend fun deleteFuelRecord(fuelRecord: FuelRecord) {
        fuelRecordDao.deleteFuelRecord(fuelRecord)
    }
}
