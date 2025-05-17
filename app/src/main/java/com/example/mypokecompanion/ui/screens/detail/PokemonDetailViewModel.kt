package com.example.mypokecompanion.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.repository.IPokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Define el estado de la UI para la pantalla de detalles del Pokémon.
 *
 * @property isLoading Indica si los datos se están cargando actualmente.
 * @property pokemonDetail Los detalles del Pokémon obtenidos. Null si no se han cargado o ha ocurrido un error.
 * @property error Un mensaje de error descriptivo si la carga falló. Null si no hay errores.
 */
data class PokemonDetailUiState(
    val isLoading: Boolean = false,
    val pokemonDetail: PokemonDetailDto? = null,
    val error: String? = null
)

/**
 * ViewModel para la pantalla [PokemonDetailScreen].
 * Se encarga de la lógica de negocio para cargar y gestionar los datos de los detalles
 * de un Pokémon específico. Interactúa con un [IPokemonRepository] para obtener los datos.
 *
 * @param repository Implementación de [IPokemonRepository] para acceder a los datos de los Pokémon.
 */
class PokemonDetailViewModel(
    private val repository: IPokemonRepository // Inyección de dependencia del repositorio
) : ViewModel() {

    // Flujo mutable privado que contiene el estado actual de la UI.
    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    /**
     * Flujo público e inmutable del estado de la UI, expuesto para ser observado por la UI (Compose).
     */
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    /**
     * Almacena el nombre del Pokémon cuyos detalles se están mostrando o se intentaron cargar.
     * Es privado para escritura (`private set`) para asegurar que solo el ViewModel lo modifique.
     */
    var currentPokemonName: String? = null
        private set

    /**
     * Inicia la carga de los detalles de un Pokémon por su nombre.
     * Actualiza [currentPokemonName] y luego llama a [loadPokemonDetailInternal].
     * Si el nombre del Pokémon está en blanco, actualiza el estado de la UI con un error.
     *
     * @param pokemonName El nombre del Pokémon a cargar.
     */
    fun loadPokemonDetailByName(pokemonName: String) {
        if (pokemonName.isBlank()) {
            _uiState.update { it.copy(error = "Nombre de Pokémon inválido", isLoading = false) }
            return
        }
        currentPokemonName = pokemonName // Guarda el nombre del Pokémon actual
        loadPokemonDetailInternal(pokemonName)
    }

    /**
     * Lógica interna para cargar los detalles del Pokémon.
     * Se ejecuta en una corrutina del [viewModelScope].
     * Actualiza el estado de la UI para indicar carga, luego intenta obtener los detalles
     * del repositorio. Maneja tanto los casos de éxito como los de error.
     *
     * @param pokemonNameForLoad El nombre del Pokémon para el cual se cargarán los detalles.
     */
    private fun loadPokemonDetailInternal(pokemonNameForLoad: String) {
        viewModelScope.launch {
            // Actualiza el estado para indicar que la carga ha comenzado.
            // Resetea cualquier detalle o error previo.
            _uiState.update { it.copy(isLoading = true, error = null, pokemonDetail = null) }
            try {
                Log.d("PokemonDetailVM", "Cargando detalles para: $pokemonNameForLoad desde el repositorio")
                // Llama al repositorio para obtener los detalles del Pokémon (nombre en minúsculas).
                val detail = repository.getPokemonDetail(pokemonNameForLoad.lowercase())
                if (detail != null) {
                    Log.d("PokemonDetailVM", "Detalles recibidos del repo: ID=${detail.id}, Nombre=${detail.name}")
                } else {
                    Log.d("PokemonDetailVM", "No se encontraron detalles para $pokemonNameForLoad")
                }
                // Actualiza el estado con los detalles obtenidos o un error si no se encontró.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pokemonDetail = detail,
                        error = if (detail == null) "Pokémon no encontrado" else null
                    )
                }
            } catch (e: Exception) {
                // Captura cualquier excepción durante la llamada al repositorio.
                Log.e("PokemonDetailVM", "Error al cargar detalles para $pokemonNameForLoad: ${e.message}", e)
                // Actualiza el estado con un mensaje de error.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar: ${e.localizedMessage ?: "Error desconocido"}",
                        pokemonDetail = null
                    )
                }
            }
        }
    }

    /**
     * Permite reintentar la carga de los detalles del Pokémon actual ([currentPokemonName]).
     * Si [currentPokemonName] es nulo (por ejemplo, si nunca se intentó cargar uno),
     * actualiza la UI con un mensaje de error apropiado.
     */
    fun retryLoadPokemonDetail() {
        currentPokemonName?.let { name ->
            // Si hay un nombre de Pokémon guardado, intenta cargar sus detalles nuevamente.
            loadPokemonDetailInternal(name)
        } ?: _uiState.update {
            // Si no hay un nombre de Pokémon, no se puede reintentar.
            it.copy(error = "No se puede reintentar: nombre del Pokémon no disponible.", isLoading = false)
        }
    }
}