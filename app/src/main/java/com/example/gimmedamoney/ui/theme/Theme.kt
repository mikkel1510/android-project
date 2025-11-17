package com.example.gimmedamoney.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,

    background = BackgroundDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White,

    surfaceVariant = DarkGrey,
    onSurfaceVariant = Color.White,

    secondary = FaintBlue,
    onSecondary = Color.White,

    tertiary = MidGrey
)


private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,

    background = BackgroundLight,
    onBackground = BackgroundDark,

    surface = SurfaceLight,
    onSurface = BackgroundDark,

    surfaceVariant = LightGrey,
    onSurfaceVariant = BackgroundDark,

    secondary = FaintBlue,
    onSecondary = Color.White,

    tertiary = MidGrey
)


@Composable
fun GimmeDaMoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        content = content,
        shapes = Shapes
    )
}