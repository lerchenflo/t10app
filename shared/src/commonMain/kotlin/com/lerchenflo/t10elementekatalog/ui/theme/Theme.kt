package com.lerchenflo.t10elementekatalog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006C51),
    onPrimary = Color.White,
    secondary = Color(0xFF4C6358),
    tertiary = Color(0xFF3F6A73)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF57DBC1),
    onPrimary = Color(0xFF003829),
    secondary = Color(0xFFB1CCB9),
    tertiary = Color(0xFFA3CED7)
)

@Composable
fun T10AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
} 