package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class ThemePreference(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

@Immutable
data class SmartLabsExtendedColors(
    val isDark: Boolean,
    val mint: Color,
    val onMint: Color,
    val pink: Color,
    val amber: Color,
    val glassOverlay: Color,
    val glassBorder: Color,
    val cardElevated: Color,
    val heroGradient: Brush,
    val blueTintGradient: Brush,
    val shimmerBase: Color,
    val shimmerHighlight: Color
)

val LocalSmartLabsColors = staticCompositionLocalOf {
    SmartLabsExtendedColors(
        isDark = false,
        mint = AccentMintDark,
        onMint = Color.White,
        pink = AccentPinkDark,
        amber = AccentAmber,
        glassOverlay = GlassWhiteOverlay,
        glassBorder = GlassWhiteBorder,
        cardElevated = LightSurface,
        heroGradient = Brush.linearGradient(listOf(OceanDeep, RoyalBlue, PrimaryBlue)),
        blueTintGradient = Brush.verticalGradient(listOf(Color(0x000A2463), Color(0xCC0A2463))),
        shimmerBase = Color(0xFFE2E8F0),
        shimmerHighlight = Color(0xFFF8FAFC)
    )
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = IceBlue,
    onPrimaryContainer = OceanDeep,
    secondary = RoyalBlue,
    onSecondary = Color.White,
    secondaryContainer = PaleMistBlue,
    onSecondaryContainer = OceanMid,
    tertiary = AccentPinkDark,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Slate900,
    surface = LightSurface,
    onSurface = Slate900,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Slate500,
    outline = Slate300,
    error = DangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyAzure,
    onPrimary = OceanDeep,
    primaryContainer = OceanMid,
    onPrimaryContainer = IceBlue,
    secondary = PrimaryBlue,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = IceBlue,
    tertiary = AccentPink,
    onTertiary = OceanDeep,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    error = DangerRed,
    onError = Color.White
)

val SmartLabsShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SmartLabsTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = if (darkTheme) {
        SmartLabsExtendedColors(
            isDark = true,
            mint = AccentMint,
            onMint = OceanDeep,
            pink = AccentPink,
            amber = AccentAmber,
            glassOverlay = Color(0xCC11224D),
            glassBorder = Color(0x4D60A5FA),
            cardElevated = DarkSurfaceElevated,
            heroGradient = Brush.linearGradient(listOf(Color(0xFF040B1F), Color(0xFF0D2866), Color(0xFF1D4ED8))),
            blueTintGradient = Brush.verticalGradient(listOf(Color(0x1A061539), Color(0xE6061539))),
            shimmerBase = Color(0xFF17223B),
            shimmerHighlight = Color(0xFF253559)
        )
    } else {
        SmartLabsExtendedColors(
            isDark = false,
            mint = AccentMintDark,
            onMint = Color.White,
            pink = AccentPinkDark,
            amber = AccentAmber,
            glassOverlay = Color(0xE6FFFFFF),
            glassBorder = Color(0x8093C5FD),
            cardElevated = LightSurface,
            heroGradient = Brush.linearGradient(listOf(OceanDeep, RoyalBlue, PrimaryBlue)),
            blueTintGradient = Brush.verticalGradient(listOf(Color(0x000A2463), Color(0xD90A2463))),
            shimmerBase = Color(0xFFE2E8F0),
            shimmerHighlight = Color(0xFFF8FAFC)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSmartLabsColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmartLabsTypography,
            shapes = SmartLabsShapes,
            content = content
        )
    }
}
