package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard spacing definitions for the design system.
 * Translates pixel specifications into Android dp values.
 */
@Immutable
data class AppSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,       // 8px
    val medium: Dp = 12.dp,     // 12px
    val large: Dp = 16.dp,      // 16px
    val extraLarge: Dp = 24.dp, // 24px
    val xxl: Dp = 32.dp         // 32px
)

/**
 * Centered Color Palette tokens.
 */
@Immutable
data class AppColorPalette(
    // Primary Purple Suite
    val primaryPurple: Color = Color(0xFF7C3AED),     // Primary Royal Purple
    val darkPurpleAccent: Color = Color(0xFF6200EE),  // High-contrast deep purple
    val secondaryPurple: Color = Color(0xFF4C1D95),   // Deep rich violet
    val lightLavenderGlow: Color = Color(0xFFF3E8FF), // Pastel lavender container bg
    val softPurpleLabel: Color = Color(0xFF7C3AED),   // Purple indicator/tag

    // High contrast Green Suite
    val accentGreen: Color = Color(0xFF10B981),       // Emerald Green (checkmark/active)
    val lightGreenGlow: Color = Color(0x1F10B981),    // Soft semitransparent green overlay

    // Premium Neutral & Slate Grays
    val backgroundDark: Color = Color(0xFF0A0A0D),    // Ultra deep slate carbon black background
    val surfaceDark: Color = Color(0xFF121215),       // Matte dark slate card surface
    val textSlateLight: Color = Color(0xFFF8FAFC),    // Bright Slate white text for dark mode
    val textSlateMuted: Color = Color(0xFF94A3B8),    // Muted slate gray text
    val borderDark: Color = Color(0x2B8B5CF6),        // Elegant border lavender-purple tint
    
    val backgroundLight: Color = Color(0xFFF9F6FC),   // High-fidelity elegant light off-white background
    val surfaceLight: Color = Color(0xFFFFFFFF),      // Crisp white card surface
    val textLightPrimary: Color = Color(0xFF111827),  // Charcoal black primary text for light mode
    val textLightSecondary: Color = Color(0xFF4B5563) // Medium gray secondary text
)

/**
 * Typography System configuring Inter (SansSerif default) and SF Pro (System default) style text.
 */
@Immutable
data class AppTypography(
    val interFontFamily: FontFamily = FontFamily.Default,   // Mapped to premium clean sans-serif/default
    val sfProFontFamily: FontFamily = FontFamily.Default, // Mapped to crisp high-legibility sans-serif

    // Semantic Typographic styles
    val displayLarge: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.5.sp
    ),
    val titleLarge: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.25.sp
    ),
    val titleMedium: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val bodyLarge: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val bodyMedium: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    val bodySmall: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    val labelMedium: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Static composition locals for AppTheme spacing & custom additions
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalAppColorPalette = staticCompositionLocalOf { AppColorPalette() }
val LocalAppTypography = staticCompositionLocalOf { AppTypography() }

/**
 * Unified Theme Configuration Object for convenient layout use.
 */
object AppTheme {
    val spacing: AppSpacing
        @Composable
        get() = LocalAppSpacing.current

    val colors: AppColorPalette
        @Composable
        get() = LocalAppColorPalette.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}
