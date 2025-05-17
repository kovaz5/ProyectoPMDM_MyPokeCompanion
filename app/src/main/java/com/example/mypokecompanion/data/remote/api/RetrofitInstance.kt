package com.example.mypokecompanion.data.remote.api

import com.squareup.moshi.Moshi // Biblioteca para el procesamiento de JSON.
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory // Adaptador de Moshi para clases de datos Kotlin.
import retrofit2.Retrofit // Biblioteca para realizar llamadas HTTP.
import retrofit2.converter.moshi.MoshiConverterFactory // Converter para que Retrofit use Moshi.

/**
 * Objeto Singleton que proporciona una instancia configurada de Retrofit
 * para interactuar con la PokeAPI.
 * La inicialización se hace de forma 'lazy' para optimizar el rendimiento.
 */
object RetrofitInstance {

    /**
     * Instancia 'lazy' de Moshi.
     * Configurada con [KotlinJsonAdapterFactory] para permitir a Moshi trabajar
     * correctamente con clases de datos Kotlin y sus propiedades no nulas/anulables.
     */
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Añade soporte para reflexión de Kotlin.
            .build()
    }

    /**
     * Instancia 'lazy' de Retrofit.
     * Configurada con la URL base de la PokeAPI y el conversor de Moshi.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/") // URL base para todas las llamadas de la API.
            // Añade un conversor de fábrica para serializar y deserializar objetos usando Moshi.
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Usa la instancia de Moshi configurada.
            .build()
    }

    /**
     * Instancia 'lazy' del servicio [PokeApiService].
     * Retrofit crea una implementación de la interfaz [PokeApiService]
     * que puede usarse para realizar llamadas HTTP reales.
     */
    val pokeApiService: PokeApiService by lazy {
        retrofit.create(PokeApiService::class.java)
    }
}