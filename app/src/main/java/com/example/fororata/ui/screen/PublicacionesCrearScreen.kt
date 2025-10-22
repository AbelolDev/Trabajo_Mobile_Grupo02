package com.example.fororata.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fororata.viewmodel.PublicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionesCrearScreen(
    navController: NavController,
    viewModel: PublicacionViewModel
) {
    val estado by viewModel.estado.collectAsState()
    var nuevoComentario by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Publicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("inicio") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🟩 Campo título
            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título de la publicación") },
                isError = estado.errores.titulo != null,
                supportingText = {
                    estado.errores.titulo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 🟩 Estrellas (puntuación general)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Puntuación: ${estado.estrellas}")
                Row {
                    Button(onClick = { viewModel.onEstrellasChange(estado.estrellas + 1) }) {
                        Text("+")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (estado.estrellas > 0)
                                viewModel.onEstrellasChange(estado.estrellas - 1)
                        }
                    ) {
                        Text("-")
                    }
                }
            }

            // 🟩 Lista de comentarios existentes
            Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(estado.comentarios) { index, comentario ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = comentario.texto.ifBlank { "(Sin texto)" })
                            Text(
                                text = "⭐ ${comentario.estrellasComentario}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            comentario.errores.texto?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // 🟩 Campo para agregar comentario nuevo
            OutlinedTextField(
                value = nuevoComentario,
                onValueChange = { nuevoComentario = it },
                label = { Text("Nuevo comentario") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (nuevoComentario.isNotBlank()) {
                        viewModel.agregarComentario(nuevoComentario)
                        nuevoComentario = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar comentario")
            }

            // 🟩 Botón de publicación final
            Button(
                onClick = {
                    if (viewModel.validarPublicacion()) {
                        // Acción si todo está correcto
                        navController.navigate("resumen")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar")
            }
        }
    }
}
