package com.example.autotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.database.AppDatabase
import com.example.autotrack.data.entity.Car
import com.example.autotrack.data.repository.CarRepository
import com.example.autotrack.datastore.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val carRepository = CarRepository(database.carDao())
    private val preferencesManager = PreferencesManager(application)

    val cars: StateFlow<List<Car>> = carRepository.allCars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCarId: StateFlow<Int?> = preferencesManager.selectedCarId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedCar: StateFlow<Car?> = combine(cars, selectedCarId) { carList, carId ->
        carList.find { it.id == carId } ?: carList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectCar(carId: Int) {
        viewModelScope.launch {
            preferencesManager.saveSelectedCarId(carId)
        }
    }

    fun addCar(car: Car, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = carRepository.insertCar(car)
            if (selectedCarId.value == null) {
                selectCar(id.toInt())
            }
            onComplete(id)
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            carRepository.updateCar(car)
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }
}
