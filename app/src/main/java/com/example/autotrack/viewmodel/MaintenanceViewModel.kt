package com.example.autotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.database.AppDatabase
import com.example.autotrack.data.entity.Maintenance
import com.example.autotrack.data.entity.Reminder
import com.example.autotrack.data.repository.MaintenanceRepository
import com.example.autotrack.data.repository.ReminderRepository
import com.example.autotrack.datastore.PreferencesManager
import com.example.autotrack.util.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val maintenanceRepository = MaintenanceRepository(database.maintenanceDao())
    private val reminderRepository = ReminderRepository(database.reminderDao())
    private val preferencesManager = PreferencesManager(application)

    val selectedCarId: StateFlow<Int?> = preferencesManager.selectedCarId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val maintenanceRecords: StateFlow<List<Maintenance>> = selectedCarId
        .flatMapLatest { carId ->
            if (carId != null && carId > 0) {
                maintenanceRepository.getMaintenanceForCar(carId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val reminders: StateFlow<List<Reminder>> = selectedCarId
        .flatMapLatest { carId ->
            if (carId != null && carId > 0) {
                reminderRepository.getRemindersForCar(carId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getMaintenanceById(id: Int): Maintenance? = maintenanceRepository.getMaintenanceById(id)
    suspend fun getReminderById(id: Int): Reminder? = reminderRepository.getReminderById(id)

    private suspend fun updateCarMileageIfNeeded(carId: Int) {
        val car = database.carDao().getCarById(carId) ?: return
        val fuelRecords = database.fuelRecordDao().getFuelRecordsList(carId)
        val maintenanceRecords = database.maintenanceDao().getMaintenanceList(carId)

        val maxFuelOdom = fuelRecords.maxOfOrNull { it.odometer } ?: 0
        val maxMaintOdom = maintenanceRecords.maxOfOrNull { it.odometer } ?: 0
        val highestOdom = maxOf(car.mileage, maxFuelOdom, maxMaintOdom)

        if (highestOdom > car.mileage) {
            val updatedCar = car.copy(mileage = highestOdom)
            database.carDao().updateCar(updatedCar)
        }
    }

    fun addMaintenance(maintenance: Maintenance, onComplete: () -> Unit) {
        viewModelScope.launch {
            maintenanceRepository.insertMaintenance(maintenance)
            updateCarMileageIfNeeded(maintenance.carId)
            onComplete()
        }
    }

    fun updateMaintenance(maintenance: Maintenance, onComplete: () -> Unit) {
        viewModelScope.launch {
            maintenanceRepository.updateMaintenance(maintenance)
            updateCarMileageIfNeeded(maintenance.carId)
            onComplete()
        }
    }

    fun deleteMaintenance(maintenance: Maintenance, onComplete: () -> Unit) {
        viewModelScope.launch {
            maintenanceRepository.deleteMaintenance(maintenance)
            onComplete()
        }
    }

    fun addReminder(reminder: Reminder, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
            ReminderScheduler.scheduleReminder(
                getApplication(),
                reminder.hashCode(),
                reminder.title,
                reminder.dueDate
            )
            onComplete()
        }
    }

    fun updateReminder(reminder: Reminder, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepository.updateReminder(reminder)
            ReminderScheduler.scheduleReminder(
                getApplication(),
                reminder.hashCode(),
                reminder.title,
                reminder.dueDate
            )
            onComplete()
        }
    }

    fun deleteReminder(reminder: Reminder, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            ReminderScheduler.cancelReminder(getApplication(), reminder.hashCode())
            onComplete()
        }
    }
}
