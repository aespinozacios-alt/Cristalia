package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val CristaliaColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = ObsidianBg,
    primaryContainer = ColorTokens.GoldContainer,
    onPrimaryContainer = WarmAlabaster,
    secondary = LuminousJade,
    onSecondary = ObsidianBg,
    secondaryContainer = ColorTokens.JadeContainer,
    onSecondaryContainer = WarmAlabaster,
    tertiary = RoyalAmethyst,
    onTertiary = ObsidianBg,
    tertiaryContainer = ColorTokens.AmethystContainer,
    onTertiaryContainer = WarmAlabaster,
    background = ObsidianBg,
    onBackground = WarmAlabaster,
    surface = MidnightSurface,
    onSurface = WarmAlabaster,
    surfaceVariant = ElevatedSlate,
    onSurfaceVariant = MutedMist,
    outline = CardOutline,
    outlineVariant = SoftDim,
    error = ErrorRose,
    onError = ObsidianBg
)

private object ColorTokens {
    val GoldContainer = androidx.compose.ui.graphics.Color(0xFF3A2F1B)
    val JadeContainer = androidx.compose.ui.graphics.Color(0xFF14352E)
    val AmethystContainer = androidx.compose.ui.graphics.Color(0xFF2D2148)
}

private val CristaliaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CristaliaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CristaliaColorScheme,
        typography = CristaliaTypography,
        shapes = CristaliaShapes,
        content = content
    )
}
