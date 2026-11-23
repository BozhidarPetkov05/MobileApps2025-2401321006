package com.example.autotrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4F0),
    onPrimaryContainer = Color(0xFF0A2440),
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2E4FA),
    onSecondaryContainer = Color(0xFF0F2D52),
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF4B5563),
    error = ErrorColor,
    errorContainer = ErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF0A2440),
    primaryContainer = Color(0xFF234268),
    onPrimaryContainer = Color(0xFFD6E4F0),
    secondary = SecondaryDark,
    onSecondary = Color(0xFF0F2D52),
    secondaryContainer = Color(0xFF2C4A73),
    onSecondaryContainer = Color(0xFFD2E4FA),
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF9CA3AF),
    error = ErrorDark,
    errorContainer = ErrorContainerDark
)

@Composable
fun AutoTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
