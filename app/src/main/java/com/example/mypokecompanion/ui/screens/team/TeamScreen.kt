package com.example.mypokecompanion.ui.screens.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
// import androidx.compose.foundation.shape.CircleShape // No se usa directamente
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.DeleteForever
// import androidx.compose.material.icons.filled.ErrorOutline // No se usa directamente
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp // No se usa directamente, el tema lo gestiona
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// import androidx.navigation.compose.rememberNavController // No se usa directamente en esta pantalla
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.mypokecompanion.R
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.remote.dto.PokemonSpritesDto
import com.example.mypokecompanion.di.AppModule
import com.example.mypokecompanion.navigation.Screen
import com.example.mypokecompanion.ui.theme.MyPokeCompanionTheme

/**
 * Pantalla que muestra el equipo Pokémon del usuario.
 * Permite ver los Pokémon del equipo, navegar a sus detalles o quitarlos.
 *
 * @param navController Controlador para la navegación.
 * @param teamViewModel ViewModel para gestionar el estado del equipo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    navController: NavController,
    teamViewModel: TeamViewModel = viewModel(factory = AppModule.viewModelFactory)
) {
    val uiState by teamViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface( // Barra superior personalizada
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Mi Equipo Pokémon",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.teamMembers.all { it == null }) {
                EmptyTeamMessage() // Muestra mensaje si el equipo está vacío
            } else {
                // Cuadrícula para mostrar los Pokémon del equipo
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(
                        items = uiState.teamMembers,
                        key = { index, _ -> "team_pokemon_$index" }
                    ) { index, pokemon ->
                        PokemonTeamSlot(
                            pokemon = pokemon,
                            onSlotClick = {
                                pokemon?.let { pkmn ->
                                    // Navega al detalle si el Pokémon existe y tiene nombre
                                    if (pkmn.name.isNotBlank()) {
                                        navController.navigate(Screen.PokemonDetail.createRoute(pkmn.name.lowercase()))
                                    }
                                }
                            },
                            onRemoveClick = {
                                pokemon?.let { teamViewModel.removePokemonFromSlot(index) }
                            },
                            slotNumber = index + 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Representa un slot individual en la cuadrícula del equipo Pokémon.
 * Muestra la información del Pokémon o un indicador de slot vacío.
 *
 * @param pokemon El Pokémon en este slot, o null si está vacío.
 * @param onSlotClick Acción al hacer clic en el slot.
 * @param onRemoveClick Acción para quitar el Pokémon del slot.
 * @param slotNumber Número del slot (1-6).
 */
@Composable
fun PokemonTeamSlot(
    pokemon: PokemonDetailDto?,
    onSlotClick: () -> Unit,
    onRemoveClick: () -> Unit,
    slotNumber: Int
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Mantiene el slot cuadrado
            .fillMaxWidth()
            .clickable(enabled = pokemon != null, onClick = onSlotClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (pokemon != null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = if (pokemon != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (pokemon != null) {
                // Contenido para un slot con Pokémon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pokemon.sprites.otherSprites?.officialArtwork?.frontDefault ?: pokemon.sprites.frontDefault)
                            .crossfade(true)
                            .build(),
                        contentDescription = pokemon.name,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit,
                        loading = { CircularProgressIndicator(modifier = Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary) },
                        error = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pokeball_placeholder),
                                contentDescription = "Error al cargar imagen",
                                modifier = Modifier.size(60.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                            )
                        },
                        success = { SubcomposeAsyncImageContent() }
                    )
                    Text(
                        text = pokemon.name.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = "Quitar ${pokemon.name}",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                // Contenido para un slot vacío
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clickable(onClick = onSlotClick) // Podría usarse para añadir Pokémon
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Slot vacío",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Slot ${slotNumber}\nVacío",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Mensaje que se muestra cuando el equipo Pokémon está vacío.
 */
@Composable
fun EmptyTeamMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_team_placeholder),
                contentDescription = "Equipo Vacío",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Tu equipo Pokémon está vacío.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "¡Ve al Pokédex y añade tus Pokémon favoritos!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- PREVIEWS ---
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Team Screen with Pokemon")
@Composable
private fun TeamScreenWithPokemonPreview() { // Se hizo privada
    MyPokeCompanionTheme {
        val samplePokemon1 = PokemonDetailDto(1, "Bulbasaur", PokemonSpritesDto("url1", null), emptyList(), emptyList(), 7, 69)
        val samplePokemon2 = PokemonDetailDto(4, "Charmander", PokemonSpritesDto("url2", null), emptyList(), emptyList(), 6, 85)
        val mockUiState = TeamUiState(
            teamMembers = listOf(samplePokemon1, samplePokemon2, null, null, null, null)
        )

        Scaffold(
            topBar = { TopAppBar(title = { Text("Mi Equipo Pokémon") }) }
        ) { paddingValues ->
            Surface(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(mockUiState.teamMembers) { index, pokemon ->
                        PokemonTeamSlot(
                            pokemon = pokemon,
                            onSlotClick = {},
                            onRemoveClick = {},
                            slotNumber = index + 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Team Screen Empty")
@Composable
private fun TeamScreenEmptyPreview() { // Se hizo privada
    MyPokeCompanionTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Mi Equipo Pokémon") }) }
        ) { paddingValues ->
            Surface(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                EmptyTeamMessage()
            }
        }
    }
}

@Preview(showBackground = true, name = "Pokemon Team Slot - Filled")
@Composable
private fun PokemonTeamSlotFilledPreview() { // Se hizo privada
    MyPokeCompanionTheme {
        val samplePokemon = PokemonDetailDto(
            id = 25, name = "Pikachu",
            sprites = PokemonSpritesDto(frontDefault = "url_placeholder", otherSprites = null),
            types = emptyList(), stats = emptyList(), height = 4, weight = 60
        )
        PokemonTeamSlot(pokemon = samplePokemon, onSlotClick = {}, onRemoveClick = {}, slotNumber = 1)
    }
}

@Preview(showBackground = true, name = "Pokemon Team Slot - Empty")
@Composable
private fun PokemonTeamSlotEmptyPreview() { // Se hizo privada
    MyPokeCompanionTheme {
        PokemonTeamSlot(pokemon = null, onSlotClick = {}, onRemoveClick = {}, slotNumber = 3)
    }
}