package com.example.fororata.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.viewmodel.PerfilViewModel

@Composable
fun ResumenScreen(
    usuarioViewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel
) {
    val estado by usuarioViewModel.estado.collectAsState()
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resumen del Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        // Imagen del usuario
        AsyncImage(
            model = imagenPerfil,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(180.dp)
                .padding(top = 16.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nombre: ${estado.nombre}", style = MaterialTheme.typography.bodyLarge)
        Text("Correo: ${estado.correo}", style = MaterialTheme.typography.bodyLarge)
        Text("Contraseña: ${"*".repeat(estado.clave.length)}", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* Aquí podrías enviar los datos o cerrar sesión */ }) {
            Text("Finalizar registro")
        }
    }
}
