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
import androidx.navigation.NavController
import com.example.fororata.api.dto.RolDTO
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.viewmodel.APIviewmodel.UserViewModel
import com.example.fororata.components.StatCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel(),
    userRole: String = "Administrador" // Parámetro para definir el rol
) {
    val context = LocalContext.current
    val users by userViewModel.users
    val isLoading by userViewModel.isLoading

    var selectedTab by remember { mutableStateOf(if (userRole == "Moderador") 0 else 0) }
    var showDeleteDialog by remember { mutableStateOf<UserDTO?>(null) }
    var showEditDialog by remember { mutableStateOf<UserDTO?>(null) }

    // Si es moderador, solo mostrar tab de publicaciones
    val isModerador = userRole == "Moderador"

    LaunchedEffect(Unit) {
        if (!isModerador) {
            userViewModel.loadUsers()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel de ${if (isModerador) "Moderación" else "Administración"}")
                        if (isModerador) {
                            Text(
                                "Gestión de Contenido",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (!isModerador) {
                        IconButton(onClick = { userViewModel.loadUsers() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isModerador)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si es admin y está en tab de usuarios
            AnimatedVisibility(
                visible = !isModerador && selectedTab == 0,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("admin-crear-admin") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                    text = { Text("Nuevo Usuario") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Estadísticas - Solo para admin
            if (!isModerador) {
                AnimatedStatsSection(users = users)
            } else {
                // Mensaje informativo para moderadores
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Modo Moderador",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Puedes gestionar el contenido de las publicaciones",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Tabs - Solo mostrar si es admin
            if (!isModerador) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Usuarios") },
                        icon = { Icon(Icons.Default.People, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Publicaciones") },
                        icon = { Icon(Icons.Default.Description, contentDescription = null) }
                    )
                }

                // Contenido según tab seleccionado
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(tween(300)) + slideInHorizontally() togetherWith
                                fadeOut(tween(300)) + slideOutHorizontally()
                    },
                    label = "tab_content"
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> UsersListSection(
                            users = users,
                            isLoading = isLoading,
                            onEditUser = { showEditDialog = it },
                            onDeleteUser = { showDeleteDialog = it }
                        )
                        1 -> PublicationsListSection(navController = navController)
                    }
                }
            } else {
                // Si es moderador, solo mostrar publicaciones
                PublicationsListSection(navController = navController)
            }
        }
    }

    // Diálogos solo para admin
    if (!isModerador) {
        // Diálogo de eliminar usuario
        showDeleteDialog?.let { user ->
            var isDeleting by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { if (!isDeleting) showDeleteDialog = null },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Eliminar Usuario") },
                text = { Text("¿Estás seguro de eliminar a ${user.nombre}? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            isDeleting = true
                            user.id?.let { userId ->
                                userViewModel.deleteUser(userId) { success, message ->
                                    isDeleting = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        showDeleteDialog = null
                                    }
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

        // Diálogo de editar usuario
        showEditDialog?.let { user ->
            EditUserDialog(
                user = user,
                userViewModel = userViewModel,
                onDismiss = { showEditDialog = null },
                onSuccess = {
                    Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    showEditDialog = null
                }
            )
        }
    }
}

@Composable
fun AnimatedStatsSection(users: List<UserDTO>) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + expandVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "Total Usuarios",
                value = users.size.toString(),
                icon = Icons.Default.People
            )
            StatCard(
                title = "Admins",
                value = users.count {
                    it.rol?.nombre_rol?.equals("Administrador", ignoreCase = true) == true
                }.toString(),
                icon = Icons.Default.AdminPanelSettings
            )
            StatCard(
                title = "Usuarios",
                value = users.count {
                    it.rol?.nombre_rol?.equals("Usuario", ignoreCase = true) == true
                }.toString(),
                icon = Icons.Default.Person
            )
        }
    }
}

@Composable
fun UsersListSection(
    users: List<UserDTO>,
    isLoading: Boolean,
    onEditUser: (UserDTO) -> Unit,
    onDeleteUser: (UserDTO) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay usuarios registrados")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserAdminCard(
                        user = user,
                        onEdit = { onEditUser(user) },
                        onDelete = { onDeleteUser(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserAdminCard(
    user: UserDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                    imageVector = if (user.rol?.nombre_rol == "Administrador")
                        Icons.Default.AdminPanelSettings
                    else
                        Icons.Default.Person,
                    contentDescription = null,
                    tint = if (user.rol?.nombre_rol == "Administrador")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = user.correo,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botones de acción
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

                    Text(
                        text = "Rol: ${user.rol?.nombre_rol ?: "Sin rol"}",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "ID: ${user.id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    user: UserDTO,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var correo by remember { mutableStateOf(user.correo) }
    var selectedRole by remember { mutableStateOf(user.rol?.nombre_rol ?: "Usuario") }
    var expanded by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isUpdating) onDismiss() },
        title = { Text("Editar Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdating
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdating
                )

                ExposedDropdownMenuBox(
                    expanded = expanded && !isUpdating,
                    onExpandedChange = { if (!isUpdating) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isUpdating
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Usuario") },
                            onClick = {
                                selectedRole = "Usuario"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Administrador") },
                            onClick = {
                                selectedRole = "Administrador"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Moderador") },
                            onClick = {
                                selectedRole = "Moderador"
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isUpdating = true
                    user.id?.let { userId ->
                        // Determinar ID del rol basado en el nombre
                        val rolId = when (selectedRole) {
                            "Administrador" -> 1L
                            "Moderador" -> 3L
                            else -> 2L // Usuario normal
                        }

                        val updatedUser = user.copy(
                            nombre = nombre,
                            correo = correo,
                            rol = user.rol?.copy(
                                id_rol = rolId,
                                nombre_rol = selectedRole
                            )
                        )

                        userViewModel.updateUser(userId, updatedUser) { success, message ->
                            isUpdating = false
                            if (success) {
                                onSuccess()
                            }
                        }
                    }
                },
                enabled = !isUpdating && nombre.isNotBlank() && correo.isNotBlank()
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
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

private fun UserDTO.copy(
    nombre: String,
    correo: String,
    rol: RolDTO?
) {
}

@Composable
fun PublicationsListSection(navController: NavController) {
    AdminPublicationsScreen()
}