package com.example.mypokecompanion.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- DTOs para la respuesta del endpoint /pokemon/{name_or_id} ---

/**
 * DTO (Data Transfer Object) principal que representa la estructura detallada
 * de un Pokémon tal como se recibe de la PokeAPI (endpoint /pokemon/{name_or_id}).
 * La anotación `@JsonClass(generateAdapter = true)` instruye a Moshi para generar
 * automáticamente un adaptador JSON para esta clase, facilitando la conversión
 * entre JSON y objetos Kotlin.
 */
@JsonClass(generateAdapter = true)
data class PokemonDetailDto(
    /** El ID numérico único del Pokémon. */
    @Json(name = "id") val id: Int,
    /** El nombre del Pokémon (generalmente en minúsculas). */
    @Json(name = "name") val name: String,
    /** Objeto que contiene las URLs de los diferentes sprites (imágenes) del Pokémon. */
    @Json(name = "sprites") val sprites: PokemonSpritesDto,
    /** Lista de los tipos a los que pertenece el Pokémon (ej. "grass", "poison"). */
    @Json(name = "types") val types: List<PokemonTypeSlotDto>,
    /** Lista de las estadísticas base del Pokémon (ej. "hp", "attack"). */
    @Json(name = "stats") val stats: List<PokemonStatSlotDto>,
    /** Altura del Pokémon en decímetros. */
    @Json(name = "height") val height: Int,
    /** Peso del Pokémon en hectogramos. */
    @Json(name = "weight") val weight: Int
)

/**
 * DTO para el objeto "sprites" dentro de [PokemonDetailDto].
 * Contiene URLs a diversas imágenes del Pokémon.
 */
@JsonClass(generateAdapter = true)
data class PokemonSpritesDto(
    /** URL de la imagen frontal por defecto del Pokémon. Puede ser nula. */
    @Json(name = "front_default") val frontDefault: String?,
    /** Contiene otras variantes de sprites, como el artwork oficial. Puede ser nulo. */
    @Json(name = "other") val otherSprites: OtherSpritesDto?
)

/**
 * DTO para el objeto "other" dentro de [PokemonSpritesDto].
 * Se utiliza para acceder a categorías específicas de sprites, como "official-artwork".
 */
@JsonClass(generateAdapter = true)
data class OtherSpritesDto(
    /** Objeto que contiene las URLs del artwork oficial del Pokémon. Puede ser nulo. */
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?
)

/**
 * DTO para el objeto "official-artwork" dentro de [OtherSpritesDto].
 * Proporciona la URL del artwork oficial frontal.
 */
@JsonClass(generateAdapter = true)
data class OfficialArtworkDto(
    /** URL de la imagen frontal del artwork oficial. Puede ser nula. */
    @Json(name = "front_default") val frontDefault: String?
)

/**
 * DTO para cada entrada en la lista "types" de [PokemonDetailDto].
 * Representa un "slot" de tipo, indicando la posición del tipo y el tipo en sí.
 */
@JsonClass(generateAdapter = true)
data class PokemonTypeSlotDto(
    /** La posición o "slot" de este tipo para el Pokémon (ej. 1 para el primer tipo, 2 para el segundo). */
    @Json(name = "slot") val slot: Int,
    /** Objeto que contiene los detalles del tipo. */
    @Json(name = "type") val type: PokemonTypeDto
)

/**
 * DTO para el objeto "type" dentro de [PokemonTypeSlotDto].
 * Contiene el nombre del tipo y una URL a más detalles sobre ese tipo.
 */
@JsonClass(generateAdapter = true)
data class PokemonTypeDto(
    /** El nombre del tipo (ej. "fire", "water"). */
    @Json(name = "name") val name: String,
    /** La URL que apunta a los detalles de este tipo en la PokeAPI. */
    @Json(name = "url") val url: String
)

/**
 * DTO para cada entrada en la lista "stats" de [PokemonDetailDto].
 * Representa una estadística base del Pokémon.
 */
@JsonClass(generateAdapter = true)
data class PokemonStatSlotDto(
    /** El valor base de la estadística (ej. 45 para HP de Pikachu). */
    @Json(name = "base_stat") val baseStat: Int,
    /** El valor de esfuerzo (EV) que este Pokémon otorga al ser derrotado. */
    @Json(name = "effort") val effort: Int,
    /** Objeto que contiene los detalles de la estadística en sí (nombre y URL). */
    @Json(name = "stat") val stat: PokemonStatDto
)

/**
 * DTO para el objeto "stat" dentro de [PokemonStatSlotDto].
 * Contiene el nombre de la estadística y una URL a más detalles sobre esa estadística.
 */
@JsonClass(generateAdapter = true)
data class PokemonStatDto(
    /** El nombre de la estadística (ej. "hp", "attack", "defense"). */
    @Json(name = "name") val name: String,
    /** La URL que apunta a los detalles de esta estadística en la PokeAPI. */
    @Json(name = "url") val url: String
)