package com.example.autotrack.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "autotrack_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        val SELECTED_CAR_ID_KEY = intPreferencesKey("selected_car_id")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode") // "SYSTEM", "LIGHT", "DARK"
    }

    val selectedCarId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_CAR_ID_KEY]
        }

    suspend fun saveSelectedCarId(carId: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CAR_ID_KEY] = carId
        }
    }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: "SYSTEM"
        }

    suspend fun saveThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode
        }
    }
}
