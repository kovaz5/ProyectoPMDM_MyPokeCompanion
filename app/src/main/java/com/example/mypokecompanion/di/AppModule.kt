package com.example.mypokecompanion.di

import TeamDao
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mypokecompanion.data.local.database.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.mypokecompanion.data.remote.api.PokeApiService
import com.example.mypokecompanion.data.repository.IPokemonRepository
import com.example.mypokecompanion.data.repository.ITeamRepository
import com.example.mypokecompanion.data.repository.PokemonRepositoryImpl
import com.example.mypokecompanion.data.repository.TeamRepositoryImpl
import com.example.mypokecompanion.ui.screens.detail.PokemonDetailViewModel
import com.example.mypokecompanion.ui.screens.pokemonlist.PokemonListViewModel
import com.example.mypokecompanion.ui.screens.team.TeamViewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Objeto Singleton que actúa como un módulo de inyección de dependencias manual.
 * Proporciona instancias de varias clases necesarias en la aplicación, como
 * servicios de red, repositorios y la factory para ViewModels.
 * Se utiliza la inicialización 'lazy' para crear instancias solo cuando se necesitan por primera vez.
 */
object AppModule {

    /**
     * Instancia 'lazy' de Moshi para la serialización/deserialización de JSON.
     * Configurada con [KotlinJsonAdapterFactory] para un correcto manejo de clases de datos Kotlin.
     */
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Instancia 'lazy' de Retrofit para realizar llamadas a la API.
     * Configurada con la URL base de PokeAPI y el conversor Moshi.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Instancia 'lazy' del servicio [PokeApiService] creado por Retrofit.
     * Esta es la interfaz a través de la cual se realizan las llamadas a la PokeAPI.
     */
    val pokeApiService: PokeApiService by lazy {
        retrofit.create(PokeApiService::class.java)
    }

    // La función provideBasePokemonPagingSource no es estrictamente necesaria aquí si
    // PokemonRepositoryImpl crea sus PagingSources internamente.
    // Si se mantuviera, sería para un caso muy específico.
    /*
    private fun provideBasePokemonPagingSource(apiService: PokeApiService): PokemonPagingSource {
        // Para la instancia base que usa el repositorio al inicio, pasamos null para la query.
        return PokemonPagingSource(apiService, null)
    }
    */

    /**
     * Instancia 'lazy' de [IPokemonRepository].
     * Actualmente proporciona [PokemonRepositoryImpl], que recibe el [pokeApiService].
     * [PokemonRepositoryImpl] es responsable de crear las instancias de [PokemonPagingSource]
     * según sea necesario, pasándoles la query correspondiente.
     */
    val pokemonRepository: IPokemonRepository by lazy {
        // PokemonRepositoryImpl es responsable de crear PagingSources
        // con o sin query internamente cuando se llama a getPokemonPagingData.
        PokemonRepositoryImpl(pokeApiService)
    }

    /**
     * Función privada para proveer la instancia de [AppDatabase] (Room).
     * Necesita el contexto de la aplicación.
     *
     * @param application La instancia de [Application].
     * @return La instancia singleton de [AppDatabase].
     */
    private fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.getDatabase(application.applicationContext)
    }

    /**
     * Función privada para proveer la instancia de [TeamDao].
     * Necesita la instancia de [AppDatabase].
     *
     * @param appDatabase La instancia de [AppDatabase].
     * @return La instancia de [TeamDao].
     */
    private fun provideTeamDao(appDatabase: AppDatabase): TeamDao {
        return appDatabase.teamDao()
    }

    // La propiedad teamRepository se elimina de aquí porque su creación depende del contexto
    // de la aplicación (para el DAO), y se manejará dentro de la ViewModelFactory.

    /**
     * Implementación de [ViewModelProvider.Factory] para crear instancias de ViewModels.
     * Esta factory permite inyectar dependencias (como repositorios) en los constructores
     * de los ViewModels.
     * Utiliza [CreationExtras] para obtener el contexto de la aplicación cuando sea necesario.
     */
    @Suppress("UNCHECKED_CAST") // Necesario para el casting genérico de ViewModel.
    val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            // Obtiene la instancia de Application desde los extras. Esencial para crear la BD y el DAO.
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

            // Crear dependencias que necesitan 'application' (y por ende, se crean por petición):
            val appDatabase = provideAppDatabase(application)
            val teamDao = provideTeamDao(appDatabase)
            // Se crea una instancia de ITeamRepository aquí, ya que depende de teamDao.
            val currentTeamRepository: ITeamRepository = TeamRepositoryImpl(teamDao)

            // IPokemonRepository no depende directamente de 'application', por lo que podemos
            // usar la instancia singleton definida en AppModule.
            val currentPokemonRepository: IPokemonRepository = pokemonRepository

            // Devuelve la instancia del ViewModel solicitado, inyectando las dependencias.
            return when {
                modelClass.isAssignableFrom(PokemonListViewModel::class.java) -> {
                    PokemonListViewModel(currentPokemonRepository) as T
                }
                modelClass.isAssignableFrom(PokemonDetailViewModel::class.java) -> {
                    // PokemonDetailViewModel ahora debería tomar IPokemonRepository
                    // y, si es necesario, ITeamRepository.
                    // Asumiendo que solo necesita IPokemonRepository por ahora (y quizás ITeamRepository para añadir al equipo):
                    PokemonDetailViewModel(currentPokemonRepository /*, currentTeamRepository */) as T
                }
                modelClass.isAssignableFrom(TeamViewModel::class.java) -> {
                    TeamViewModel(currentTeamRepository) as T
                }
                else -> throw IllegalArgumentException("Clase ViewModel desconocida en AppModule.viewModelFactory: ${modelClass.name}.")
            }
        }
    }
}