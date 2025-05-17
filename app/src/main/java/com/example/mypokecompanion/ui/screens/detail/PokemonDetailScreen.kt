package com.example.mypokecompanion.ui.screens.detail

import com.example.mypokecompanion.util.PokemonTranslationUtil
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.request.ImageRequest
import com.example.mypokecompanion.data.remote.dto.PokemonDetailDto
import com.example.mypokecompanion.data.remote.dto.PokemonSpritesDto
import com.example.mypokecompanion.data.remote.dto.PokemonTypeDto
import com.example.mypokecompanion.data.remote.dto.PokemonTypeSlotDto
import com.example.mypokecompanion.di.AppModule
import com.example.mypokecompanion.ui.theme.MyPokeCompanionTheme
import com.example.mypokecompanion.ui.theme.PokemonTypeColors
import com.example.mypokecompanion.ui.screens.team.TeamViewModel
import com.example.mypokecompanion.ui.screens.pokemonlist.ErrorStateItem
import kotlin.text.replaceFirstChar
import kotlin.text.titlecase

/**
 * Pantalla principal que muestra los detalles de un Pokémon específico.
 * Esta pantalla obtiene los datos del [PokemonDetailViewModel] y también interactúa
 * con el [TeamViewModel] para gestionar la adición o reemplazo de Pokémon en el equipo.
 *
 * @param navController Controlador de navegación para acciones como volver a la pantalla anterior.
 * @param pokemonName El nombre del Pokémon cuyos detalles se van a mostrar. Este se recibe como argumento de navegación.
 * @param detailViewModel ViewModel responsable de cargar y mantener el estado de los detalles del Pokémon.
 * @param teamViewModel ViewModel responsable de gestionar el equipo Pokémon del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para algunos componentes de Material 3
@Composable
fun PokemonDetailScreen(
    navController: NavHostController,
    pokemonName: String,
    detailViewModel: PokemonDetailViewModel = viewModel(factory = AppModule.viewModelFactory),
    teamViewModel: TeamViewModel = viewModel(factory = AppModule.viewModelFactory)
) {
    // Se observan los estados de la UI de ambos ViewModels como State de Compose.
    val detailUiState by detailViewModel.uiState.collectAsState()
    val teamUiState by teamViewModel.uiState.collectAsState()
    // Estado para gestionar la Snackbar, permitiendo mostrar mensajes al usuario.
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto lanzado cuando pokemonName cambia. Se utiliza para cargar los detalles del Pokémon.
    // Si pokemonName no está vacío, se llama a loadPokemonDetailByName del detailViewModel.
    LaunchedEffect(pokemonName) {
        if (pokemonName.isNotBlank()) {
            detailViewModel.loadPokemonDetailByName(pokemonName)
        }
    }

    // Efecto lanzado cuando el mensaje de error en teamUiState cambia.
    // Muestra una Snackbar si hay un mensaje de error y luego lo limpia para evitar repeticiones.
    LaunchedEffect(teamUiState.errorMessage) {
        teamUiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            teamViewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Contenedor para las Snackbars
        topBar = {
            // Determina el nombre del Pokémon a mostrar en la TopAppBar.
            // Si los detalles están disponibles, usa el nombre del Pokémon capitalizado.
            // De lo contrario, muestra "Detalle Pokémon" como título por defecto.
            val currentPokemonName = detailUiState.pokemonDetail?.name?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            } ?: "Detalle Pokémon"

            // Surface utilizada como TopAppBar personalizada para tener control sobre el color y contenido.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Altura estándar para TopAppBars
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de navegación para volver a la pantalla anterior.
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                    // Título de la pantalla, mostrando el nombre del Pokémon.
                    Text(
                        text = currentPokemonName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante.
                            .padding(start = 8.dp, end = 16.dp),
                        maxLines = 1, // Evita que el texto ocupe múltiples líneas.
                        overflow = TextOverflow.Ellipsis // Añade "..." si el texto es demasiado largo.
                    )
                }
            }
        }
    ) { paddingValues ->
        // Box principal que contiene el contenido de la pantalla.
        // Utiliza paddingValues del Scaffold para evitar solapamientos con la TopAppBar.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estructura 'when' para renderizar diferentes UI según el estado de detailUiState.
            when {
                // Muestra un indicador de progreso circular mientras se cargan los datos.
                detailUiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                // Muestra un mensaje de error si detailUiState.error no es nulo.
                // Utiliza el componente reutilizable ErrorStateItem.
                detailUiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateItem(
                            message = detailUiState.error ?: "Error desconocido",
                            onRetry = { detailViewModel.retryLoadPokemonDetail() } // Permite reintentar la carga.
                        )
                    }
                }
                // Si los detalles del Pokémon están disponibles (no son nulos).
                detailUiState.pokemonDetail != null -> {
                    val currentPokemon = detailUiState.pokemonDetail!! // Pokémon actual, no nulo aquí.
                    // Determina si el Pokémon actual ya está en el equipo del usuario.
                    val isPokemonInTeam =
                        teamUiState.teamMembers.any { it?.id == currentPokemon.id }

                    // Renderiza el contenido principal de los detalles del Pokémon.
                    ActualPokemonDetailContentScrollable(
                        pokemon = currentPokemon,
                        isPokemonInTeam = isPokemonInTeam,
                        isTeamFull = teamUiState.isTeamFull,
                        onAddToTeamClick = { teamViewModel.addPokemonToTeam(currentPokemon) },
                        // Parámetros relacionados con el diálogo de reemplazo.
                        // Se gestionan aquí para decidir si el diálogo debe mostrarse a nivel de pantalla.
                        showReplaceDialog = teamUiState.showReplaceDialog && teamUiState.pokemonToAddOrReplace?.id == currentPokemon.id,
                        currentTeamForDialog = teamUiState.teamMembers,
                        onConfirmReplaceInDialog = { slotIndexToReplace ->
                            teamViewModel.replacePokemonInSlot(slotIndexToReplace, currentPokemon)
                        },
                        onDismissReplaceDialog = { teamViewModel.cancelReplaceDialog() }
                    )
                }
                // Caso por defecto si no hay datos, ni error, ni está cargando.
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay detalles para mostrar.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Muestra el diálogo de reemplazo si es necesario.
            // Se superpone al contenido de la pantalla.
            val currentPokemonForDialog = detailUiState.pokemonDetail
            if (currentPokemonForDialog != null && teamUiState.showReplaceDialog && teamUiState.pokemonToAddOrReplace?.id == currentPokemonForDialog.id) {
                ReplacePokemonDialog(
                    pokemonToAdd = currentPokemonForDialog,
                    currentTeam = teamUiState.teamMembers,
                    onConfirmReplace = { slotIndexToReplace ->
                        teamViewModel.replacePokemonInSlot(slotIndexToReplace, currentPokemonForDialog)
                    },
                    onDismiss = { teamViewModel.cancelReplaceDialog() }
                )
            }
        }
    }
}

/**
 * Composable que muestra el contenido detallado de un Pokémon dentro de una [LazyColumn]
 * para permitir el desplazamiento si el contenido es extenso.
 *
 * @param pokemon El objeto [PokemonDetailDto] con los datos del Pokémon a mostrar.
 * @param isPokemonInTeam Booleano que indica si el Pokémon actual ya está en el equipo.
 * @param isTeamFull Booleano que indica si el equipo Pokémon está lleno.
 * @param onAddToTeamClick Lambda que se ejecuta cuando el usuario intenta añadir/reemplazar el Pokémon en el equipo.
 * @param showReplaceDialog (No utilizado directamente para UI aquí) Indica si el diálogo de reemplazo debería estar visible.
 *                          Se pasa por si este componente necesitara reaccionar a ello, aunque el diálogo se maneja a nivel de pantalla.
 * @param currentTeamForDialog (No utilizado directamente para UI aquí) La lista actual de miembros del equipo para el diálogo.
 * @param onConfirmReplaceInDialog (No utilizado directamente para UI aquí) Lambda para confirmar el reemplazo en el diálogo.
 * @param onDismissReplaceDialog (No utilizado directamente para UI aquí) Lambda para descartar el diálogo.
 * @param modifier Modificador de Compose para personalizar la apariencia o comportamiento.
 */
@Composable
fun ActualPokemonDetailContentScrollable(
    pokemon: PokemonDetailDto,
    isPokemonInTeam: Boolean,
    isTeamFull: Boolean,
    onAddToTeamClick: () -> Unit,
    showReplaceDialog: Boolean, // Parámetro pasado pero no usado directamente en la UI de este composable
    currentTeamForDialog: List<PokemonDetailDto?>, // Parámetro pasado
    onConfirmReplaceInDialog: (Int) -> Unit, // Parámetro pasado
    onDismissReplaceDialog: () -> Unit, // Parámetro pasado
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Padding horizontal general
        horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido horizontalmente
        contentPadding = PaddingValues(bottom = 80.dp) // Espacio inferior para evitar solapamientos
    ) {
        // Sección: Imagen del Pokémon
        item {
            Spacer(modifier = Modifier.height(24.dp)) // Espacio superior
            // Se obtiene la URL de la imagen, priorizando el artwork oficial.
            val imageUrl = pokemon.sprites.otherSprites?.officialArtwork?.frontDefault
                ?: pokemon.sprites.frontDefault
            // Carga asíncrona de la imagen del Pokémon con Coil.
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true) // Efecto de fundido al cargar la imagen
                    .build(),
                contentDescription = "Imagen de ${pokemon.name}",
                modifier = Modifier
                    .fillMaxWidth(0.65f) // Ocupa el 65% del ancho disponible
                    .aspectRatio(1f) // Mantiene una relación de aspecto cuadrada
                    .clip(CircleShape) // Recorta la imagen en forma de círculo
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) // Fondo sutil
                    .padding(8.dp) // Padding interno
                    .shadow(elevation = 8.dp, shape = CircleShape), // Sombra suave
                contentScale = ContentScale.Fit, // Ajusta la imagen dentro de los límites
                loading = { // Muestra un indicador de progreso mientras carga
                    CircularProgressIndicator(
                        modifier = Modifier.padding(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                error = { // Muestra un icono de error si la carga falla
                    Icon(
                        Icons.Filled.ErrorOutline,
                        "Error imagen",
                        modifier = Modifier.size(100.dp)
                    )
                },
                success = { SubcomposeAsyncImageContent() } // Muestra la imagen una vez cargada
            )
            Spacer(modifier = Modifier.height(24.dp)) // Espacio inferior
        }

        // Sección: Nombre del Pokémon
        item {
            Text(
                text = pokemon.name.replaceFirstChar { it.titlecase() }, // Nombre capitalizado
                style = MaterialTheme.typography.displaySmall, // Estilo de texto grande y prominente
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Sección: Tipos del Pokémon
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Itera sobre los tipos del Pokémon y muestra un chip para cada uno.
                pokemon.types.forEach { typeSlot ->
                    PokemonTypeChip(typeSlot.type.name)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección: Información de Altura, Peso y Botón de Añadir/Reemplazar
        item {
            Card( // Agrupa la información en una tarjeta para mejorar la estructura visual.
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row( // Muestra la altura y el peso lado a lado.
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PokemonStatItem("Altura", "${pokemon.height / 10.0} m")
                        PokemonStatItem("Peso", "${pokemon.weight / 10.0} kg")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Botón para añadir al equipo o reemplazar si está lleno.
                    AddOrReplaceTeamButton(
                        pokemon = pokemon,
                        isPokemonInTeam = isPokemonInTeam,
                        isTeamFull = isTeamFull,
                        onAddToTeamClick = onAddToTeamClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección: Estadísticas Base
        item {
            Text(
                "Estadísticas Base",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start // Alineado a la izquierda
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card( // Agrupa las estadísticas en una tarjeta.
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Itera sobre las estadísticas base y muestra una fila para cada una.
                    pokemon.stats.forEachIndexed { index, statSlot ->
                        PokemonStatRow(
                            statName = statSlot.stat.name,
                            baseStat = statSlot.baseStat
                        )
                        // Añade un pequeño espaciador entre las filas de estadísticas.
                        if (index < pokemon.stats.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp)) // Espacio final
        }
    }
}

/**
 * Composable que renderiza un botón dinámico para "Añadir al equipo", "Reemplazar en equipo" o
 * mostrar "En tu equipo" (deshabilitado) según el estado actual.
 *
 * @param pokemon El Pokémon al que se refiere el botón.
 * @param isPokemonInTeam Indica si el Pokémon ya está en el equipo.
 * @param isTeamFull Indica si el equipo está lleno.
 * @param onAddToTeamClick Callback que se invoca cuando se hace clic en el botón (si está habilitado).
 *                         Esta acción puede ser añadir o iniciar el proceso de reemplazo.
 * @param modifier Modificador de Compose.
 */
@Composable
fun AddOrReplaceTeamButton(
    pokemon: PokemonDetailDto,
    isPokemonInTeam: Boolean,
    isTeamFull: Boolean,
    onAddToTeamClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Variables para definir el texto, icono, estado de habilitación y colores del botón.
    val buttonText: String
    val buttonIcon: ImageVector
    val buttonEnabled: Boolean
    val colors: ButtonColors

    // Lógica condicional para configurar el botón.
    when {
        // Caso 1: El Pokémon ya está en el equipo.
        isPokemonInTeam -> {
            buttonText = "En tu equipo"
            buttonIcon = Icons.Filled.CheckCircle // Icono de "check"
            buttonEnabled = false // El botón está deshabilitado.
            colors = ButtonDefaults.buttonColors( // Colores para estado deshabilitado.
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        // Caso 2: El equipo está lleno Y el Pokémon no está en el equipo.
        isTeamFull && !isPokemonInTeam -> {
            buttonText = "Reemplazar en equipo"
            buttonIcon = Icons.Filled.SwapHoriz // Icono de "intercambiar"
            buttonEnabled = true // El botón está habilitado para iniciar el reemplazo.
            colors = ButtonDefaults.buttonColors( // Colores secundarios para esta acción.
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
        // Caso 3: (Por defecto) El Pokémon no está en el equipo y hay espacio.
        else -> {
            buttonText = "Añadir al equipo"
            buttonIcon = Icons.Filled.Add // Icono de "añadir"
            buttonEnabled = true // El botón está habilitado para añadir.
            colors = ButtonDefaults.buttonColors( // Colores primarios por defecto.
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // Renderiza el botón de Material 3.
    Button(
        onClick = {
            // Ejecuta la acción solo si el botón está habilitado.
            if (buttonEnabled) {
                onAddToTeamClick()
            }
        },
        enabled = buttonEnabled,
        modifier = modifier.height(48.dp), // Altura estándar.
        shape = MaterialTheme.shapes.medium, // Forma del tema.
        colors = colors // Aplica los colores dinámicos.
    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = null, // El texto del botón es suficiente descripción.
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing)) // Espacio entre icono y texto.
        Text(
            buttonText,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

/**
 * Composable que muestra un diálogo de alerta cuando el usuario intenta añadir un Pokémon
 * a un equipo que ya está lleno. Permite al usuario seleccionar un miembro del equipo
 * actual para reemplazarlo por el nuevo Pokémon.
 *
 * @param pokemonToAdd El Pokémon que el usuario desea añadir al equipo.
 * @param currentTeam La lista actual de miembros del equipo (puede contener nulos para slots vacíos).
 * @param onConfirmReplace Callback que se invoca cuando el usuario confirma el reemplazo,
 *                         pasando el índice del slot del Pokémon a reemplazar.
 * @param onDismiss Callback que se invoca cuando el usuario descarta el diálogo.
 */
@Composable
fun ReplacePokemonDialog(
    pokemonToAdd: PokemonDetailDto,
    currentTeam: List<PokemonDetailDto?>,
    onConfirmReplace: (slotIndexToReplace: Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Estado para recordar el índice del slot seleccionado por el usuario para el reemplazo.
    var selectedSlotToReplace by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss, // Se llama cuando se intenta cerrar el diálogo (ej. tocando fuera).
        shape = MaterialTheme.shapes.large, // Bordes más redondeados.
        icon = { // Icono en la parte superior del diálogo.
            Icon(
                Icons.Filled.SwapHoriz,
                contentDescription = "Reemplazar Pokémon",
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = { Text("Equipo Lleno", style = MaterialTheme.typography.headlineSmall) },
        text = { // Contenido principal del diálogo.
            Column {
                Text(
                    "Tu equipo Pokémon está lleno. ¿Deseas reemplazar a un miembro actual por ${pokemonToAdd.name.replaceFirstChar { it.titlecase() }}?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Selecciona el Pokémon a reemplazar:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                // LazyColumn para mostrar la lista de miembros del equipo seleccionables.
                // Esto es útil si la lista pudiera ser larga, aunque aquí son 6 como máximo.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // Altura máxima antes de que aparezca el scroll.
                ) {
                    itemsIndexed(currentTeam) { index, member ->
                        // Solo muestra miembros del equipo que no son nulos.
                        if (member != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { selectedSlotToReplace = index } // Permite seleccionar el slot.
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton( // Botón de radio para la selección.
                                    selected = selectedSlotToReplace == index,
                                    onClick = { selectedSlotToReplace = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    member.name.replaceFirstChar { it.titlecase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            // Divisor opcional entre ítems (excepto el último).
                            val lastNonNullIndex = currentTeam.indexOfLast { it != null }
                            if (index < lastNonNullIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 56.dp), // Alineado después del RadioButton.
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                // Mensaje de error interno (debería ser raro que aparezca).
                if (currentTeam.count { it != null } == 0 && currentTeam.size >= 6) {
                    Text(
                        "Error interno: El equipo se considera lleno pero no hay miembros.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = { // Botón para confirmar la acción.
            Button(
                onClick = {
                    // Llama a onConfirmReplace solo si se ha seleccionado un slot.
                    selectedSlotToReplace?.let { slotIndex -> onConfirmReplace(slotIndex) }
                },
                enabled = selectedSlotToReplace != null, // Habilitado solo si hay una selección.
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reemplazar")
            }
        },
        dismissButton = { // Botón para cancelar/cerrar el diálogo.
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text("Cancelar")
            }
        }
    )
}


/**
 * Composable de utilidad que muestra un "chip" o "etiqueta" para un tipo de Pokémon.
 * Incluye un color de fondo distintivo basado en el tipo y el nombre del tipo traducido.
 *
 * @param typeName El nombre del tipo de Pokémon (ej. "grass", "fire").
 */
@Composable
fun PokemonTypeChip(typeName: String) {
    // Traduce el nombre del tipo (si hay traducciones disponibles).
    val translatedTypeName = PokemonTranslationUtil.translateTypeName(typeName)
    // Obtiene el color de fondo basado en el nombre del tipo.
    val backgroundColor = PokemonTypeColors.getColor(typeName)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Forma de píldora.
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding interno.
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = translatedTypeName.uppercase(), // Muestra el nombre en mayúsculas.
            color = Color.White, // Asume que el texto blanco contrasta bien con los colores de tipo.
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/**
 * Composable de utilidad para mostrar una pieza de información simple con una etiqueta y un valor.
 * Usado aquí para mostrar la Altura y el Peso del Pokémon.
 *
 * @param label La etiqueta descriptiva (ej. "Altura").
 * @param value El valor correspondiente (ej. "0.7 m").
 */
@Composable
fun PokemonStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Función de utilidad que devuelve un color basado en el valor de una estadística Pokémon.
 * Los colores ayudan a visualizar rápidamente si una estadística es baja, media o alta.
 *
 * @param statValue El valor numérico de la estadística.
 * @return Un [Color] para representar visualmente la magnitud de la estadística.
 */
fun getStatColor(statValue: Int): Color {
    // Define colores con transparencia para las barras de estadísticas.
    return when {
        statValue < 50 -> Color.Red.copy(alpha = 0.7f)         // Estadísticas bajas
        statValue < 80 -> Color.Yellow.copy(alpha = 0.7f)     // Estadísticas medias-bajas
        statValue < 110 -> Color.Green.copy(alpha = 0.7f)     // Estadísticas medias-altas
        else -> Color.Cyan.copy(alpha = 0.7f)                 // Estadísticas altas
        // Considerar usar colores de MaterialTheme.colorScheme para mayor consistencia.
    }
}

/**
 * Composable que muestra una fila para una estadística base específica de un Pokémon.
 * Incluye el nombre de la estadística (traducido), su valor numérico y una barra de progreso lineal.
 *
 * @param statName El nombre de la estadística (ej. "hp", "attack").
 * @param baseStat El valor base de la estadística.
 * @param maxStat El valor máximo posible para esta estadística, usado para calcular el progreso.
 *                Por defecto es 255, un valor común en los juegos Pokémon.
 */
@Composable
fun PokemonStatRow(
    statName: String,
    baseStat: Int,
    maxStat: Int = 255
) {
    // Traduce el nombre de la estadística.
    val translatedStatName = PokemonTranslationUtil.translateStatName(statName)
    // Calcula el porcentaje de la estadística para la barra de progreso, limitado entre 0 y 1.
    val statPercentage = (baseStat.toFloat() / maxStat.toFloat()).coerceIn(0f, 1f)

    Column(modifier = Modifier.padding(vertical = 4.dp)) { // Padding vertical para cada fila.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nombre de la estadística.
            Text(
                text = translatedStatName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.30f) // Asigna peso para distribución de espacio.
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Valor numérico de la estadística.
            Text(
                text = baseStat.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Barra de progreso lineal para la estadística.
            LinearProgressIndicator(
                progress = { statPercentage }, // El progreso se pasa como una lambda que devuelve el float
                modifier = Modifier
                    .weight(0.55f) // Asigna más peso a la barra.
                    .height(10.dp) // Altura de la barra.
                    .clip(RoundedCornerShape(50)), // Bordes redondeados.
                color = getStatColor(baseStat), // Color dinámico según el valor de la estadística.
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) // Color de fondo de la barra.
            )
        }
    }
}

// --- SECCIÓN DE PREVIEWS PARA JETPACK COMPOSE ---
// Estas funciones @Composable anotadas con @Preview permiten visualizar los componentes
// directamente en el editor de Android Studio sin necesidad de ejecutar la aplicación completa.

/**
 * Preview para el Composable [ActualPokemonDetailContentScrollable].
 * Muestra cómo se vería el contenido principal de la pantalla de detalles con datos de ejemplo.
 */
@Preview(showBackground = true, name = "Actual Detail Content Preview")
@Composable
fun ActualPokemonDetailContentPreview() {
    MyPokeCompanionTheme {
        // Creación de datos de ejemplo (mock) para el Pokémon.
        val sampleSprites = PokemonSpritesDto(frontDefault = "url_placeholder", otherSprites = null)
        val sampleType = PokemonTypeDto(name = "grass", url = "")
        val sampleTypeSlot = PokemonTypeSlotDto(slot = 1, type = sampleType)
        val sampleStatData =
            com.example.mypokecompanion.data.remote.dto.PokemonStatDto(name = "hp", url = "")
        val sampleStatSlot = com.example.mypokecompanion.data.remote.dto.PokemonStatSlotDto(
            baseStat = 45,
            effort = 0,
            stat = sampleStatData
        )
        val samplePokemon = PokemonDetailDto(
            id = 25, name = "Pikachu", sprites = sampleSprites,
            types = listOf(sampleTypeSlot.copy(type = PokemonTypeDto("electric", ""))),
            stats = listOf(sampleStatSlot), height = 4, weight = 60
        )

        // Llamada al Composable con los datos de ejemplo y lambdas vacías para las acciones.
        ActualPokemonDetailContentScrollable(
            pokemon = samplePokemon,
            isPokemonInTeam = false,
            isTeamFull = false,
            onAddToTeamClick = {},
            showReplaceDialog = false,
            currentTeamForDialog = emptyList(),
            onConfirmReplaceInDialog = {},
            onDismissReplaceDialog = {}
        )
    }
}

/**
 * Preview para el Composable [ReplacePokemonDialog].
 * Muestra cómo se vería el diálogo de reemplazo con datos de ejemplo.
 */
@Preview(showBackground = true, name = "Replace Dialog Preview")
@Composable
fun ReplaceDialogPreview() {
    MyPokeCompanionTheme {
        // Datos de ejemplo para el Pokémon a añadir y el equipo actual.
        val placeholderSprites = PokemonSpritesDto(
            frontDefault = "placeholder_url",
            otherSprites = null
        )
        val samplePokemonToAdd = PokemonDetailDto(
            id = 1, name = "Bulbasaur", sprites = placeholderSprites,
            types = emptyList(), stats = emptyList(), height = 0, weight = 0
        )
        val teamMember1 = PokemonDetailDto(
            id = 4, name = "Charmander", sprites = placeholderSprites,
            types = emptyList(), stats = emptyList(), height = 0, weight = 0
        )
        val teamMember2 = PokemonDetailDto(
            id = 7, name = "Squirtle", sprites = placeholderSprites,
            types = emptyList(), stats = emptyList(), height = 0, weight = 0
        )
        // Equipo de ejemplo con algunos miembros y algunos slots vacíos (null).
        val sampleTeam = listOf(teamMember1, teamMember2, null, null, null, null)

        ReplacePokemonDialog(
            pokemonToAdd = samplePokemonToAdd,
            currentTeam = sampleTeam,
            onConfirmReplace = {}, // Lambda vacía para la preview
            onDismiss = {}        // Lambda vacía para la preview
        )
    }
}

/**
 * Preview para el estado de carga de [PokemonDetailScreen].
 * Muestra un indicador de progreso circular.
 */
@Preview(showBackground = true, name = "Detail Screen Loading")
@Composable
fun PokemonDetailScreenPreviewLoading() {
    MyPokeCompanionTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Preview para el estado de error de [PokemonDetailScreen].
 * Muestra un mensaje de error.
 */
@Preview(showBackground = true, name = "Detail Screen Error")
@Composable
fun PokemonDetailScreenPreviewError() {
    MyPokeCompanionTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: No se pudo cargar", color = MaterialTheme.colorScheme.error)
        }
    }
}