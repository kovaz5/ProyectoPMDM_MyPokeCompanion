package com.example.mypokecompanion.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Objeto que mapea los nombres de los tipos de Pokémon a sus colores representativos.
 */
object PokemonTypeColors {
    private val typeColorMap = mapOf(
        "normal" to Color(0xFFA8A77A),
        "fire" to Color(0xFFEE8130),
        "water" to Color(0xFF6390F0),
        "electric" to Color(0xFFF7D02C),
        "grass" to Color(0xFF7AC74C),
        "ice" to Color(0xFF96D9D6),
        "fighting" to Color(0xFFC22E28),
        "poison" to Color(0xFFA33EA1),
        "ground" to Color(0xFFE2BF65),
        "flying" to Color(0xFFA98FF3),
        "psychic" to Color(0xFFF95587),
        "bug" to Color(0xFFA6B91A),
        "rock" to Color(0xFFB6A136),
        "ghost" to Color(0xFF735797),
        "dragon" to Color(0xFF6F35FC),
        "dark" to Color(0xFF705746),
        "steel" to Color(0xFFB7B7CE),
        "fairy" to Color(0xFFD685AD)
    )

    /**
     * Devuelve el [Color] asociado a un nombre de tipo de Pokémon.
     *
     * @param typeName Nombre del tipo de Pokémon (insensible a mayúsculas/minúsculas).
     * @return El [Color] para el tipo, o [Color.Gray] si el tipo no se encuentra.
     */
    fun getColor(typeName: String): Color {
        return typeColorMap[typeName.lowercase()] ?: Color.Gray
    }
}