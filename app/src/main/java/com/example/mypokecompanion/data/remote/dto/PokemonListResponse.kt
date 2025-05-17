package com.example.mypokecompanion.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO que representa la respuesta completa del endpoint /pokemon de la PokeAPI,
 * el cual devuelve una lista paginada de Pokémon.
 * `@JsonClass(generateAdapter = true)` habilita la generación automática de adaptadores Moshi.
 */
@JsonClass(generateAdapter = true)
data class PokemonListResponse(
    /**
     * El número total de recursos (Pokémon) disponibles en la API.
     * La anotación `@Json(name = "...")` asegura el correcto mapeo desde la clave JSON,
     * aunque Moshi podría inferirlo si los nombres coinciden. Es una buena práctica ser explícito.
     */
    @Json(name = "count") val count: Int,

    /**
     * La URL para obtener la siguiente página de resultados.
     * Puede ser nula si no hay una página siguiente (es decir, se está en la última página).
     */
    @Json(name = "next") val next: String?,

    /**
     * La URL para obtener la página anterior de resultados.
     * Puede ser nula si no hay una página anterior (es decir, se está en la primera página).
     */
    @Json(name = "previous") val previous: String?,

    /**
     * La lista de resultados de Pokémon para la página actual.
     * Cada elemento de la lista es un objeto [PokemonResultDto].
     */
    @Json(name = "results") val results: List<PokemonResultDto>
)