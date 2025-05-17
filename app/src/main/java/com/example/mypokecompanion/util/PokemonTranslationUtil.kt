package com.example.mypokecompanion.util

/**
 * Utilidad para traducir nombres relacionados con Pokémon (tipos, estadísticas) al español.
 */
object PokemonTranslationUtil {

    /**
     * Traduce el nombre de un tipo de Pokémon del inglés al español.
     *
     * @param englishTypeName Nombre del tipo en inglés.
     * @return Nombre del tipo traducido al español. Si no se encuentra traducción,
     * devuelve el nombre original capitalizado.
     */
    fun translateTypeName(englishTypeName: String): String {
        return when (englishTypeName.lowercase()) {
            "normal" -> "Normal"
            "fighting" -> "Lucha"
            "flying" -> "Volador"
            "poison" -> "Veneno"
            "ground" -> "Tierra"
            "rock" -> "Roca"
            "bug" -> "Bicho"
            "ghost" -> "Fantasma"
            "steel" -> "Acero"
            "fire" -> "Fuego"
            "water" -> "Agua"
            "grass" -> "Planta"
            "electric" -> "Eléctrico"
            "psychic" -> "Psíquico"
            "ice" -> "Hielo"
            "dragon" -> "Dragón"
            "dark" -> "Siniestro"
            "fairy" -> "Hada"
            // Considera añadir "unknown" y "shadow" si la API los devuelve y quieres traducirlos.
            else -> englishTypeName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    /**
     * Traduce el nombre de una estadística de Pokémon del inglés al español.
     *
     * @param englishStatName Nombre de la estadística en inglés (ej: "special-attack").
     * @return Nombre de la estadística traducido al español. Si no se encuentra traducción,
     * devuelve el nombre original capitalizado y con guiones reemplazados por espacios.
     */
    fun translateStatName(englishStatName: String): String {
        return when (englishStatName.lowercase()) {
            "hp" -> "PS" // Puntos de Salud
            "attack" -> "Ataque"
            "defense" -> "Defensa"
            "special-attack" -> "Ataque Especial"
            "special-defense" -> "Defensa Especial"
            "speed" -> "Velocidad"
            else -> englishStatName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }.replace("-", " ")
        }
    }
}