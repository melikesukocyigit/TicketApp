package com.turkcell.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = OnPrimary,
    primaryContainer = SurfaceTint,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandAccent,
    onSecondary = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceTint,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    error = ErrorRed,
    onError = OnPrimary,
)

@Composable
fun TicketAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = TicketAppTypography,
        content = content
    )
}