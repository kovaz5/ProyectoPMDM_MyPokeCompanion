package com.example.mypokecompanion.data.repository

import androidx.paging.PagingData // Importación necesaria para Paging 3
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.remote.dto.PokemonResultDto // DTO para los items de la lista paginada
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para el repositorio de Pokémon.
 * Abstrae el origen de los datos de Pokémon (remoto, local, caché) y proporciona
 * un punto de acceso único para los ViewModels.
 */
interface IPokemonRepository {
    /**
     * Obtiene un [Flow] de [PagingData] que contiene elementos de tipo [PokemonResultDto].
     * Este método se utiliza para cargar y mostrar una lista paginada de Pokémon,
     * permitiendo la carga bajo demanda a medida que el usuario se desplaza.
     *
     * @param query Una cadena de texto opcional para buscar Pokémon. Si es nulo o vacío,
     *              se devuelve la lista general de Pokémon. Si se proporciona, se utiliza
     *              para filtrar los resultados (la implementación específica de la búsqueda
     *              depende de [PokemonPagingSource]).
     * @return Un Flow que emite [PagingData] con los resultados de Pokémon.
     */
    fun getPokemonPagingData(query: String? = null): Flow<PagingData<PokemonResultDto>>

    /**
     * Obtiene los detalles completos de un Pokémon específico por su nombre o ID.
     * Es una función suspendida ya que la obtención de datos es una operación asíncrona.
     *
     * @param nameOrId El nombre (String) o el ID numérico (convertido a String) del Pokémon.
     * @return Un [PokemonDetailDto] con los detalles del Pokémon si se encuentra, o null si
     *         ocurre un error o el Pokémon no existe.
     */
    suspend fun getPokemonDetail(nameOrId: String): PokemonDetailDto?
}