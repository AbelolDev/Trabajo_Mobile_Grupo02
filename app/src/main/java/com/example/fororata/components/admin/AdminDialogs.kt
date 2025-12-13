package com.example.fororata.components.admin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fororata.api.dto.RolDTO
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.viewmodel.APIviewmodel.UserViewModel

@Composable
fun DeleteUserDialog(
    user: UserDTO?,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    context: Context
) {
    user?.let { userToDelete ->
        var isDeleting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isDeleting) onDismiss() },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de eliminar a ${userToDelete.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        userToDelete.id?.let { userId ->
                            userViewModel.deleteUser(userId) { success, message ->
                                isDeleting = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onDismiss()
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
                    onClick = onDismiss,
                    enabled = !isDeleting
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    user: UserDTO?,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    context: Context
) {
    user?.let { userToEdit ->
        var nombre by remember { mutableStateOf(userToEdit.nombre) }
        var correo by remember { mutableStateOf(userToEdit.correo) }
        var selectedRole by remember { mutableStateOf(userToEdit.rol?.nombre_rol ?: "Usuario") }
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
                        userToEdit.id?.let { userId ->
                            val rolId = when (selectedRole) {
                                "Administrador" -> 1L
                                "Moderador" -> 3L
                                else -> 2L
                            }

                            val updatedUser = userToEdit.copy(
                                nombre = nombre,
                                correo = correo,
                                rol = userToEdit.rol?.copy(
                                    id_rol = rolId,
                                    nombre_rol = selectedRole
                                ) ?: RolDTO(rolId, selectedRole, "")
                            )

                            userViewModel.updateUser(userId, updatedUser) { success, message ->
                                isUpdating = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onDismiss()
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
}

@Composable
fun PublicationsListSection(navController: androidx.navigation.NavController) {
    // Implementación de la pantalla de publicaciones
    // AdminPublicationsScreen()
}