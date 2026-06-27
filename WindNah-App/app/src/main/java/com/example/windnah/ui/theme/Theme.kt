package com.example.windnah.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
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

@Composable
fun WindNahTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WindNahLightColors,
        typography = Typography,
        content = content
    )
}

@Composable
fun WindNahSystemBars() {
    val view = LocalView.current
    val systemBarColor = Color.White

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = systemBarColor.toArgb()
        window.navigationBarColor = systemBarColor.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
}
