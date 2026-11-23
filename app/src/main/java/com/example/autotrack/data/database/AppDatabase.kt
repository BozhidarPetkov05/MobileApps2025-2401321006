package com.example.autotrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autotrack.data.database.dao.CarDao
import com.example.autotrack.data.database.dao.FuelRecordDao
import com.example.autotrack.data.database.dao.MaintenanceDao
import com.example.autotrack.data.database.dao.ReminderDao
import com.example.autotrack.data.entity.Car
import com.example.autotrack.data.entity.FuelRecord
import com.example.autotrack.data.entity.Maintenance
import com.example.autotrack.data.entity.Reminder

@Database(
    entities = [Car::class, FuelRecord::class, Maintenance::class, Reminder::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun fuelRecordDao(): FuelRecordDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autotrack_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
