package com.example.mypokecompanion.navigation

/**
 * Objeto que define las rutas de navegación de la aplicación de forma segura y organizada.
 * Usar un objeto (o sealed class/interface) para las rutas ayuda a evitar errores de tipeo
 * y centraliza la definición de las mismas.
 */
object Screen {
    /**
     * Ruta para la pantalla de la lista de Pokémon (Pokédex).
     */
    object PokemonList {
        const val route = "pokemon_list"
    }

    /**
     * Ruta y argumentos para la pantalla de detalles de un Pokémon específico.
     */
    object PokemonDetail {
        /**
         * Ruta base para los detalles del Pokémon, incluye un placeholder para el nombre del Pokémon.
         * Ejemplo: "pokemon_detail/pikachu"
         */
        const val route = "pokemon_detail/{pokemonName}"

        /**
         * Nombre de la constante utilizada como clave para el argumento 'pokemonName' en la ruta.
         * Es importante que este nombre coincida con el placeholder en la `route`.
         */
        const val ARG_POKEMON_NAME = "pokemonName"

        /**
         * Función de utilidad para construir la ruta completa hacia la pantalla de detalles
         * de un Pokémon específico, sustituyendo el placeholder con el nombre real.
         *
         * @param pokemonName El nombre del Pokémon para el cual se crea la ruta.
         * @return La cadena de la ruta completa (ej. "pokemon_detail/bulbasaur").
         */
        fun createRoute(pokemonName: String) = "pokemon_detail/$pokemonName"
    }

    /**
     * Ruta para la pantalla de "Mis Equipos".
     */
    object MyTeams {
        const val route = "my_teams"
    }
}