package com.example.mypokecompanion.data.repository

import TeamDao
import android.util.Log
import com.example.mypokecompanion.data.local.entity.TeamMemberEntity
import com.example.mypokecompanion.data.remote.dto.OfficialArtworkDto
import com.example.mypokecompanion.data.remote.dto.OtherSpritesDto
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.remote.dto.PokemonSpritesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Importación necesaria para el operador .map en Flows

/**
 * Implementación de [ITeamRepository].
 * Gestiona los datos del equipo Pokémon del usuario interactuando con el [TeamDao] (Room).
 *
 * @property teamDao El Data Access Object para la tabla de miembros del equipo.
 */
class TeamRepositoryImpl(
    private val teamDao: TeamDao
) : ITeamRepository {

    /**
     * Implementación de [ITeamRepository.teamMembersFlow].
     * Obtiene un [Flow] de entidades [TeamMemberEntity] desde el [TeamDao] y las transforma
     * en un [Flow] de listas de [PokemonDetailDto?].
     * Cada lista emitida representa el estado actual del equipo, con un tamaño fijo (MAX_TEAM_SIZE)
     * donde los slots vacíos son representados por `null`.
     */
    override val teamMembersFlow: Flow<List<PokemonDetailDto?>> =
        teamDao.getTeamMembers() // Obtiene el Flow de la base de datos.
            .map { entities: List<TeamMemberEntity> -> // Aplica una transformación a cada lista emitida por el Flow.
                Log.d("TeamRepositoryImpl", "BD emitió ${entities.size} entidades de equipo. Mapeando a DTOs...")
                // Crea una lista mutable de tamaño MAX_TEAM_SIZE inicializada con nulos.
                val teamWithNulls = MutableList<PokemonDetailDto?>(MAX_TEAM_SIZE) { null }

                // Itera sobre las entidades obtenidas de la base de datos.
                entities.forEach { entity ->
                    if (entity.slotPosition >= 0 && entity.slotPosition < MAX_TEAM_SIZE) {
                        // Si la posición del slot es válida, mapea la entidad a un DTO parcial y colócala en la lista.
                        teamWithNulls[entity.slotPosition] = mapEntityToPartialDetailDto(entity)
                    } else {
                        // Registra una advertencia si una entidad tiene una posición de slot inválida.
                        Log.w("TeamRepositoryImpl", "Entidad ${entity.name} tiene slotPosition inválido: ${entity.slotPosition}")
                    }
                }
                Log.d("TeamRepositoryImpl", "Mapeo completo. DTOs de equipo: ${teamWithNulls.count { it != null }} miembros.")
                teamWithNulls // Emite la lista transformada.
            }

    /**
     * Función privada para mapear una [TeamMemberEntity] (de la base de datos)
     * a un [PokemonDetailDto] parcial. Este DTO solo contendrá la información
     * necesaria para mostrar el Pokémon en la lista del equipo (ID, nombre, imagen).
     * Otros campos como tipos, estadísticas, etc., no se rellenan aquí ya que no
     * se almacenan completamente en la entidad del equipo.
     *
     * @param entity La entidad del miembro del equipo a mapear.
     * @return Un [PokemonDetailDto] con información parcial.
     */
    private fun mapEntityToPartialDetailDto(entity: TeamMemberEntity): PokemonDetailDto {
        return PokemonDetailDto(
            id = entity.id,
            name = entity.name,
            sprites = PokemonSpritesDto( // Construye la estructura de sprites necesaria.
                frontDefault = null, // No se almacena el frontDefault directamente en la entidad.
                otherSprites = OtherSpritesDto(
                    officialArtwork = OfficialArtworkDto(
                        frontDefault = entity.imageUrl // Usa la URL de imagen almacenada.
                    )
                )
            ),
            types = emptyList(),    // Los tipos no se almacenan en TeamMemberEntity.
            stats = emptyList(),    // Las estadísticas no se almacenan en TeamMemberEntity.
            height = 0,             // La altura no se almacena en TeamMemberEntity.
            weight = 0              // El peso no se almacena en TeamMemberEntity.
        )
    }

    /**
     * Implementación de [ITeamRepository.addPokemonToTeam].
     * Añade un Pokémon al equipo en el slot especificado.
     * Convierte el [PokemonDetailDto] a [TeamMemberEntity] antes de insertarlo en la BD.
     *
     * @param pokemon El Pokémon a añadir.
     * @param slot El slot donde se añadirá.
     */
    override suspend fun addPokemonToTeam(pokemon: PokemonDetailDto, slot: Int) {
        // Valida que el slot esté dentro de los límites permitidos.
        if (slot < 0 || slot >= MAX_TEAM_SIZE) {
            Log.e("TeamRepositoryImpl", "Intento de añadir Pokémon a slot inválido: $slot")
            return
        }
        // Crea la entidad para la base de datos.
        val entity = TeamMemberEntity(
            id = pokemon.id,
            name = pokemon.name,
            // Intenta obtener la URL del artwork oficial, si no, la frontal por defecto.
            imageUrl = pokemon.sprites.otherSprites?.officialArtwork?.frontDefault
                ?: pokemon.sprites.frontDefault
                ?: "", // Si ninguna URL está disponible, usa una cadena vacía.
            slotPosition = slot
        )
        if (entity.imageUrl.isEmpty()) {
            Log.w("TeamRepositoryImpl", "Añadiendo Pokémon ${pokemon.name} al equipo sin una URL de imagen válida.")
        }
        teamDao.insertTeamMember(entity) // Inserta el miembro en la base de datos.
        Log.d("TeamRepositoryImpl", "Añadido ${pokemon.name} al slot $slot.")
    }

    /**
     * Implementación de [ITeamRepository.removePokemonFromSlot].
     * Elimina un Pokémon del equipo basándose en su posición de slot.
     *
     * @param slotPosition La posición del slot del Pokémon a eliminar.
     */
    override suspend fun removePokemonFromSlot(slotPosition: Int) {
        // Valida que el slot esté dentro de los límites permitidos.
        if (slotPosition < 0 || slotPosition >= MAX_TEAM_SIZE) {
            Log.e("TeamRepositoryImpl", "Intento de eliminar Pokémon de slot inválido: $slotPosition")
            return
        }
        teamDao.deleteTeamMemberBySlot(slotPosition) // Llama al DAO para eliminar.
        Log.d("TeamRepositoryImpl", "Eliminado Pokémon del slot $slotPosition.")
    }

    /**
     * Implementación de [ITeamRepository.isPokemonInTeam].
     * Verifica si un Pokémon ya existe en el equipo consultando la base de datos por ID.
     *
     * @param pokemonId El ID del Pokémon a verificar.
     * @return `true` si el Pokémon está en el equipo, `false` de lo contrario.
     */
    override suspend fun isPokemonInTeam(pokemonId: Int): Boolean {
        val isInTeam = teamDao.getTeamMemberById(pokemonId) != null
        Log.d("TeamRepositoryImpl", "Pokémon ID $pokemonId está en el equipo: $isInTeam")
        return isInTeam
    }

    /**
     * Implementación de [ITeamRepository.removePokemonById].
     * Elimina todas las entradas de un Pokémon del equipo basándose en su ID.
     *
     * @param pokemonId El ID del Pokémon a eliminar.
     */
    override suspend fun removePokemonById(pokemonId: Int) {
        teamDao.deleteTeamMemberById(pokemonId) // Llama al DAO para eliminar por ID.
        Log.d("TeamRepositoryImpl", "Eliminado Pokémon por ID $pokemonId (si existía).")
    }

    companion object {
        /**
         * Constante que define el tamaño máximo del equipo Pokémon.
         */
        const val MAX_TEAM_SIZE = 6
    }
}