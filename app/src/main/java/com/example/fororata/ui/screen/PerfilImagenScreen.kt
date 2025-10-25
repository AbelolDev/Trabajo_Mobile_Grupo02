package com.example.fororata.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fororata.components.ImagenInteligente
import com.example.fororata.viewmodel.PerfilViewModel

/**
 * Pantalla principal del perfil de usuario
 * Permite seleccionar imagen desde galería o capturar con cámara
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilImagenScreen(
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observar el estado de la imagen del ViewModel
    val imagenPerfil by viewModel.imagenPerfil.collectAsStateWithLifecycle()

    // Estado para mostrar diálogo de permisos
    var mostrarDialogoPermisos by remember { mutableStateOf(false) }

    // Launcher para seleccionar imagen de la galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.actualizarImagenDesdeGaleria(uri)
    }

    // Launcher para capturar imagen con la cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.actualizarImagenDesdeCamara(success)
    }

    // Launcher para solicitar permisos de cámara
    val permisosCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.crearImagenTemporal(context)
            camaraLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Permiso de cámara denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher para solicitar permisos de galería
    val permisosGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galeriaLauncher.launch("image/*")
        } else {
            Toast.makeText(
                context,
                "Permiso de galería denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Componente de imagen circular
            ImagenInteligente(
                imageUri = imagenPerfil,
                size = 200.dp,
                borderWidth = 3.dp,
                borderColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Texto informativo
            Text(
                text = "Actualiza tu foto de perfil",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón para abrir galería
                ElevatedButton(
                    onClick = {
                        // Verificar permisos según la versión de Android
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                // Android 13+ usa READ_MEDIA_IMAGES
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    galeriaLauncher.launch("image/*")
                                } else {
                                    permisosGaleriaLauncher.launch(
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    )
                                }
                            }
                            else -> {
                                // Android 12 y anteriores
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    galeriaLauncher.launch("image/*")
                                } else {
                                    permisosGaleriaLauncher.launch(
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Galería",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galería")
                }

                // Botón para abrir cámara
                Button(
                    onClick = {
                        // Verificar permiso de cámara
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val uri = viewModel.crearImagenTemporal(context)
                            camaraLauncher.launch(uri)
                        } else {
                            permisosCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cámara",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cámara")
                }
            }

            // Botón opcional para limpiar imagen
            if (imagenPerfil != null) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { viewModel.limpiarImagenPerfil() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar foto")
                }
            }
        }
    }

    // Diálogo informativo sobre permisos (opcional)
    if (mostrarDialogoPermisos) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPermisos = false },
            title = { Text("Permisos requeridos") },
            text = {
                Text("Esta aplicación necesita permisos para acceder a tu cámara y galería para actualizar tu foto de perfil.")
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoPermisos = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}