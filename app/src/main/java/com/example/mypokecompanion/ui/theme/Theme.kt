package com.example.mypokecompanion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.darkColorScheme // Ya definidos en Color.kt
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
// import androidx.compose.material3.lightColorScheme // Ya definidos en Color.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// No es necesario re-declarar LightColorScheme y DarkColorScheme si están en Color.kt
// y Color.kt está en el mismo paquete.

/**
 * Tema principal de la aplicación MyPokeCompanion.
 * Configura el esquema de colores (claro/oscuro, dinámico si está disponible)
 * y la tipografía para toda la aplicación.
 *
 * @param darkTheme Indica si se debe usar el tema oscuro. Por defecto, sigue la configuración del sistema.
 * @param dynamicColor Habilita el color dinámico en Android 12+ si está activado.
 * @param content El contenido Composable al que se aplicará este tema.
 */
@Composable
fun MyPokeCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // Usa el DarkColorScheme personalizado del archivo Color.kt
        else -> LightColorScheme     // Usa el LightColorScheme personalizado del archivo Color.kt
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Color de la barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Tipografía personalizada definida en Type.kt
        content = content
    )
}