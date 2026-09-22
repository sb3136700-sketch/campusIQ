package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueDarkPrimary,
    onPrimary = BrandBlueDarkOnPrimary,
    primaryContainer = BrandBlueDarkContainer,
    onPrimaryContainer = BrandBlueDarkOnContainer,
    secondary = BrandTealDarkSecondary,
    secondaryContainer = BrandTealDarkContainer,
    onSecondaryContainer = BrandTealDarkOnContainer,
    tertiary = BrandAmberDarkTertiary,
    tertiaryContainer = BrandAmberDarkContainer,
    onTertiaryContainer = BrandAmberDarkOnContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBluePrimary,
    onPrimary = BrandBlueOnPrimary,
    primaryContainer = BrandBlueContainer,
    onPrimaryContainer = BrandBlueOnContainer,
    secondary = BrandTealSecondary,
    secondaryContainer = BrandTealContainer,
    onSecondaryContainer = BrandTealOnContainer,
    tertiary = BrandAmberTertiary,
    tertiaryContainer = BrandAmberContainer,
    onTertiaryContainer = BrandAmberOnContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun CampusIqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep CampusIQ distinctive academic branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backward compatible alias for template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = CampusIqTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
