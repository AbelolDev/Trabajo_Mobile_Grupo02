package com.example.fororata.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val aceptaTerminos: Boolean = false,
    val fotoUri: String? = null,
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)