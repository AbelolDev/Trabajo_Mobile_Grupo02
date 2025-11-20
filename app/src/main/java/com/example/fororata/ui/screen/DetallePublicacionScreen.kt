package com.example.fororata.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fororata.data.db.Comentario
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioDBViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePublicacionScreen(
    navController: NavController,
    viewModel: PublicacionViewModel,
    usuarioDBViewModel: UsuarioDBViewModel,
    publicacionId: Int
) {
    val context = LocalContext.current
    val publicacion by viewModel.publicacionSeleccionada.collectAsState()
    val comentarios by viewModel.comentarios.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val usuarioActual by usuarioDBViewModel.usuarioActual.collectAsState()

    var textoComentario by remember { mutableStateOf("") }
    var estrellasSeleccionadas by remember { mutableStateOf(0) }
    var mostrarFormularioComentario by remember { mutableStateOf(false) }

    LaunchedEffect(publicacionId) {
        viewModel.cargarPublicacionConDetalles(publicacionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Publicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Publicación principal
            item {
                publicacion?.let { pub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = pub.titulo,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Por Usuario ${pub.autorId}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = pub.contenido,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Estadísticas
                            estadisticas?.let { stats ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "Calificación promedio",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (stats.promedioEstrellas > 0)
                                                    String.format("%.1f", stats.promedioEstrellas)
                                                else "Sin calificar",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "${stats.cantidadComentarios} comentarios",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón para agregar comentario
            item {
                if (usuarioActual != null && publicacion?.autorId != usuarioActual?.id) {
                    Button(
                        onClick = { mostrarFormularioComentario = !mostrarFormularioComentario },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (mostrarFormularioComentario) "Cancelar" else "Agregar Comentario")
                    }
                } else if (publicacion?.autorId == usuarioActual?.id) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "No puedes comentar tu propia publicación",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Inicia sesión para comentar y calificar",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Formulario de comentario
            if (mostrarFormularioComentario) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tu calificación:", fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                (1..5).forEach { index ->
                                    IconButton(onClick = { estrellasSeleccionadas = index }) {
                                        Icon(
                                            imageVector = if (index <= estrellasSeleccionadas)
                                                Icons.Filled.Star
                                            else
                                                Icons.Outlined.StarOutline,
                                            contentDescription = "$index estrellas",
                                            tint = if (index <= estrellasSeleccionadas)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = textoComentario,
                                onValueChange = { textoComentario = it },
                                label = { Text("Tu comentario") },
                                placeholder = { Text("Escribe tu opinión...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (usuarioActual != null) {
                                        viewModel.agregarComentarioConCalificacion(
                                            publicacionId = publicacionId,
                                            autorId = usuarioActual!!.id,
                                            texto = textoComentario,
                                            estrellas = estrellasSeleccionadas
                                        ) { exito, mensaje ->
                                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                            if (exito) {
                                                textoComentario = ""
                                                estrellasSeleccionadas = 0
                                                mostrarFormularioComentario = false
                                            }
                                        }
                                    }
                                },
                                enabled = textoComentario.isNotBlank() && estrellasSeleccionadas > 0,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publicar Comentario")
                            }
                        }
                    }
                }
            }

            // Lista de comentarios
            item {
                Text(
                    "Comentarios (${comentarios.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (comentarios.isEmpty()) {
                item {
                    Card {
                        Text(
                            "Aún no hay comentarios. ¡Sé el primero en comentar!",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(comentarios) { comentario ->
                    ComentarioCard(comentario)
                }
            }
        }
    }
}

@Composable
fun ComentarioCard(comentario: Comentario) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fecha = dateFormat.format(Date(comentario.fechaCreacion))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Usuario ${comentario.autorId}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(comentario.estrellas) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                comentario.texto,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                fecha,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}