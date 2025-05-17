package com.example.mypokecompanion.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mypokecompanion.data.remote.api.PokeApiService
import com.example.mypokecompanion.data.remote.dto.PokemonListResponse
import com.example.mypokecompanion.data.remote.dto.PokemonResultDto // DTO para los elementos de la lista
import java.io.IOException // Para gestionar errores de red
import retrofit2.HttpException // Para gestionar errores HTTP específicos

/**
 * Una implementación de [PagingSource] que carga Pokémon desde la PokeAPI.
 * Esta clase es responsable de definir cómo se cargan los datos de forma incremental
 * para su visualización en una lista paginada.
 *
 * @param apiService Instancia de [PokeApiService] para realizar llamadas a la API.
 * @param query Una cadena de búsqueda opcional. Si se proporciona, la lógica de carga
 *              intentará obtener un Pokémon específico que coincida con la consulta.
 *              Si es nula o está en blanco, se cargará la lista general de Pokémon.
 */
class PokemonPagingSource(
    private val apiService: PokeApiService,
    private val query: String?
) : PagingSource<Int, PokemonResultDto>() { // El tipo de la clave es Int (para el offset/página), el tipo del valor es PokemonResultDto

    companion object {
        /** Tamaño de página predeterminado para cargar Pokémon. */
        const val DEFAULT_PAGE_SIZE = 20
        /** Índice de la página inicial (u offset inicial). PokeAPI usa 'offset'. */
        private const val STARTING_PAGE_INDEX = 0
    }

    /**
     * Función llamada por la biblioteca Paging para cargar de forma asíncrona más datos.
     *
     * @param params Objeto [LoadParams] que contiene información sobre la carga a realizar,
     *               incluyendo la clave de la página a cargar y el tamaño de la carga.
     * @return Un [LoadResult] que representa el resultado de la operación de carga.
     *         Puede ser [LoadResult.Page] en caso de éxito, o [LoadResult.Error] en caso de fallo.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonResultDto> {
        // Determina la posición actual. Si es la primera carga (params.key es null), usa STARTING_PAGE_INDEX.
        val position = params.key ?: STARTING_PAGE_INDEX
        // Calcula el 'offset' para la API. Si es la primera carga, el offset es 0.
        // Si no, se calcula en base a la posición y al tamaño de carga (loadSize).
        // Esta lógica puede variar dependiendo de si 'position' representa el índice de página o el offset directo.
        // Aquí asumimos que 'position' es un índice de página.
        val offset = if (params.key == null) 0 else position * params.loadSize

        Log.d("PagingSource", "load llamado - Consulta: '$query', Offset: $offset, TamañoCarga: ${params.loadSize}")

        return try {
            // LÓGICA DE CARGA DIFERENCIADA SEGÚN SI HAY CONSULTA DE BÚSQUEDA O NO

            if (!query.isNullOrBlank()) {
                // CASO 1: HAY UNA CONSULTA DE BÚSQUEDA (query no es nula ni está en blanco)
                // La PokeAPI no tiene un endpoint de búsqueda por nombre parcial que devuelva una lista paginada.
                // La estrategia aquí es intentar obtener un Pokémon por nombre EXACTO.
                Log.d("PagingSource", "Intentando obtener Pokémon específico: '$query'")
                try {
                    // Realiza la llamada a la API para obtener detalles de un Pokémon específico.
                    // La PokeAPI normalmente espera nombres en minúsculas.
                    val detailDto = apiService.getPokemonDetail(query.lowercase())

                    // Construye un PokemonResultDto a partir del PokemonDetailDto obtenido.
                    // Esto es necesario si el PagingSource está tipado con PokemonResultDto.
                    val resultDto = PokemonResultDto(
                        name = detailDto.name,
                        // Construye la URL basándose en el ID del Pokémon.
                        url = "https://pokeapi.co/api/v2/pokemon/${detailDto.id}/"
                    )
                    Log.d("PagingSource", "Pokémon específico encontrado: ${resultDto.name}")

                    // Para una búsqueda específica de un único elemento, no hay páginas previas o siguientes.
                    LoadResult.Page(
                        data = listOf(resultDto), // Devuelve una lista con un único elemento.
                        prevKey = null,           // No hay página anterior.
                        nextKey = null            // No hay página siguiente.
                    )
                } catch (e: HttpException) {
                    // Gestiona errores HTTP específicos.
                    if (e.code() == 404) { // Código 404: No encontrado.
                        Log.d("PagingSource", "Pokémon '$query' no encontrado (404). Devolviendo página vacía.")
                        LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                    } else { // Otro error HTTP.
                        Log.e("PagingSource", "HttpException para consulta '$query': ${e.message}", e)
                        LoadResult.Error(e)
                    }
                } catch (e: Exception) { // Otro tipo de error (IOException, etc.).
                    Log.e("PagingSource", "Excepción para consulta '$query': ${e.message}", e)
                    LoadResult.Error(e)
                }

            } else {
                // CASO 2: NO HAY CONSULTA DE BÚSQUEDA (query es nula o está en blanco)
                // Carga la lista general de Pokémon de forma paginada.
                Log.d("PagingSource", "Obteniendo lista paginada - Offset: $offset, Límite: ${params.loadSize}")
                val response: PokemonListResponse = apiService.getPokemonList(
                    limit = params.loadSize, // El número de elementos a cargar.
                    offset = offset          // El punto de partida en la lista de la API.
                )
                val pokemons = response.results // La lista de Pokémon recibida.

                // Determina la clave para la siguiente página.
                // Si la lista actual está vacía o no hay una URL 'next' en la respuesta de la API,
                // significa que no hay más páginas.
                val nextKey = if (pokemons.isEmpty() || response.next == null) {
                    null
                } else {
                    // Si 'position' es el índice de la página, la siguiente clave es position + 1.
                    position + 1
                    // Si 'position' fuese el offset directamente, sería: offset + params.loadSize
                }

                // Determina la clave para la página anterior.
                // Si estamos en la página inicial, no hay página anterior.
                val prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1

                Log.d("PagingSource", "Lista paginada obtenida. Cantidad: ${pokemons.size}, SiguienteClave: $nextKey")
                LoadResult.Page(
                    data = pokemons,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

        } catch (e: IOException) { // Error de red (conectividad, etc.).
            Log.e("PagingSource", "Error de E/S (IOException) en la carga: ${e.message}", e)
            LoadResult.Error(e)
        } catch (e: HttpException) { // Error HTTP no capturado anteriormente (ej: error de servidor 500).
            Log.e("PagingSource", "Error HTTP en la carga: ${e.message}", e)
            LoadResult.Error(e)
        } catch (e: Exception) { // Cualquier otra excepción no esperada.
            Log.e("PagingSource", "Error genérico en la carga: ${e.message}", e)
            LoadResult.Error(e)
        }
    }

    /**
     * Proporciona una clave para cargar cuando los datos se invalidan o es la primera carga
     * después de una restauración de estado (por ejemplo, después de un cambio de configuración).
     * La biblioteca Paging usa esta clave para centrar los datos cargados alrededor de la última
     * posición de desplazamiento del usuario (anchorPosition).
     *
     * @param state El estado actual de paginación [PagingState].
     * @return La clave (en este caso, un Int para el índice de página/offset) para la operación de refresco,
     *         o null si no se puede determinar una clave.
     */
    override fun getRefreshKey(state: PagingState<Int, PokemonResultDto>): Int? {
        // Intenta encontrar la página más cercana a la 'anchorPosition' (la última posición visible).
        // Si se encuentra, se calcula la clave de refresco basándose en las claves 'prevKey' o 'nextKey'
        // de esa página más cercana.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}