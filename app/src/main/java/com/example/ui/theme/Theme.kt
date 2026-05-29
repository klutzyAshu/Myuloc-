package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EarthySienna,
    secondary = WarmSaddleBrown,
    tertiary = CopperGlow,
    background = DeepCharcoalBg,
    surface = DenseDarkBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE5DCD3), // Sand text color
    onSurface = Color(0xFFFBFBFB)
)

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    secondary = SoftPurpleLabel,
    tertiary = LavenderGlow,
    background = GlossyLightBg,
    surface = PureWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF111827), // Strong dark gray text
    onSurface = Color(0xFF1F2937)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We take value from viewmodel state, default to dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
