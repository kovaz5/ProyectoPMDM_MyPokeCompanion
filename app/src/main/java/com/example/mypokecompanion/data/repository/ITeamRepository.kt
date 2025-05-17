package com.example.mypokecompanion.data.repository

import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto // DTO que representa un miembro del equipo para la UI
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para el repositorio del equipo Pokémon.
 * Se encarga de la lógica de gestión del equipo del usuario, interactuando
 * con la fuente de datos local (Room).
 */
interface ITeamRepository {
    /**
     * Un [Flow] que emite la lista actual de miembros del equipo.
     * La lista puede contener elementos nulos para representar slots vacíos en el equipo.
     * Permite a los observadores (ViewModels) reaccionar a los cambios en el equipo
     * de forma automática.
     */
    val teamMembersFlow: Flow<List<PokemonDetailDto?>>

    /**
     * Añade un Pokémon a un slot específico del equipo.
     *
     * @param pokemon El [PokemonDetailDto] del Pokémon a añadir.
     * @param slot El índice del slot (posición) donde se añadirá el Pokémon.
     */
    suspend fun addPokemonToTeam(pokemon: PokemonDetailDto, slot: Int)

    /**
     * Elimina un Pokémon de un slot específico del equipo.
     *
     * @param slotPosition El índice del slot del cual se eliminará el Pokémon.
     */
    suspend fun removePokemonFromSlot(slotPosition: Int)

    /**
     * Comprueba si un Pokémon (identificado por su ID) ya está en el equipo.
     *
     * @param pokemonId El ID del Pokémon a verificar.
     * @return `true` si el Pokémon está en el equipo, `false` en caso contrario.
     */
    suspend fun isPokemonInTeam(pokemonId: Int): Boolean

    /**
     * Elimina un Pokémon del equipo basándose en su ID.
     * Útil si se necesita eliminar un Pokémon independientemente de su slot actual.
     *
     * @param pokemonId El ID del Pokémon a eliminar.
     */
    suspend fun removePokemonById(pokemonId: Int)

    // Aquí se pueden añadir otras funciones públicas necesarias para el repositorio del equipo.
}