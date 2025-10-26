package com.example.fororata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.data.db.Usuario
import com.example.fororata.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioDBViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application.applicationContext)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun registrar(nombre: String, correo: String, clave: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val nuevoUsuario = Usuario(nombre = nombre, correo = correo, clave = clave)
            val exito = repository.registrarUsuario(nuevoUsuario)
            onResultado(exito)
        }
    }

    fun iniciarSesion(correo: String, clave: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = repository.iniciarSesion(correo, clave)
            if (exito) {
                _usuarioActual.value = repository.obtenerUsuario(correo)
            }
            onResultado(exito)
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
