package com.example.fororata.ui.screen.publicaciones

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioDBViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEditarPublicacionScreen(
    navController: NavController,
    viewModel: PublicacionViewModel,
    usuarioDBViewModel: UsuarioDBViewModel,
    publicacionId: Int? = null // null = crear, con valor = editar
) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    val usuarioActual by usuarioDBViewModel.usuarioActual.collectAsState()

    val esEdicion = publicacionId != null
    val titulo = if (esEdicion) "Editar Publicación" else "Nueva Publicación"

    LaunchedEffect(publicacionId) {
        if (publicacionId != null) {
            viewModel.cargarPublicacionParaEditar(publicacionId)
        }
    }

    val contenido = estado.comentarios.firstOrNull()?.texto ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.limpiarFormulario()
                        navController.popBackStack()
                    }) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje informativo
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Las publicaciones no tienen calificación inicial. Los usuarios podrán calificar tu publicación mediante comentarios.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Campo título
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

            // Campo contenido
            OutlinedTextField(
                value = contenido,
                onValueChange = viewModel::onContenidoChange,
                label = { Text("Contenido") },
                placeholder = { Text("Escribe el contenido de tu publicación...") },
                isError = estado.errores.comentarios.firstOrNull()?.texto != null,
                supportingText = {
                    estado.errores.comentarios.firstOrNull()?.texto?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                maxLines = 15
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de acción
            Button(
                onClick = {
                    if (usuarioActual == null) {
                        Toast.makeText(
                            context,
                            "Debes iniciar sesión para publicar",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("iniciar-sesion")
                        return@Button
                    }

                    if (esEdicion) {
                        viewModel.actualizarPublicacion { exito, mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            if (exito) {
                                navController.popBackStack()
                            }
                        }
                    } else {
                        viewModel.crearPublicacion(usuarioActual!!.id) { exito, mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            if (exito) {
                                navController.navigate("publicaciones") {
                                    popUpTo("publicaciones") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = usuarioActual != null
            ) {
                Text(if (esEdicion) "Actualizar" else "Publicar")
            }

            if (usuarioActual == null) {
                Text(
                    "Debes iniciar sesión para crear publicaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}