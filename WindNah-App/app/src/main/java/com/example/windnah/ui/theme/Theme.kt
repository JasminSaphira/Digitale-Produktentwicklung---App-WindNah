package com.example.windnah.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val WindNahLightColors = lightColorScheme(
    primary = WindNahGreen,
    primaryContainer = WindNahGreenContainer,
    secondary = WindNahSecondary,
    secondaryContainer = WindNahSecondaryContainer,
    tertiary = WindNahTertiary,
    tertiaryContainer = WindNahTertiaryContainer,
    onTertiary = Color.White,
    onTertiaryContainer = Color(0xFF00201F),
    background = WindNahBackground,
    surface = WindNahSurface,
    error = WindNahError,
)

private val WindNahDarkColors = darkColorScheme(
    primary = WindNahGreenDark,
    primaryContainer = WindNahGreenContainerDark,
    secondary = WindNahSecondaryDark,
    secondaryContainer = WindNahSecondaryContainerDark,
    tertiary = WindNahTertiaryDark,
    tertiaryContainer = WindNahTertiaryContainerDark,
    onTertiary = Color(0xFF00201F),
    onTertiaryContainer = WindNahTertiaryDark,
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

@Composable
fun WindNahSystemBars() {
    val view = LocalView.current
    val colorScheme = MaterialTheme.colorScheme
    val systemBarColor = if (colorScheme.background.luminance() > 0.5f) {
        Color.White
    } else {
        colorScheme.surfaceContainer
    }
    val useDarkIcons = systemBarColor.luminance() > 0.5f

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = systemBarColor.toArgb()
        window.navigationBarColor = systemBarColor.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = useDarkIcons
        controller.isAppearanceLightNavigationBars = useDarkIcons
    }
}
