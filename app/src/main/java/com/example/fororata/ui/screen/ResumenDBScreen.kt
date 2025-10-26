package com.example.fororata.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fororata.viewmodel.UsuarioDBViewModel

@Composable
fun ResumenDBScreen(
    navController: NavController,
    usuarioDBViewModel: UsuarioDBViewModel
) {
    val usuarioState = usuarioDBViewModel.usuarioActual.collectAsState()
    val usuario = usuarioState.value

    if (usuario == null) {
        // No hay usuario logueado, regresar al inicio
        LaunchedEffect(Unit) {
            navController.navigate("registro") { popUpTo("inicio") { inclusive = false } }
        }
        return
    }

    // Ahora usuario no es null dentro de este bloque let
    usuario.let { u ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumen del Usuario",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nombre: ${u.nombre}", style = MaterialTheme.typography.bodyLarge)
            Text("Correo: ${u.correo}", style = MaterialTheme.typography.bodyLarge)
            Text("Contraseña: ${"*".repeat(u.clave.length)}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                usuarioDBViewModel.cerrarSesion()
                navController.navigate("inicio") { popUpTo("registro") { inclusive = true } }
            }) {
                Text("Cerrar sesión")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                navController.navigate("inicio")
            }) {
                Text("Volver a Home")
            }
        }
    }
}

