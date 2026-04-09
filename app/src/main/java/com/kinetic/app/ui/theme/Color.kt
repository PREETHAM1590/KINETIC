package com.kinetic.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

internal data class KineticPalette(
    val lime: Color,
    val limeDark: Color,
    val background: Color,
    val surface1: Color,
    val surface2: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val error: Color
)

internal val DarkKineticPalette = KineticPalette(
    lime = Color(0xFFD1FF26),
    limeDark = Color(0xFF8AB800),
    background = Color(0xFF09090B),
    surface1 = Color(0xFF18181B),
    surface2 = Color(0xFF27272A),
    textPrimary = Color(0xFFF4F4F5),
    textMuted = Color(0xFF71717A),
    error = Color(0xFFFF4444)
)

internal val LightKineticPalette = KineticPalette(
    lime = Color(0xFFD1FF26),
    limeDark = Color(0xFF7BA800),
    background = Color(0xFFF8F9F4),
    surface1 = Color(0xFFFFFFFF),
    surface2 = Color(0xFFE5E8D9),
    textPrimary = Color(0xFF111214),
    textMuted = Color(0xFF5E6470),
    error = Color(0xFFB3261E)
)

private var activePalette by mutableStateOf(DarkKineticPalette)

internal fun setActivePalette(useDarkPalette: Boolean) {
    activePalette = if (useDarkPalette) DarkKineticPalette else LightKineticPalette
}

val Lime: Color
    get() = activePalette.lime

val LimeDark: Color
    get() = activePalette.limeDark

val Background: Color
    get() = activePalette.background

val Surface1: Color
    get() = activePalette.surface1

val Surface2: Color
    get() = activePalette.surface2

val TextPrimary: Color
    get() = activePalette.textPrimary

val TextMuted: Color
    get() = activePalette.textMuted

val Error: Color
    get() = activePalette.error