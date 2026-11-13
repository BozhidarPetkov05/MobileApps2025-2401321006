package com.example.autotrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autotrack.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE carId = :carId ORDER BY dueDate ASC")
    fun getRemindersForCar(carId: Int): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}
