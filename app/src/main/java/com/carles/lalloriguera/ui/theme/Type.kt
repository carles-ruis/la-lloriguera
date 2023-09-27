package com.carles.lalloriguera.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.carles.lalloriguera.R

private val RetroComputer = FontFamily(Font(R.font.retro_computer))

val Typo = Typography(
    headlineLarge = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RetroComputer,
        fontSize = 10.sp
    )
)