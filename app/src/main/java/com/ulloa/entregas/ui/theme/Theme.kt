package com.ulloa.entregas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MediumBlue,
    onPrimary = Color.White,
    secondary = LightBlue,
    onSecondary = Color.White,
    tertiary = AccentColor,
    background = Color(0xFFF4F7FC),
    surface = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = TextPrimaryDark,
    secondary = AccentColor,
    onSecondary = TextPrimaryDark,
    tertiary = AccentColor,
    background = DarkBlue,
    surface = MediumBlue,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = Color(0xFFCF6679)
)

@Composable
fun AppEntregasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Color de la barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asume que existe un archivo Type.kt
        content = content
    )
}

