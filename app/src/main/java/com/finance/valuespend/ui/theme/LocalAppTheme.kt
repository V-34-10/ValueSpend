package com.finance.valuespend.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ColorTokens(
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val error: Color,
    val success: Color,
    val warning: Color
)

@Immutable
data class TypographyTokens(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
    val button: TextStyle
)

@Immutable
data class DimensionTokens(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp
)

@Immutable
data class RadiusTokens(
    val sm: Dp,
    val md: Dp,
    val lg: Dp
)

@Immutable
data class ElevationTokens(
    val card: Dp,
    val modal: Dp,
    val fab: Dp
)

internal val LocalColorTokens = staticCompositionLocalOf {
    ColorTokens(
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
}

internal val LocalTypographyTokens = staticCompositionLocalOf {
    TypographyTokens(
        h1 = AppTypography.headlineLarge,
        h2 = AppTypography.headlineMedium,
        h3 = AppTypography.headlineSmall,
        body = AppTypography.bodyLarge,
        caption = AppTypography.labelSmall,
        button = AppTypography.labelLarge
    )
}

internal val LocalDimensionTokens = staticCompositionLocalOf {
    DimensionTokens(
        xs = 4.dp,
        sm = 8.dp,
        md = 16.dp,
        lg = 24.dp,
        xl = 32.dp
    )
}

internal val LocalRadiusTokens = staticCompositionLocalOf {
    RadiusTokens(
        sm = 8.dp,
        md = 16.dp,
        lg = 24.dp
    )
}

internal val LocalElevationTokens = staticCompositionLocalOf {
    ElevationTokens(
        card = 2.dp,
        modal = 8.dp,
        fab = 6.dp
    )
}

/**
 * Public facade required by project rules.
 * UI must read colors/typography/dimens only through [LocalAppTheme].
 */
object LocalAppTheme {
    val colors: ColorTokens
        @Composable get() = LocalColorTokens.current

    val typography: TypographyTokens
        @Composable get() = LocalTypographyTokens.current

    val dimens: DimensionTokens
        @Composable get() = LocalDimensionTokens.current

    val radius: RadiusTokens
        @Composable get() = LocalRadiusTokens.current

    val elevation: ElevationTokens
        @Composable get() = LocalElevationTokens.current
}

