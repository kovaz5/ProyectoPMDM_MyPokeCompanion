package com.example.mypokecompanion.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Colores base de la aplicación inspirados en la temática Pokémon.
val PokemonRed = Color(0xFFEE1515)
val PokemonBlue = Color(0xFF3B4CCA)
val PokemonYellow = Color(0xFFFFDE00)
val PokemonDarkGray = Color(0xFF222222)
val PokemonLightGray = Color(0xFFF0F0F0)
val PokemonWhite = Color(0xFFFFFFFF)
val PokemonGreen = Color(0xFF4DAD5B) // Para elementos de éxito o confirmación.

/**
 * Esquema de colores para el tema claro de la aplicación.
 */
val LightColorScheme = lightColorScheme(
    primary = PokemonRed,
    onPrimary = PokemonWhite,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = PokemonBlue,
    onSecondary = PokemonWhite,
    secondaryContainer = Color(0xFFD6E3FF),
    onSecondaryContainer = Color(0xFF001B3E),
    tertiary = PokemonYellow,
    onTertiary = PokemonDarkGray,
    tertiaryContainer = Color(0xFFFFE258),
    onTertiaryContainer = Color(0xFF241A00),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = PokemonLightGray,
    onBackground = PokemonDarkGray,
    surface = PokemonWhite,
    onSurface = PokemonDarkGray,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

/**
 * Esquema de colores para el tema oscuro de la aplicación.
 */
val DarkColorScheme = darkColorScheme(
    primary = PokemonRed,
    onPrimary = PokemonWhite,
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = PokemonBlue,
    onSecondary = PokemonWhite,
    secondaryContainer = Color(0xFF004A7A),
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = PokemonYellow,
    onTertiary = PokemonDarkGray,
    tertiaryContainer = Color(0xFF5C4A00),
    onTertiaryContainer = Color(0xFFFFE258),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = PokemonDarkGray,
    onBackground = PokemonLightGray,
    surface = Color(0xFF2B292B),
    onSurface = PokemonLightGray,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)