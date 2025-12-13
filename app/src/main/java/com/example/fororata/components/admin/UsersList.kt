package com.example.fororata.components.admin

import androidx.compose.animation.*
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
import com.example.fororata.api.dto.UserDTO

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
        when {
            isLoading -> LoadingState()
            users.isEmpty() -> EmptyState()
            else -> UsersList(users, onEditUser, onDeleteUser)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.PersonOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay usuarios registrados")
        }
    }
}

@Composable
private fun UsersList(
    users: List<UserDTO>,
    onEditUser: (UserDTO) -> Unit,
    onDeleteUser: (UserDTO) -> Unit
) {
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
            UserHeader(user, expanded) { expanded = !expanded }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                UserDetails(user, onEdit, onDelete)
            }
        }
    }
}

@Composable
private fun UserHeader(
    user: UserDTO,
    expanded: Boolean,
    onExpandClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (user.rol?.nombre_rol == "Administrador")
                Icons.Filled.AdminPanelSettings
            else
                Icons.Filled.Person,
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

        IconButton(onClick = onExpandClick) {
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "Expandir"
            )
        }
    }
}

@Composable
private fun UserDetails(
    user: UserDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                Icon(Icons.Filled.Edit, contentDescription = null)
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
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Eliminar")
            }
        }
    }
}