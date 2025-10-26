package com.example.fororata.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val _imagenPerfil = MutableStateFlow<Uri?>(null)
    val imagenPerfil: StateFlow<Uri?> = _imagenPerfil.asStateFlow()

    private var _tempCameraUri: Uri? = null
    val tempCameraUri: Uri?
        get() = _tempCameraUri

    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        uri?.let { _imagenPerfil.value = it }
    }

    fun actualizarImagenDesdeCamara(success: Boolean) {
        if (success) {
            _tempCameraUri?.let { _imagenPerfil.value = it }
        }
    }

    fun crearImagenTemporal(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        _tempCameraUri = uri
        return uri
    }

    fun limpiarImagenPerfil() {
        _imagenPerfil.value = null
        _tempCameraUri = null
    }
}
