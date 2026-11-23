package com.example.autotrack.data.repository

import com.example.autotrack.data.database.dao.MaintenanceDao
import com.example.autotrack.data.entity.Maintenance
import kotlinx.coroutines.flow.Flow

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {
    fun getMaintenanceForCar(carId: Int): Flow<List<Maintenance>> {
        return maintenanceDao.getMaintenanceForCar(carId)
    }

    suspend fun getMaintenanceById(id: Int): Maintenance? {
        return maintenanceDao.getMaintenanceById(id)
    }

    suspend fun insertMaintenance(maintenance: Maintenance) {
        maintenanceDao.insertMaintenance(maintenance)
    }

    suspend fun updateMaintenance(maintenance: Maintenance) {
        maintenanceDao.updateMaintenance(maintenance)
    }

    suspend fun deleteMaintenance(maintenance: Maintenance) {
        maintenanceDao.deleteMaintenance(maintenance)
    }
}
