package fi.danielz.bussini.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val Teal200 = Color(0xFFB1A7E2)
val Red400 = Color(0xFFc45076)

val LightBlue300 = Color(0xFF698CCE)
val LightBlue500 = Color(0xFF005bbc)
val LightBlue700 = Color(0xFF0049a6)

val colorPrimary = LightBlue300

val bussiniColorPalette: Colors = Colors(
    primary = colorPrimary,
    primaryVariant = LightBlue700,
    secondary = LightBlue500,
    secondaryVariant = Teal200,
    error = Red400,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onError = Color.White
)