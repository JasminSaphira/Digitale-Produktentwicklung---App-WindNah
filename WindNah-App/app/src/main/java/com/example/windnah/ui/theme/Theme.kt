package com.example.windnah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WindNahLightColors = lightColorScheme(
    primary = WindNahGreen,
    primaryContainer = WindNahGreenContainer,
    secondary = WindNahSecondary,
    secondaryContainer = WindNahSecondaryContainer,
    background = WindNahBackground,
    surface = WindNahSurface,
    error = WindNahError,
)

private val WindNahDarkColors = darkColorScheme(
    primary = WindNahGreenDark,
    primaryContainer = WindNahGreenContainerDark,
    secondary = WindNahSecondaryDark,
    secondaryContainer = WindNahSecondaryContainerDark,
    background = WindNahBackgroundDark,
    surface = WindNahSurfaceDark,
    error = WindNahErrorDark,
)

@Composable
fun WindNahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WindNahDarkColors else WindNahLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
