package com.example.mypokecompanion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// Las clases Screen y BottomNavItem se asume que están correctamente definidas
// en este paquete 'navigation' o importadas adecuadamente.

import com.example.mypokecompanion.ui.screens.detail.PokemonDetailScreen
import com.example.mypokecompanion.ui.screens.pokemonlist.PokemonListScreen
import com.example.mypokecompanion.ui.screens.team.TeamScreen

/**
 * Composable principal que configura la navegación de la aplicación utilizando Jetpack Navigation Compose.
 * Incluye un [Scaffold] con una [NavigationBar] (BottomNavigationView) y un [NavHost]
 * para gestionar las diferentes pantallas (destinos).
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para algunos componentes de Material 3 como Scaffold.
@Composable
fun AppNavigation() {
    // Controlador de navegación que gestiona el estado de la navegación (backstack, pantalla actual, etc.).
    val navController = rememberNavController()

    // Lista de ítems que se mostrarán en la barra de navegación inferior.
    // Se asume que BottomNavItem está definido y contiene las rutas y los iconos.
    val bottomNavItems = listOf(
        BottomNavItem.PokemonList,
        BottomNavItem.MyTeams
    )

    // Scaffold proporciona una estructura básica de Material Design para la app.
    Scaffold(
        bottomBar = {
            // Obtiene la entrada actual de la pila de navegación para determinar la ruta actual.
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Determina si se debe mostrar la barra de navegación inferior.
            // Solo se muestra si la ruta actual es una de las definidas en bottomNavItems.
            val showBottomBar = bottomNavItems.any { it.screenRoute == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar {
                    // Itera sobre los ítems de la barra de navegación.
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            // El ítem se considera seleccionado si la ruta actual coincide con la ruta del ítem.
                            selected = currentDestination?.hierarchy?.any { it.route == item.screenRoute } == true,
                            onClick = {
                                // Navega a la ruta del ítem al hacer clic.
                                navController.navigate(item.screenRoute) {
                                    // popUpTo asegura que no se acumulen múltiples instancias de la misma pantalla en la pila.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true // Guarda el estado de la pantalla de la que se sale.
                                    }
                                    launchSingleTop = true // Evita múltiples copias de la misma pantalla en la cima de la pila.
                                    restoreState = true // Restaura el estado si se vuelve a esta pantalla.
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding -> // El padding proporcionado por el Scaffold para el contenido principal.
        // NavHost es el contenedor donde se muestran las diferentes pantallas (composables).
        NavHost(
            navController = navController,
            startDestination = Screen.PokemonList.route, // La pantalla inicial de la aplicación.
            modifier = Modifier.padding(innerPadding) // Aplica el padding para no solaparse con la bottom bar.
        ) {
            // Define la pantalla para la lista de Pokémon.
            composable(route = Screen.PokemonList.route) {
                PokemonListScreen(
                    navController = navController // Pasa el navController para permitir la navegación desde esta pantalla.
                )
            }

            // Define la pantalla para los detalles de un Pokémon.
            // Esta ruta incluye un argumento {pokemonName}.
            composable(
                route = Screen.PokemonDetail.route, // Ruta base definida en Screen.PokemonDetail.
                arguments = listOf(navArgument(Screen.PokemonDetail.ARG_POKEMON_NAME) { // Define el argumento esperado.
                    type = NavType.StringType // El tipo del argumento es String.
                })
            ) { backStackEntry ->
                // Obtiene el valor del argumento "pokemonName" de la ruta.
                val pokemonNameFromArgs = backStackEntry.arguments?.getString(Screen.PokemonDetail.ARG_POKEMON_NAME)

                if (pokemonNameFromArgs != null) {
                    // Si el nombre del Pokémon se obtiene correctamente, muestra la pantalla de detalles.
                    PokemonDetailScreen(
                        navController = navController,
                        pokemonName = pokemonNameFromArgs
                    )
                } else {
                    // Manejo de error si el argumento no se encuentra (poco probable si la navegación está bien configurada).
                    Text("Error: Nombre del Pokémon no encontrado en la navegación.")
                }
            }

            // Define la pantalla para "Mis Equipos".
            composable(route = Screen.MyTeams.route) {
                TeamScreen(
                    navController = navController
                )
            }
        }
    }
}