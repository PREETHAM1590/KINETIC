package com.kinetic.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KineticDarkColorScheme = darkColorScheme(
    primary = DarkKineticPalette.lime,
    onPrimary = DarkKineticPalette.background,
    primaryContainer = DarkKineticPalette.limeDark,
    onPrimaryContainer = DarkKineticPalette.lime,
    secondary = DarkKineticPalette.surface1,
    onSecondary = DarkKineticPalette.textPrimary,
    secondaryContainer = DarkKineticPalette.surface2,
    onSecondaryContainer = DarkKineticPalette.textPrimary,
    background = DarkKineticPalette.background,
    onBackground = DarkKineticPalette.textPrimary,
    surface = DarkKineticPalette.surface1,
    onSurface = DarkKineticPalette.textPrimary,
    surfaceVariant = DarkKineticPalette.surface2,
    onSurfaceVariant = DarkKineticPalette.textMuted,
    error = DarkKineticPalette.error,
    onError = DarkKineticPalette.background,
    outline = DarkKineticPalette.textMuted
)

private val KineticLightColorScheme = lightColorScheme(
    primary = LightKineticPalette.limeDark,
    onPrimary = LightKineticPalette.background,
    primaryContainer = LightKineticPalette.lime,
    onPrimaryContainer = LightKineticPalette.textPrimary,
    secondary = LightKineticPalette.surface1,
    onSecondary = LightKineticPalette.textPrimary,
    secondaryContainer = LightKineticPalette.surface2,
    onSecondaryContainer = LightKineticPalette.textPrimary,
    background = LightKineticPalette.background,
    onBackground = LightKineticPalette.textPrimary,
    surface = LightKineticPalette.surface1,
    onSurface = LightKineticPalette.textPrimary,
    surfaceVariant = LightKineticPalette.surface2,
    onSurfaceVariant = LightKineticPalette.textMuted,
    error = LightKineticPalette.error,
    onError = LightKineticPalette.surface1,
    outline = LightKineticPalette.textMuted
)

@Composable
fun KineticTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KineticDarkColorScheme else KineticLightColorScheme
    val view = LocalView.current

    SideEffect {
        setActivePalette(darkTheme)
        if (!view.isInEditMode) {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}