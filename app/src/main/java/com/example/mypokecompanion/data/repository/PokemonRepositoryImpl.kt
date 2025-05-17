package com.example.mypokecompanion.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mypokecompanion.data.paging.PokemonPagingSource // Fuente de datos para la paginación
import com.example.mypokecompanion.data.remote.api.PokeApiService
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.remote.dto.PokemonResultDto
import kotlinx.coroutines.flow.Flow
import kotlin.text.ifBlank
import kotlin.text.trim

/**
 * Implementación de [IPokemonRepository].
 * Es responsable de obtener datos de Pokémon, ya sea una lista paginada o detalles específicos,
 * principalmente desde una fuente remota ([PokeApiService]).
 *
 * @param apiService La instancia del servicio Retrofit para interactuar con la PokeAPI.
 */
class PokemonRepositoryImpl(
    private val apiService: PokeApiService,
) : IPokemonRepository {

    /**
     * Implementación de [IPokemonRepository.getPokemonPagingData].
     * Configura y devuelve un [Flow] de [PagingData] utilizando [Pager] de la biblioteca Paging 3.
     * La fuente de datos para este Pager es [PokemonPagingSource].
     *
     * @param query La cadena de búsqueda opcional para filtrar Pokémon.
     * @return Un Flow que emite [PagingData] de [PokemonResultDto].
     */
    override fun getPokemonPagingData(query: String?): Flow<PagingData<PokemonResultDto>> {
        Log.d("PokemonRepositoryImpl", "getPokemonPagingData llamado con query: '${query ?: "null"}'")
        return Pager(
            config = PagingConfig(
                pageSize = PokemonPagingSource.DEFAULT_PAGE_SIZE, // Tamaño de página definido en el PagingSource
                enablePlaceholders = false // Los placeholders no se utilizan generalmente con Paging 3 y fuentes de red
            ),
            // La factory que crea una nueva instancia de PokemonPagingSource cada vez que se necesitan datos.
            pagingSourceFactory = {
                Log.d("PokemonRepositoryImpl", "Factory: Creando PagingSource con query: '${query ?: "null"}'")
                // Se pasa el apiService y la query (limpiada de espacios y convertida a null si está en blanco) al PagingSource.
                PokemonPagingSource(apiService, query?.trim()?.ifBlank { null })
            }
        ).flow // Expone el resultado del Pager como un Flow.
    }

    /**
     * Implementación de [IPokemonRepository.getPokemonDetail].
     * Obtiene los detalles de un Pokémon de la [PokeApiService].
     * Maneja posibles excepciones durante la llamada a la API y devuelve null en caso de error.
     *
     * @param nameOrId El nombre o ID del Pokémon a obtener.
     * @return [PokemonDetailDto] si tiene éxito, null en caso de error.
     */
    override suspend fun getPokemonDetail(nameOrId: String): PokemonDetailDto? {
        return try {
            // Llama al servicio de la API, convirtiendo el nombre/ID a minúsculas
            // ya que la PokeAPI suele ser sensible a mayúsculas/minúsculas para los nombres.
            apiService.getPokemonDetail(nameOrId.lowercase())
        } catch (e: Exception) {
            Log.e("PokemonRepositoryImpl", "Error obteniendo detalles para $nameOrId", e)
            null // Devuelve null si ocurre cualquier excepción.
        }
    }
}