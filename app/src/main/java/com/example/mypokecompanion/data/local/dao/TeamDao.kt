import androidx.room.*
import com.example.mypokecompanion.data.local.entity.TeamMemberEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad TeamMemberEntity.
 * Define las operaciones de base de datos permitidas en la tabla "team_members".
 * Estas operaciones se utilizan para gestionar el equipo Pokémon del usuario.
 */
@Dao
interface TeamDao {

    /**
     * Inserta un miembro del equipo en la base de datos.
     * Si ya existe un Pokémon en el mismo 'slotPosition', será reemplazado
     * gracias a la estrategia de conflicto OnConflictStrategy.REPLACE.
     * Esta función es 'suspend' porque las operaciones de base de datos deben ser asíncronas.
     *
     * @param member El objeto TeamMemberEntity a insertar o reemplazar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TeamMemberEntity)

    /**
     * Elimina un miembro del equipo de la base de datos basándose en su posición en el slot.
     *
     * @param slotPosition La posición del slot del miembro del equipo a eliminar.
     */
    @Query("DELETE FROM team_members WHERE slotPosition = :slotPosition")
    suspend fun deleteTeamMemberBySlot(slotPosition: Int)

    /**
     * Obtiene todos los miembros del equipo de la base de datos, ordenados por su posición en el slot.
     * Devuelve un Flow<List<TeamMemberEntity>>, lo que permite observar cambios en los datos
     * de forma reactiva. La UI se actualizará automáticamente cuando los datos del equipo cambien.
     *
     * @return Un Flow que emite la lista de miembros del equipo cada vez que cambian.
     */
    @Query("SELECT * FROM team_members ORDER BY slotPosition ASC")
    fun getTeamMembers(): Flow<List<TeamMemberEntity>>

    /**
     * Obtiene un miembro específico del equipo por su ID de Pokémon.
     * Se limita a un resultado (LIMIT 1) ya que el ID del Pokémon debería ser único si está en el equipo.
     *
     * @param pokemonId El ID del Pokémon a buscar en el equipo.
     * @return El TeamMemberEntity si se encuentra, o null si no.
     */
    @Query("SELECT * FROM team_members WHERE id = :pokemonId LIMIT 1")
    suspend fun getTeamMemberById(pokemonId: Int): TeamMemberEntity?

    /**
     * Obtiene el número actual de miembros en el equipo.
     *
     * @return El total de Pokémon actualmente en el equipo.
     */
    @Query("SELECT COUNT(*) FROM team_members")
    suspend fun getTeamSize(): Int

    /**
     * Elimina un miembro del equipo de la base de datos basándose en el ID del Pokémon.
     * Esto es útil, por ejemplo, si un Pokémon se mueve de un slot a otro y
     * primero se debe asegurar que no exista una entrada previa con el mismo ID en otro slot.
     *
     * @param pokemonId El ID del Pokémon del miembro del equipo a eliminar.
     */
    @Query("DELETE FROM team_members WHERE id = :pokemonId")
    suspend fun deleteTeamMemberById(pokemonId: Int)
}