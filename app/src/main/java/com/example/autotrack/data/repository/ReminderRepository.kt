package com.example.autotrack.data.repository

import com.example.autotrack.data.database.dao.ReminderDao
import com.example.autotrack.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getRemindersForCar(carId: Int): Flow<List<Reminder>> {
        return reminderDao.getRemindersForCar(carId)
    }

    fun getActiveReminders(): Flow<List<Reminder>> {
        return reminderDao.getActiveReminders()
    }

    suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}
