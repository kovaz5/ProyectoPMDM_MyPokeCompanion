package com.example.mypokecompanion.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO que representa un único Pokémon en la lista devuelta por el endpoint /pokemon de la PokeAPI.
 * Contiene la información básica de un Pokémon dentro de una lista.
 * `@JsonClass(generateAdapter = true)` permite a Moshi convertir este objeto desde y hacia JSON.
 */
@JsonClass(generateAdapter = true)
data class PokemonResultDto(
    /**
     * El nombre del Pokémon.
     * La anotación `@Json(name = "name")` mapea la propiedad 'name' de Kotlin
     * a la clave "name" en el JSON.
     */
    @Json(name = "name") val name: String,

    /**
     * La URL que apunta a los detalles completos de este Pokémon en la PokeAPI.
     * Esta URL se puede usar para realizar otra llamada a la API y obtener
     * el [PokemonDetailDto] correspondiente.
     */
    @Json(name = "url") val url: String
)