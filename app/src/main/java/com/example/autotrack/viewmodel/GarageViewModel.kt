package com.example.autotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.database.AppDatabase
import com.example.autotrack.data.entity.Car
import com.example.autotrack.data.repository.CarRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GarageViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val carRepository = CarRepository(database.carDao())

    val cars: StateFlow<List<Car>> = carRepository.allCars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getCarById(carId: Int): Car? {
        return carRepository.getCarById(carId)
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }
}
