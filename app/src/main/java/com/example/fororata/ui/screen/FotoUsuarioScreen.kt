package com.example.fororata.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fororata.components.BotonComponente
import com.example.fororata.components.ImagenInteligente
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoUsuarioScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()

    // Estado local para almacenar URI temporal de cámara
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para la cámara
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            perfilViewModel.actualizarImagenDesdeCamara(true)
        } else {
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para la galería
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) perfilViewModel.actualizarImagenDesdeGaleria(uri)
    }

    // Launcher para permisos de cámara
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Seleccionar Foto de Perfil") }) }
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

            // Fila de botones: Cámara y Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonComponente(
                    text = "Usar cámara",
                    onClick = {
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission)
                            == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            val uri = perfilViewModel.crearImagenTemporal(context)
                            cameraUri = uri
                            launcherCamara.launch(uri)
                        } else {
                            requestPermissionLauncher.launch(permission)
                        }
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

            // Fila de botones: Continuar y Volver
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
