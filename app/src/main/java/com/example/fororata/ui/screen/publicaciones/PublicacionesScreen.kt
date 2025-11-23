package com.example.fororata.ui.screen.publicaciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fororata.components.BottomNavBar
import com.example.fororata.data.db.Publicacion
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioDBViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionesScreenContent(
    navController: NavController,
    viewModel: PublicacionViewModel,
    usuarioDBViewModel: UsuarioDBViewModel
) {
    val publicaciones by viewModel.publicaciones.collectAsState()
    val usuarioActual by usuarioDBViewModel.usuarioActual.collectAsState()
    var mostrarDialogoEliminar by remember { mutableStateOf<Publicacion?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Publicaciones", fontWeight = FontWeight.Bold) },
            )
        },
        floatingActionButton = {
            if (usuarioActual != null) {
                FloatingActionButton(
                    onClick = { navController.navigate("crear-publicacion") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
                }
            }
        }
    ) { paddingValues ->
        if (publicaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay publicaciones todavía",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (usuarioActual != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "¡Crea la primera!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Inicia sesión para crear publicaciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(publicaciones) { publicacion ->
                    PublicacionCard(
                        publicacion = publicacion,
                        esAutor = usuarioActual?.id == publicacion.autorId,
                        onClick = {
                            navController.navigate("detalle-publicacion/${publicacion.id}")
                        },
                        onEditar = {
                            viewModel.cargarPublicacionParaEditar(publicacion.id)
                            navController.navigate("editar-publicacion/${publicacion.id}")
                        },
                        onEliminar = { mostrarDialogoEliminar = publicacion }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    mostrarDialogoEliminar?.let { publicacion ->
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = null },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${publicacion.titulo}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarPublicacion(publicacion) { exito, mensaje ->
                            // Aquí podrías mostrar un Toast con el mensaje
                        }
                        mostrarDialogoEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PublicacionCard(
    publicacion: Publicacion,
    esAutor: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fecha = dateFormat.format(Date(publicacion.fechaCreacion))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y acciones (solo si es autor)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = publicacion.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (esAutor) {
                    Row {
                        IconButton(onClick = onEditar) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onEliminar) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido
            Text(
                text = publicacion.contenido,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer con info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toca para ver más",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = fecha,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PublicacionesScreen(
    navController: NavController,
    viewModel: PublicacionViewModel,
    usuarioDBViewModel: UsuarioDBViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PublicacionesScreenContent(
                navController = navController,
                viewModel = viewModel,
                usuarioDBViewModel = usuarioDBViewModel
            )
        }
    }
}