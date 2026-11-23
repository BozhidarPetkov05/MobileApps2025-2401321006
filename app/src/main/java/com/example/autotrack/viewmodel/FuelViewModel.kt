package com.example.autotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.database.AppDatabase
import com.example.autotrack.data.entity.FuelRecord
import com.example.autotrack.data.repository.FuelRepository
import com.example.autotrack.datastore.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FuelViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val fuelRepository = FuelRepository(database.fuelRecordDao())
    private val preferencesManager = PreferencesManager(application)

    val selectedCarId: StateFlow<Int?> = preferencesManager.selectedCarId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelRecords: StateFlow<List<FuelRecord>> = selectedCarId
        .flatMapLatest { carId ->
            if (carId != null && carId > 0) {
                fuelRepository.getFuelRecordsForCar(carId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getFuelRecordById(id: Int): FuelRecord? {
        return fuelRepository.getFuelRecordById(id)
    }

    fun addFuelRecord(fuelRecord: FuelRecord, onComplete: () -> Unit) {
        viewModelScope.launch {
            fuelRepository.insertFuelRecord(fuelRecord)
            onComplete()
        }
    }

    fun updateFuelRecord(fuelRecord: FuelRecord, onComplete: () -> Unit) {
        viewModelScope.launch {
            fuelRepository.updateFuelRecord(fuelRecord)
            onComplete()
        }
    }

    fun deleteFuelRecord(fuelRecord: FuelRecord, onComplete: () -> Unit) {
        viewModelScope.launch {
            fuelRepository.deleteFuelRecord(fuelRecord)
            onComplete()
        }
    }
}
