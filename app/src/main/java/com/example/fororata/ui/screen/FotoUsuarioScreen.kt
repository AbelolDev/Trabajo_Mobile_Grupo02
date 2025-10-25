package com.example.fororata.ui.screen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.components.ImagenInteligente
import com.example.fororata.components.BotonComponente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoUsuarioScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> perfilViewModel.actualizarImagenDesdeCamara(success) }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> perfilViewModel.actualizarImagenDesdeGaleria(uri) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Seleccionar Foto de Perfil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elige una imagen para tu perfil",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val imageSize by animateDpAsState(
                targetValue = if (imagenPerfil != null) 200.dp else 120.dp,
                animationSpec = tween(600)
            )

            ImagenInteligente(
                imageUri = imagenPerfil,
                size = imageSize,
                borderWidth = 3.dp,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.animateContentSize()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primera fila: Cámara y Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonComponente(
                    text = "Usar cámara",
                    onClick = {
                        val uri = perfilViewModel.crearImagenTemporal(context)
                        launcherCamara.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                )

                BotonComponente(
                    text = "Abrir galería",
                    onClick = { launcherGaleria.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Segunda fila: Continuar y Volver
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonComponente(
                    text = "Continuar",
                    onClick = { navController.navigate("resumen") },
                    modifier = Modifier.weight(1f),
                    enabled = imagenPerfil != null
                )

                BotonComponente(
                    text = "Volver",
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                )
            }

        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FotoUsuarioScreenPreview() {
    val context = LocalContext.current
    val perfilViewModel = PerfilViewModel(context.applicationContext as android.app.Application)
    val usuarioViewModel = UsuarioViewModel()

    FotoUsuarioScreen(
        navController = rememberNavController(),
        perfilViewModel = perfilViewModel,
        usuarioViewModel = usuarioViewModel
    )
}
