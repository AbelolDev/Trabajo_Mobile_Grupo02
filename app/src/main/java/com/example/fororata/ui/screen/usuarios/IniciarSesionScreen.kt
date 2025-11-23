package com.example.fororata.ui.screen.usuarios

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fororata.components.BottomNavBar
import com.example.fororata.viewmodel.UsuarioDBViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IniciarSesionScreenContent(
    navController: NavController,
    usuarioDBViewModel: UsuarioDBViewModel = viewModel()
) {
    val context = LocalContext.current
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val usuarioState = usuarioDBViewModel.usuarioActual.collectAsState()
    val usuario = usuarioState.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inicio de Sesión") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a ForoRata 🐀",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (correo.isNotBlank() && clave.isNotBlank()) {
                        isLoading = true
                        usuarioDBViewModel.iniciarSesion(correo, clave) { exito ->
                            isLoading = false
                            if (exito) {
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("resumenDB") {
                                    popUpTo("inicio") { inclusive = false }
                                    launchSingleTop = true
                                }
                            } else {
                                Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.navigate("registro") }
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }

            usuario?.let { u ->
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sesión activa: ${u.nombre}", style = MaterialTheme.typography.bodyMedium)
                TextButton(
                    onClick = {
                        usuarioDBViewModel.cerrarSesion()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
fun IniciarSesionScreen(
    navController: NavController,
    usuarioDBViewModel: UsuarioDBViewModel = viewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            IniciarSesionScreenContent(
                navController = navController,
                usuarioDBViewModel = usuarioDBViewModel
            )
        }
    }
}
