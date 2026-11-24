package com.example.appriego.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrimarioDark,
    secondary = VerdeSecundarioDark,
    primaryContainer = ContenedorOscuro,
    surface = FondoOscuroMente,
    onPrimary = androidx.compose.ui.graphics.Color.Black // Texto negro sobre el botón verde brillante para que se lea bien
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    secondary = VerdeSecundario,
    primaryContainer = VerdeContenedor,
    surface = FondoSuperficie,
    onPrimary = androidx.compose.ui.graphics.Color.White
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AppRiegoTheme(
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
        content = content
    )
}
