package com.example.mypokecompanion.ui.screens.team

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.repository.ITeamRepository
import com.example.mypokecompanion.data.repository.TeamRepositoryImpl // Necesario para MAX_TEAM_SIZE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla del equipo Pokémon.
 *
 * @property teamMembers Lista de Pokémon en el equipo (puede tener nulos para slots vacíos).
 * @property isTeamFull Indica si el equipo ha alcanzado su capacidad máxima.
 * @property errorMessage Mensaje de error para mostrar al usuario, si existe.
 * @property showReplaceDialog Controla la visibilidad del diálogo de reemplazo de Pokémon.
 * @property pokemonToAddOrReplace El Pokémon que se intenta añadir o que reemplazará a otro.
 */
data class TeamUiState(
    val teamMembers: List<PokemonDetailDto?> = List(TeamRepositoryImpl.MAX_TEAM_SIZE) { null },
    val isTeamFull: Boolean = false,
    val errorMessage: String? = null,
    val showReplaceDialog: Boolean = false,
    val pokemonToAddOrReplace: PokemonDetailDto? = null
)

/**
 * ViewModel para la pantalla [TeamScreen].
 * Gestiona la lógica de negocio relacionada con el equipo Pokémon, como añadir,
 * quitar y reemplazar miembros del equipo.
 *
 * @param teamRepository Repositorio para interactuar con los datos del equipo.
 */
class TeamViewModel(
    private val teamRepository: ITeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamUiState())

    /**
     * Flujo del estado de la UI para ser observado por la pantalla del equipo.
     */
    val uiState: StateFlow<TeamUiState> = _uiState.asStateFlow()

    init {
        observeTeam()
    }

    /**
     * Observa los cambios en el flujo de miembros del equipo desde el repositorio
     * y actualiza el [_uiState] correspondientemente.
     */
    private fun observeTeam() {
        viewModelScope.launch {
            teamRepository.teamMembersFlow.collect { teamListFromRepo ->
                _uiState.update { currentState ->
                    currentState.copy(
                        teamMembers = teamListFromRepo,
                        isTeamFull = teamListFromRepo.count { it != null } >= TeamRepositoryImpl.MAX_TEAM_SIZE,
                        errorMessage = null // Limpia errores previos al actualizar el equipo
                    )
                }
            }
        }
    }

    /**
     * Intenta añadir un Pokémon al equipo.
     * Si el Pokémon ya está, muestra un error.
     * Si hay espacio, lo añade al primer slot vacío.
     * Si el equipo está lleno, prepara el diálogo de reemplazo.
     *
     * @param pokemon El Pokémon a añadir.
     */
    fun addPokemonToTeam(pokemon: PokemonDetailDto) {
        viewModelScope.launch {
            if (teamRepository.isPokemonInTeam(pokemon.id)) {
                _uiState.update {
                    it.copy(errorMessage = "${pokemon.name.replaceFirstChar { c -> c.titlecase() }} ya está en tu equipo.")
                }
                return@launch
            }

            val currentFilledSlots = uiState.value.teamMembers.count { it != null }
            if (currentFilledSlots < TeamRepositoryImpl.MAX_TEAM_SIZE) {
                val firstEmptySlotIndex = uiState.value.teamMembers.indexOfFirst { it == null }
                if (firstEmptySlotIndex != -1) {
                    teamRepository.addPokemonToTeam(pokemon, firstEmptySlotIndex)
                } else {
                    // Situación inesperada: el conteo dice que no está lleno, pero no hay slots vacíos
                    Log.e("TeamViewModel", "Error: No empty slot found but team not full.")
                    _uiState.update { it.copy(errorMessage = "Error al encontrar un hueco vacío.") }
                }
            } else {
                // Equipo lleno, mostrar diálogo de reemplazo
                _uiState.update {
                    it.copy(
                        showReplaceDialog = true,
                        pokemonToAddOrReplace = pokemon
                    )
                }
            }
        }
    }

    /**
     * Quita un Pokémon del slot especificado en el equipo.
     *
     * @param slotIndex Índice del slot del cual quitar el Pokémon.
     */
    fun removePokemonFromSlot(slotIndex: Int) {
        if (slotIndex < 0 || slotIndex >= TeamRepositoryImpl.MAX_TEAM_SIZE) {
            Log.e("TeamViewModel", "Invalid slotIndex for removal: $slotIndex")
            return
        }
        viewModelScope.launch {
            teamRepository.removePokemonFromSlot(slotIndex)
        }
    }

    /**
     * Reemplaza un Pokémon en un slot específico del equipo por uno nuevo.
     * Si el nuevo Pokémon ya existe en otro slot, se elimina de ese otro slot primero.
     *
     * @param slotIndexToReplace Índice del slot donde se colocará el nuevo Pokémon.
     * @param newPokemon El nuevo Pokémon a añadir.
     */
    fun replacePokemonInSlot(slotIndexToReplace: Int, newPokemon: PokemonDetailDto) {
        if (slotIndexToReplace < 0 || slotIndexToReplace >= TeamRepositoryImpl.MAX_TEAM_SIZE) {
            Log.e("TeamViewModel", "Invalid slotIndex for replacement: $slotIndexToReplace")
            return
        }

        viewModelScope.launch {
            // Verificar si el Pokémon a añadir ya está en el equipo en un slot DIFERENTE al que se va a reemplazar.
            // Si es así, se quita primero de su slot actual para evitar duplicados lógicos (misma ID).
            val existingPokemonInstanceInTeam =
                uiState.value.teamMembers.find { it?.id == newPokemon.id }
            if (existingPokemonInstanceInTeam != null) {
                val indexOfExisting =
                    uiState.value.teamMembers.indexOf(existingPokemonInstanceInTeam)
                if (indexOfExisting != -1 && indexOfExisting != slotIndexToReplace) {
                    // Si el Pokémon existe y no está en el slot que vamos a usar para el reemplazo,
                    // entonces lo quitamos de su posición actual.
                    teamRepository.removePokemonById(newPokemon.id)
                }
            }
            // Ahora añade/reemplaza el Pokémon en el slot deseado.
            teamRepository.addPokemonToTeam(newPokemon, slotIndexToReplace)
            _uiState.update {
                it.copy(
                    showReplaceDialog = false,
                    pokemonToAddOrReplace = null,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Cancela la operación de reemplazo de Pokémon y oculta el diálogo.
     */
    fun cancelReplaceDialog() {
        _uiState.update {
            it.copy(showReplaceDialog = false, pokemonToAddOrReplace = null, errorMessage = null)
        }
    }

    /**
     * Limpia el mensaje de error actual en el estado de la UI.
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}