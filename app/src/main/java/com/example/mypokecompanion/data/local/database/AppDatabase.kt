package com.example.mypokecompanion.data.local.database

import TeamDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mypokecompanion.data.local.entity.TeamMemberEntity

/**
 * Clase principal de la base de datos Room para la aplicación.
 * Define las entidades que contiene la base de datos y proporciona acceso a los DAOs.
 *
 * @property entities Lista de clases de entidad que forman parte de esta base de datos.
 * @property version Versión de la base de datos. Debe incrementarse al cambiar el esquema.
 * @property exportSchema Define si se debe exportar el esquema de la base de datos a un archivo JSON.
 *                        Es útil para el control de versiones del esquema, pero se puede desactivar para proyectos más simples.
 */
@Database(entities = [TeamMemberEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Método abstracto que Room implementará para proporcionar una instancia del TeamDao.
     * A través de este método, se accede a las operaciones de base de datos para la entidad TeamMember.
     *
     * @return Una instancia de TeamDao.
     */
    abstract fun teamDao(): TeamDao

    /**
     * Objeto companion para implementar el patrón Singleton y obtener una instancia de la base de datos.
     * Esto asegura que solo exista una instancia de la base de datos en toda la aplicación,
     * lo cual es importante para el rendimiento y la consistencia de los datos.
     */
    companion object {
        /**
         * Variable volátil para la instancia Singleton de AppDatabase.
         * @Volatile asegura que los cambios en esta variable sean visibles inmediatamente para todos los hilos.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia Singleton de la base de datos.
         * Si la instancia no existe, la crea de forma segura para hilos (thread-safe).
         *
         * @param context El contexto de la aplicación, necesario para construir la base de datos.
         * @return La instancia Singleton de AppDatabase.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Retorna la instancia existente si ya fue creada.
            return INSTANCE ?: synchronized(this) {
                // Si INSTANCE es null, entra en el bloque sincronizado para crearla.
                // 'synchronized(this)' previene que múltiples hilos creen la instancia simultáneamente.
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Usa el contexto de la aplicación para evitar memory leaks.
                    AppDatabase::class.java,    // La clase de la base de datos.
                    "pokemon_team_database"     // El nombre del archivo de la base de datos.
                )
                    // .fallbackToDestructiveMigration() // Opcional: Si se incrementa la versión de la BD
                    // y no se proveen migraciones, esto eliminará
                    // y recreará la base de datos (perdiendo datos).
                    // Útil durante el desarrollo, pero no para producción
                    // si la pérdida de datos es inaceptable.
                    .build()
                INSTANCE = instance // Asigna la nueva instancia a INSTANCE.
                instance            // Retorna la instancia creada.
            }
        }
    }
}