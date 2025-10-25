package com.example.fororata.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel para manejar el estado del perfil del usuario
 * Gestiona la imagen de perfil usando StateFlow para reactividad
 */
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    // Estado privado mutable para la URI de la imagen
    private val _imagenPerfil = MutableStateFlow<Uri?>(null)

    // Estado público inmutable expuesto a la UI
    val imagenPerfil: StateFlow<Uri?> = _imagenPerfil.asStateFlow()

    // URI temporal para la cámara
    private var _tempCameraUri: Uri? = null
    val tempCameraUri: Uri?
        get() = _tempCameraUri

    /**
     * Actualiza la imagen de perfil desde la galería
     * @param uri URI de la imagen seleccionada de la galería
     */
    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        uri?.let {
            _imagenPerfil.value = it
        }
    }

    /**
     * Actualiza la imagen de perfil desde la cámara
     * @param success Indica si la captura fue exitosa
     */
    fun actualizarImagenDesdeCamara(success: Boolean) {
        if (success) {
            _tempCameraUri?.let {
                _imagenPerfil.value = it
            }
        }
    }

    /**
     * Crea una URI temporal para guardar la foto de la cámara
     * @param context Contexto de la aplicación
     * @return URI del archivo temporal
     */
    fun crearImagenTemporal(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(context.cacheDir, "images").apply {
            if (!exists()) mkdirs()
        }

        val imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        _tempCameraUri = uri
        return uri
    }

    /**
     * Limpia la imagen de perfil
     */
    fun limpiarImagenPerfil() {
        _imagenPerfil.value = null
        _tempCameraUri = null
    }
}