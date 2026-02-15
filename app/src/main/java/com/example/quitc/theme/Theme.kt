package com.example.quitc.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF00E676),
    background = Color(0xFF0B0B0B),
    surface = Color(0xFF151515),
    onPrimary = Color.Black,
    onBackground = Color.White
)

@Composable
fun QuitCTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content
    )
}