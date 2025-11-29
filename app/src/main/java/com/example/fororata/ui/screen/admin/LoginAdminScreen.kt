package com.example.fororata.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fororata.viewmodel.APIviewmodel.UserViewModel
import com.example.fororata.api.dto.UserDTO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdminScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Observar el estado del ViewModel
    val users by userViewModel.users
    val errorMessage by userViewModel.errorMessage
    val usersLoading by userViewModel.isLoading

    LaunchedEffect(Unit) {
        userViewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Admin Login",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de administrador
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Panel de Administración",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Acceso exclusivo para administradores",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Mostrar estado de carga de usuarios
            if (usersLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando usuarios...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${users.size} usuarios cargados",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Contraseña"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de login
            Button(
                onClick = {
                    isLoading = true
                    loginError = null

                    coroutineScope.launch {
                        try {
                            // Forzar recarga de usuarios por si acaso
                            userViewModel.loadUsers()

                            // Pequeña pausa para asegurar que los datos estén cargados
                            kotlinx.coroutines.delay(500)

                            // Verificar credenciales
                            val isAdmin = verifyAdminCredentials(email, password, users)

                            if (isAdmin) {
                                onLoginSuccess()
                                navController.navigate("admin-dashboard") {
                                    // Limpiar el stack de navegación
                                    popUpTo("admin-login") { inclusive = true }
                                }
                            } else {
                                loginError = "Credenciales incorrectas o no tienes permisos de administrador"
                            }
                        } catch (e: Exception) {
                            loginError = "Error al verificar credenciales: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading && !usersLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar mensajes de error del login
            loginError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Mostrar mensajes de error de la carga de usuarios
            errorMessage?.let { message ->
                Text(
                    text = "Error cargando usuarios: $message",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DEBUG: Mostrar información de usuarios cargados (solo para desarrollo)
            if (users.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "DEBUG - Usuarios cargados:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        users.take(3).forEach { user ->
                            Text(
                                text = "• ${user.correo} - Rol: ${user.rol?.nombre_rol ?: "Sin rol"}",
                                fontSize = 10.sp
                            )
                        }
                        if (users.size > 3) {
                            Text(
                                text = "... y ${users.size - 3} más",
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Información para administradores
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información para administradores:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Solo usuarios con rol de administrador pueden acceder\n• Contacta con el superadministrador si necesitas acceso",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Función para verificar credenciales de administrador
private fun verifyAdminCredentials(
    email: String,
    password: String,
    users: List<UserDTO>
): Boolean {
    println("Verificando credenciales para: $email")
    println("Total usuarios cargados: ${users.size}")

    users.forEach { user ->
        println("Usuario: ${user.correo}, Rol: ${user.rol?.nombre_rol}")
    }

    return users.any { user ->
        val emailMatch = user.correo.equals(email, ignoreCase = true)
        val passwordMatch = user.clave == password
        // CORREGIDO: Buscar "Administrador" en lugar de "admin"
        val isAdmin = user.rol?.nombre_rol?.equals("Administrador", ignoreCase = true) == true

        val result = emailMatch && passwordMatch && isAdmin
        if (result) {
            println("✅ Usuario admin encontrado: ${user.correo}")
            println("✅ Rol: ${user.rol?.nombre_rol}")
            println("✅ Credenciales correctas")
        }
        result
    }
}