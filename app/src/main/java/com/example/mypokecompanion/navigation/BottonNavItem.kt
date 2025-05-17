package com.example.mypokecompanion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted // Icono para lista
import androidx.compose.material.icons.filled.GroupWork // Icono para equipos
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Clase sellada (sealed class) que define los diferentes ítems
 * que pueden aparecer en la barra de navegación inferior (Bottom Navigation Bar).
 * Cada objeto representa una pantalla principal accesible desde la barra.
 *
 * @param title El texto que se mostrará debajo del icono en la barra de navegación.
 * @param icon El [ImageVector] que se usará como icono para este ítem.
 * @param screenRoute La ruta de navegación (definida en [Screen]) asociada a este ítem.
 */
sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screenRoute: String
) {
    /**
     * Ítem para la pantalla de la Pokédex (lista de Pokémon).
     */
    object PokemonList : BottomNavItem(
        title = "Pokédex",
        icon = Icons.AutoMirrored.Filled.FormatListBulleted, // Icono estándar para listas. AutoMirrored se adapta a RTL.
        screenRoute = Screen.PokemonList.route // Ruta definida en la clase Screen.
    )

    /**
     * Ítem para la pantalla de "Mi Equipo".
     */
    object MyTeams : BottomNavItem(
        title = "Mi Equipo",
        icon = Icons.Filled.GroupWork, // Icono que sugiere un grupo o equipo.
        screenRoute = Screen.MyTeams.route // Ruta definida en la clase Screen.
    )
}