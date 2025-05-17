package com.example.mypokecompanion.data.remote.api

import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto // DTO para los detalles de un Pokémon
import com.example.mypokecompanion.data.remote.dto.PokemonListResponse // DTO para la respuesta de la lista de Pokémon
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define los endpoints de la PokeAPI.
 * Cada método corresponde a una operación específica de la API.
 */
interface PokeApiService {

    /**
     * Obtiene una lista paginada de Pokémon.
     *
     * @param limit El número máximo de Pokémon a devolver por página.
     * @param offset El índice inicial desde el cual comenzar a devolver Pokémon.
     * @return Un objeto [PokemonListResponse] que contiene la lista de resultados e información de paginación.
     */
    @GET("pokemon") // Endpoint relativo a la baseUrl de Retrofit.
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    /**
     * Obtiene los detalles de un Pokémon específico por su nombre o ID.
     *
     * @param nameOrId El nombre (en minúsculas) o el ID numérico del Pokémon.
     * @return Un objeto [PokemonDetailDto] que contiene toda la información detallada del Pokémon.
     */
    @GET("pokemon/{name}") // El parámetro del path se sustituye por el valor de 'nameOrId'.
    suspend fun getPokemonDetail(
        @Path("name") nameOrId: String // Indica que 'nameOrId' debe usarse para el segmento {name} de la URL.
    ): PokemonDetailDto
}