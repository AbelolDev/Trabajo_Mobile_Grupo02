package com.example.fororata.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fororata.R

/**
 * Componente reutilizable para mostrar una imagen de perfil circular.
 * Si no hay imagen, muestra una imagen por defecto.
 *
 * @param imageUri URI de la imagen a mostrar (puede ser null)
 * @param size Tamaño del círculo de la imagen
 * @param modifier Modificador adicional
 * @param borderWidth Ancho del borde
 * @param borderColor Color del borde
 * @param backgroundColor Color de fondo cuando no hay imagen
 */
@Composable
fun ImagenInteligente(
    imageUri: Uri?,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                // Mostrar imagen seleccionada usando Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Mostrar imagen por defecto (por ejemplo, avatar.png en res/drawable)
                Image(
                    painter = painterResource(id = R.drawable.avatar_default),
                    contentDescription = "Imagen por defecto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagenInteligentePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview sin imagen (usa imagen por defecto)
            ImagenInteligente(
                imageUri = null,
                size = 100.dp
            )

            // Preview con otro tamaño
            ImagenInteligente(
                imageUri = null,
                size = 150.dp,
                borderColor = Color.Blue,
                borderWidth = 3.dp
            )
        }
    }
}
