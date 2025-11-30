package com.example.fororata.ui.screen.admin

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fororata.api.dto.PublicationDTO
import com.example.fororata.viewmodel.APIviewmodel.PublicationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPublicationsScreen(
    publicationViewModel: PublicationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val publications by publicationViewModel.publications
    val isLoading by publicationViewModel.isLoading

    var showDeleteDialog by remember { mutableStateOf<PublicationDTO?>(null) }
    var showEditDialog by remember { mutableStateOf<PublicationDTO?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        publicationViewModel.loadPublications()
    }

    val filteredPublications = remember(publications, searchQuery) {
        if (searchQuery.isBlank()) {
            publications
        } else {
            publications.filter {
                it.titulo.contains(searchQuery, ignoreCase = true) ||
                        it.autor.nombre.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de búsqueda y botón crear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar publicaciones...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear publicación")
            }
        }

        // Lista de publicaciones
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredPublications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (searchQuery.isNotEmpty())
                            "No se encontraron publicaciones"
                        else
                            "No hay publicaciones"
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPublications) { publication ->
                    PublicationAdminCard(
                        publication = publication,
                        onEdit = { showEditDialog = publication },
                        onDelete = { showDeleteDialog = publication }
                    )
                }
            }
        }
    }

    // Diálogo de eliminar
    showDeleteDialog?.let { publication ->
        var isDeleting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = null },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar Publicación") },
            text = {
                Text("¿Estás seguro de eliminar \"${publication.titulo}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        publicationViewModel.deletePublication(publication.id) { success, message ->
                            isDeleting = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                showDeleteDialog = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null },
                    enabled = !isDeleting
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de editar
    showEditDialog?.let { publication ->
        EditPublicationDialog(
            publication = publication,
            publicationViewModel = publicationViewModel,
            onDismiss = { showEditDialog = null },
            onSuccess = {
                Toast.makeText(context, "Publicación actualizada", Toast.LENGTH_SHORT).show()
                showEditDialog = null
            }
        )
    }

    // Diálogo de crear
    if (showCreateDialog) {
        CreatePublicationDialog(
            publicationViewModel = publicationViewModel,
            onDismiss = { showCreateDialog = false },
            onSuccess = {
                Toast.makeText(context, "Publicación creada", Toast.LENGTH_SHORT).show()
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PublicationAdminCard(
    publication: PublicationDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = publication.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 2
                    )
                    Text(
                        text = "Por: ${publication.autor.nombre}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir"
                    )
                }
            }

            // Contenido expandible
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Contenido de la publicación
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Contenido:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = publication.contenido ?: "Sin contenido",
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Información del autor
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Información del autor:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Nombre: ${publication.autor.nombre}",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Correo: ${publication.autor.correo}",
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = "ID: ${publication.autor.id}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fechas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Creado:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dateFormat.format(Date(publication.fecha_creacion)),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        publication.fecha_modificacion?.let { fechaMod ->
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Modificado:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dateFormat.format(Date(fechaMod)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar")
                        }

                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditPublicationDialog(
    publication: PublicationDTO,
    publicationViewModel: PublicationViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var titulo by remember { mutableStateOf(publication.titulo) }
    var contenido by remember { mutableStateOf(publication.contenido ?: "") }
    var isUpdating by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isUpdating) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Publicación")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    leadingIcon = {
                        Icon(Icons.Default.Title, contentDescription = null)
                    },
                    enabled = !isUpdating
                )

                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
                    leadingIcon = {
                        Icon(Icons.Default.Article, contentDescription = null)
                    },
                    enabled = !isUpdating
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Información del autor:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Nombre: ${publication.autor.nombre}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Correo: ${publication.autor.correo}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "ID Publicación: ${publication.id}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isUpdating = true
                    publication.autor.id?.let { autorId ->
                        publicationViewModel.updatePublication(
                            id = publication.id,
                            titulo = titulo,
                            contenido = contenido,
                            autorId = autorId
                        ) { success, message ->
                            isUpdating = false
                            if (success) {
                                onSuccess()
                            }
                        }
                    }
                },
                enabled = !isUpdating && titulo.isNotBlank() && contenido.isNotBlank()
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CreatePublicationDialog(
    publicationViewModel: PublicationViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var autorId by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva Publicación")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    placeholder = { Text("Escribe el título...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    leadingIcon = {
                        Icon(Icons.Default.Title, contentDescription = null)
                    },
                    enabled = !isCreating
                )

                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    placeholder = { Text("Escribe el contenido...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
                    leadingIcon = {
                        Icon(Icons.Default.Article, contentDescription = null)
                    },
                    enabled = !isCreating
                )

                OutlinedTextField(
                    value = autorId,
                    onValueChange = { autorId = it },
                    label = { Text("ID del Autor") },
                    placeholder = { Text("Ej: 1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    enabled = !isCreating
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nota: Debes ingresar el ID de un usuario existente",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isCreating = true
                    autorId.toLongOrNull()?.let { id ->
                        publicationViewModel.createPublication(
                            titulo = titulo,
                            contenido = contenido,
                            autorId = id
                        ) { success, message ->
                            isCreating = false
                            if (success) {
                                onSuccess()
                            }
                        }
                    }
                },
                enabled = !isCreating && titulo.isNotBlank() && contenido.isNotBlank() && autorId.toLongOrNull() != null
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCreating
            ) {
                Text("Cancelar")
            }
        }
    )
}