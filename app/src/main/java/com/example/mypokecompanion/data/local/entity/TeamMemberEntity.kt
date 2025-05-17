package com.example.mypokecompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un miembro del equipo Pokémon en la base de datos local.
 * La anotación @Entity marca esta clase como una tabla en la base de datos Room.
 *
 * @property tableName El nombre de la tabla en la base de datos.
 */
@Entity(tableName = "team_members")
data class TeamMemberEntity(
    /**
     * Clave primaria de la tabla. Corresponde al ID del Pokémon obtenido de la PokéAPI.
     * Esto asegura que cada Pokémon en la base de datos (si está en el equipo) tenga un identificador único.
     */
    @PrimaryKey val id: Int,

    /**
     * Nombre del Pokémon.
     */
    val name: String,

    /**
     * URL de la imagen del Pokémon.
     * Se almacena para poder mostrarla en la UI sin necesidad de hacer una nueva llamada a la API
     * solo para obtener la imagen del Pokémon en el equipo.
     */
    val imageUrl: String,

    /**
     * Posición del Pokémon dentro del equipo.
     * Este campo se utiliza para determinar en qué "hueco" o slot del equipo se encuentra el Pokémon (ej. 0 a 5).
     * Es importante para mantener el orden del equipo y para operaciones como reemplazar un Pokémon en un slot específico.
     */
    val slotPosition: Int
)