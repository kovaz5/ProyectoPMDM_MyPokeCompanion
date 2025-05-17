package com.example.mypokecompanion.ui.screens.pokemonlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mypokecompanion.data.remote.dto.PokemonResultDto
import com.example.mypokecompanion.data.repository.IPokemonRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

/**
 * ViewModel para [PokemonListScreen].
 * Maneja la lógica de obtención y filtrado de la lista de Pokémon.
 *
 * @param pokemonRepository Repositorio para acceder a los datos de Pokémon.
 */
class PokemonListViewModel(
    private val pokemonRepository: IPokemonRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    /**
     * Query de búsqueda actual introducida por el usuario.
     */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Flujo de datos paginados de Pokémon. Se actualiza según [searchQuery].
     * Utiliza `debounce` para optimizar las búsquedas y `cachedIn` para persistencia.
     */
    @OptIn(FlowPreview::class)
    val pokemonPagingDataFlow: Flow<PagingData<PokemonResultDto>> =
        _searchQuery
            .debounce(500L)
            .flatMapLatest { currentQuery ->
                Log.d("PokemonListVM", "flatMapLatest triggered with query: '$currentQuery'")
                pokemonRepository.getPokemonPagingData(currentQuery.trim().ifBlank { null })
            }
            .cachedIn(viewModelScope)

    /**
     * Actualiza la [searchQuery].
     *
     * @param query La nueva query de búsqueda.
     */
    fun setSearchQuery(query: String) {
        Log.d("PokemonListVM", "setSearchQuery called with: '$query'")
        _searchQuery.value = query
        Log.d("PokemonListVM", "_searchQuery new value: '${_searchQuery.value}'")
    }
}