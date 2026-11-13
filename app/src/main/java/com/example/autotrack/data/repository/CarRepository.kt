package com.example.autotrack.data.repository

import com.example.autotrack.data.database.dao.CarDao
import com.example.autotrack.data.entity.Car
import kotlinx.coroutines.flow.Flow

class CarRepository(private val carDao: CarDao) {
    val allCars: Flow<List<Car>> = carDao.getAllCars()

    suspend fun getCarById(carId: Int): Car? {
        return carDao.getCarById(carId)
    }

    suspend fun insertCar(car: Car): Long {
        return carDao.insertCar(car)
    }

    suspend fun updateCar(car: Car) {
        carDao.updateCar(car)
    }

    suspend fun deleteCar(car: Car) {
        carDao.deleteCar(car)
    }
}
