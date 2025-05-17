package com.example.mypokecompanion.ui.screens.pokemonlist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.mypokecompanion.data.remote.dto.PokemonResultDto
import com.example.mypokecompanion.navigation.Screen
import com.example.mypokecompanion.ui.theme.MyPokeCompanionTheme
import com.example.mypokecompanion.di.AppModule
import androidx.compose.ui.text.style.TextAlign

/**
 * Pantalla principal que muestra una lista paginada de Pokémon.
 * Permite buscar y navegar a los detalles de un Pokémon.
 *
 * @param navController Controlador para la navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    navController: NavHostController,
) {
    val listViewModel: PokemonListViewModel = viewModel(factory = AppModule.viewModelFactory)
    val lazyPokemonItems = listViewModel.pokemonPagingDataFlow.collectAsLazyPagingItems()
    val searchQuery by listViewModel.searchQuery.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Pokédex",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Campo de búsqueda de Pokémon
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { listViewModel.setSearchQuery(it) },
                label = { Text("Buscar Pokémon") },
                placeholder = { Text("Ej: Pikachu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Search, "Icono de búsqueda")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { listViewModel.setSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, "Borrar búsqueda")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            )

            // Lista paginada de Pokémon
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = lazyPokemonItems.itemCount,
                    key = lazyPokemonItems.itemKey { it.name }
                ) { index ->
                    val pokemon = lazyPokemonItems[index]
                    pokemon?.let {
                        PokemonListItem(
                            pokemon = it,
                            onPokemonClick = { pokemonName ->
                                Log.d("PokemonListScreen", "Navegando a: $pokemonName")
                                navController.navigate(Screen.PokemonDetail.createRoute(pokemonName))
                            }
                        )
                    }
                }

                // Indicadores de estado de carga y error para Paging
                lazyPokemonItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { LoadingIndicator(Modifier.fillParentMaxSize()) }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                ErrorStateItem(
                                    message = "Error al cargar: ${e.error.localizedMessage}",
                                    onRetry = { lazyPokemonItems.retry() }
                                )
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { LoadingIndicator(Modifier.fillMaxWidth().padding(vertical = 16.dp)) }
                        }
                        loadState.append is LoadState.Error -> {
                            item { AppendErrorIndicator() }
                        }
                        loadState.refresh is LoadState.NotLoading && lazyPokemonItems.itemCount == 0 -> {
                            item {
                                EmptyStateMessage(
                                    message = if (searchQuery.isNotEmpty()) "No se encontraron Pokémon para \"$searchQuery\""
                                    else "Explora el Pokédex para encontrar tus Pokémon favoritos.",
                                    modifier = Modifier.fillParentMaxSize() // Pasa el modifier aquí
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Representa un ítem individual en la lista de Pokémon.
 *
 * @param pokemon Datos del Pokémon a mostrar.
 * @param onPokemonClick Acción al hacer clic en el ítem.
 */
@Composable
fun PokemonListItem(
    pokemon: PokemonResultDto,
    onPokemonClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPokemonClick(pokemon.name) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = pokemon.name.replaceFirstChar { it.titlecase() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Muestra un mensaje de error con opción de reintento.
 *
 * @param message Mensaje de error.
 * @param onRetry Acción para reintentar la carga.
 */
@Composable
fun ErrorStateItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Reintentar")
        }
    }
}

/** Muestra un indicador de carga genérico. */
@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

/** Muestra un mensaje de error para la carga de más ítems (append). */
@Composable
private fun AppendErrorIndicator() {
    // Log.e("PokemonListScreen", "Error al cargar más Pokémon: ${(lazyPokemonItems.loadState.append as? LoadState.Error)?.error?.localizedMessage}") // Mover log a ViewModel o quitar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Error al cargar más. Intenta de nuevo.", textAlign = TextAlign.Center)
        // Podría incluir un botón de reintento si se desea:
        // Button(onClick = { lazyPokemonItems.retry() }) { Text("Reintentar") }
    }
}

/** Muestra un mensaje cuando la lista está vacía o no hay resultados de búsqueda. */
@Composable
private fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier // Acepta un modifier
) {
    Box(
        modifier = modifier // Usa el modifier proporcionado
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}


// --- Previews ---

@Preview(showBackground = true, name = "Pokemon List Item Preview")
@Composable
fun PokemonListItemPreview() {
    MyPokeCompanionTheme {
        PokemonListItem(
            pokemon = PokemonResultDto("Pikachu", "url"),
            onPokemonClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State Item Preview")
@Composable
fun ErrorStateItemPreview() {
    MyPokeCompanionTheme {
        ErrorStateItem(message = "Error de prueba", onRetry = {})
    }
}

@Preview(showBackground = true, name = "Loading Indicator Preview")
@Composable
fun LoadingIndicatorPreview() {
    MyPokeCompanionTheme {
        LoadingIndicator(Modifier.size(100.dp))
    }
}

@Preview(showBackground = true, name = "Empty State Preview - No Search Results")
@Composable
fun EmptyStateNoResultsPreview() {
    MyPokeCompanionTheme {
        // Para la preview, puedes usar fillMaxSize() o un tamaño específico
        EmptyStateMessage(
            message = "No se encontraron Pokémon para \"Mewtwo\"",
            modifier = Modifier.fillMaxSize() // O Modifier.height(200.dp).fillMaxWidth() para un tamaño fijo
        )
    }
}

@Preview(showBackground = true, name = "Empty State Preview - Initial Empty")
@Composable
fun EmptyStateInitialPreview() {
    MyPokeCompanionTheme {
        EmptyStateMessage(
            message = "Explora el Pokédex para encontrar tus Pokémon favoritos.",
            modifier = Modifier.fillMaxSize()
        )
    }
}