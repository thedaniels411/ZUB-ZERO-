package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZubZeroColorScheme = darkColorScheme(
    primary = ZubCyan,
    onPrimary = Color(0xFF04121F),
    primaryContainer = Color(0xFF0C2E46),
    onPrimaryContainer = Color(0xFFC7F2FF),
    secondary = ZubIceBlue,
    onSecondary = Color(0xFF061826),
    secondaryContainer = Color(0xFF132A3E),
    onSecondaryContainer = Color(0xFFBCE3FD),
    tertiary = ZubTwilightCrimson,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4C0E1E),
    onTertiaryContainer = Color(0xFFFFD9DF),
    background = ZubBlack,
    onBackground = ZubTextPrimary,
    surface = ZubDarkNavy,
    onSurface = ZubTextPrimary,
    surfaceVariant = ZubSurfaceVariant,
    onSurfaceVariant = ZubTextSecondary,
    outline = ZubBorder,
    error = Color(0xFFEF4444)
)

@Composable
fun ZubZeroTheme(
    darkTheme: Boolean = true, // Force cinematic dark style for Sub-Zero experience
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZubZeroColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ZubZeroTheme(darkTheme = darkTheme, content = content)
}

