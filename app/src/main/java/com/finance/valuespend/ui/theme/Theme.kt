package com.finance.valuespend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    onPrimary = OnPrimary,
    secondary = SecondaryAccent,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error
)

// We keep a simple dark scheme aliasing light colors for now,
// as the product is specified as light-only.
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    background = Color.Black,
    surface = Color(0xFF121212),
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Error
)

@Composable
fun ValueSpendTheme(
    darkTheme: Boolean = false,
    // Dynamic color is disabled to keep strict brand palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
        val colorTokens = ColorTokens(
            primaryAccent = PrimaryAccent,
            secondaryAccent = SecondaryAccent,
            background = Background,
            surface = Surface,
            onPrimary = OnPrimary,
            onSecondary = OnSecondary,
            onBackground = OnBackground,
            onSurface = OnSurface,
            error = Error,
            success = Success,
            warning = Warning
        )

        val typographyTokens = TypographyTokens(
            h1 = AppTypography.headlineLarge,
            h2 = AppTypography.headlineMedium,
            h3 = AppTypography.headlineSmall,
            body = AppTypography.bodyLarge,
            caption = AppTypography.labelSmall,
            button = AppTypography.labelLarge
        )

        val dimensionTokens = DimensionTokens(
            xs = 4.dp,
            sm = 8.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp
        )

        val radiusTokens = RadiusTokens(
            sm = 8.dp,
            md = 16.dp,
            lg = 24.dp
        )

        val elevationTokens = ElevationTokens(
            card = 2.dp,
            modal = 8.dp,
            fab = 6.dp
        )

        CompositionLocalProvider(
            LocalColorTokens provides colorTokens,
            LocalTypographyTokens provides typographyTokens,
            LocalDimensionTokens provides dimensionTokens,
            LocalRadiusTokens provides radiusTokens,
            LocalElevationTokens provides elevationTokens
        ) {
            content()
        }
    }
}