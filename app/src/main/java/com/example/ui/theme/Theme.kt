package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = M3AccentDark,
    onPrimary = M3AccentLight,
    secondary = M3AccentDark,
    onSecondary = M3AccentLight,
    background = MdBlack,
    onBackground = M3TextDark,
    surface = M3SurfaceDark,
    onSurface = M3TextDark,
    surfaceVariant = Color(0xFF24242A),
    onSurfaceVariant = M3TextSecondaryDark,
    outline = M3OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = M3AccentLight,
    onPrimary = M3AccentDark,
    secondary = M3AccentLight,
    onSecondary = M3AccentDark,
    background = MdWhite,
    onBackground = M3TextLight,
    surface = M3SurfaceLight,
    onSurface = M3TextLight,
    surfaceVariant = Color(0xFFEAECF0),
    onSurfaceVariant = M3TextSecondaryLight,
    outline = M3OutlineLight
)

// Helper to reliably construct Compose Color from HSV values
fun createHsvColor(h: Float, s: Float, v: Float): Color {
    val hue = h.coerceIn(0f, 360f)
    val sat = s.coerceIn(0f, 1f)
    val value = v.coerceIn(0f, 1f)
    val colorInt = android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, value))
    return Color(colorInt)
}

// Convert chosen Hue and Saturation values into a balanced M3 ColorScheme based on the active dark/light mode
fun generateCustomColorScheme(hue: Float, saturation: Float, lightness: Float, isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    val h = hue.coerceIn(0f, 360f)
    val s = saturation.coerceIn(0f, 1f)
    val l = lightness.coerceIn(0f, 1f)

    return if (isDarkMode) {
        // Replace vibrant/neon colors in dark mode with elegant subtle/pastel tones of the same kind (hue)
        val subtleSat = (s * 0.35f).coerceIn(0.15f, 0.32f)
        val primaryColor = createHsvColor(h, subtleSat, 0.82f)      
        val secondaryColor = createHsvColor((h + 30f) % 360f, subtleSat * 0.75f, 0.72f)
        val bgColor = createHsvColor(h, subtleSat * 0.22f, l * 0.45f) // Adjust background based on lightness
        val surfaceColor = createHsvColor(h, subtleSat * 0.22f, l * 0.75f) // Adjust surface based on lightness
        val onBgColor = createHsvColor(h, subtleSat * 0.08f, 0.92f)
        val outlineColor = createHsvColor(h, subtleSat * 0.35f, 0.45f)
        
        androidx.compose.material3.darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = primaryColor,
            background = bgColor,
            surface = surfaceColor,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = onBgColor,
            onSurface = onBgColor,
            surfaceVariant = createHsvColor(h, subtleSat * 0.18f, l * 0.95f),
            onSurfaceVariant = createHsvColor(h, subtleSat * 0.12f, 0.82f),
            outline = outlineColor
        )
    } else {
        val primaryColor = createHsvColor(h, s, 0.4f)      
        val secondaryColor = createHsvColor((h + 30f) % 360f, s * 0.7f, 0.5f)
        val bgColor = createHsvColor(h, s * 0.05f, l.coerceAtLeast(0.85f)) // Use lightness for light background
        val surfaceColor = createHsvColor(h, s * 0.05f, (l - 0.03f).coerceAtLeast(0.80f))
        val onBgColor = createHsvColor(h, s * 0.1f, 0.1f)
        val outlineColor = createHsvColor(h, s * 0.2f, 0.6f)
        
        androidx.compose.material3.lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = primaryColor,
            background = bgColor,
            surface = surfaceColor,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = onBgColor,
            onSurface = onBgColor,
            surfaceVariant = createHsvColor(h, s * 0.08f, (l - 0.08f).coerceAtLeast(0.75f)),
            onSurfaceVariant = createHsvColor(h, s * 0.15f, 0.3f),
            outline = outlineColor
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    customThemeEnabled: Boolean = false,
    customHue: Float = 200f,
    customSaturation: Float = 0.8f,
    customLightness: Float = 0.15f,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val colorScheme = when {
        customThemeEnabled -> generateCustomColorScheme(customHue, customSaturation, customLightness, darkTheme)
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context) else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val appSpacing = AppSpacing()
    val appColorPalette = AppColorPalette()
    val appTypography = AppTypography()

    CompositionLocalProvider(
        LocalAppSpacing provides appSpacing,
        LocalAppColorPalette provides appColorPalette,
        LocalAppTypography provides appTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
