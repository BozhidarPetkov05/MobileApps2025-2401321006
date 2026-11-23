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

    fun addMaintenance(maintenance: Maintenance, onComplete: () -> Unit) {
        viewModelScope.launch {
            maintenanceRepository.insertMaintenance(maintenance)
            onComplete()
        }
    }

    fun updateMaintenance(maintenance: Maintenance, onComplete: () -> Unit) {
        viewModelScope.launch {
            maintenanceRepository.updateMaintenance(maintenance)
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
            onComplete()
        }
    }

    fun updateReminder(reminder: Reminder, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepository.updateReminder(reminder)
            onComplete()
        }
    }

    fun deleteReminder(reminder: Reminder, onComplete: () -> Unit) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            onComplete()
        }
    }
}
