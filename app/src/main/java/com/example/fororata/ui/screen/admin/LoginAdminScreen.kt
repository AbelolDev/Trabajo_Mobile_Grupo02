package com.example.fororata.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
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
import com.example.fororata.viewmodel.AdminViewModel
import com.example.fororata.viewmodel.LoginState
import com.example.fororata.components.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdminScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    // Estados locales para la UI
    var passwordVisible by remember { mutableStateOf(false) }

    // Recoger estados del ViewModel
    val loginFormState by adminViewModel.loginFormState
    val loginState by adminViewModel.loginState.collectAsState()
    val users by adminViewModel.users.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Cargar usuarios al iniciar
    LaunchedEffect(Unit) {
        adminViewModel.loadUsers()
    }

    // Manejar resultado del login
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                onLoginSuccess()
                navController.navigate("admin-dashboard") {
                    popUpTo("admin-login") { inclusive = true }
                }
            }
            else -> {
                // Otros estados se manejan en la UI
            }
        }
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
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                if (isLoading) {
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
                    value = loginFormState.email,
                    onValueChange = adminViewModel::onLoginEmailChange,
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = loginFormState.emailError != null
                )

                // Mostrar error de email
                loginFormState.emailError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contraseña
                OutlinedTextField(
                    value = loginFormState.password,
                    onValueChange = adminViewModel::onLoginPasswordChange,
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
                    singleLine = true,
                    isError = loginFormState.passwordError != null
                )

                // Mostrar error de contraseña
                loginFormState.passwordError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de login
                Button(
                    onClick = {
                        adminViewModel.loginAdmin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = loginFormState.isFormValid && loginState !is LoginState.Loading
                ) {
                    if (loginState is LoginState.Loading) {
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
                when (loginState) {
                    is LoginState.Error -> {
                        val errorMessage = (loginState as LoginState.Error).message
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    else -> {
                        // Otros estados no muestran mensaje
                    }
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

                // Espacio adicional al final para mejor scroll
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}