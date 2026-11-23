package com.example.autotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.autotrack.datastore.PreferencesManager
import com.example.autotrack.navigation.AppNavigation
import com.example.autotrack.ui.theme.AutoTrackTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val preferencesManager = PreferencesManager(applicationContext)
        val initialThemeMode = runBlocking {
            preferencesManager.themeMode.first()
        }

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState(initial = initialThemeMode)
            val systemInDark = isSystemInDarkTheme()

            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemInDark
            }

            AutoTrackTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}
