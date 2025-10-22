package com.example.fororata.model

data class UsuarioUiState(
    val nombre: String = "",              // Nombre del usuario
    val correo: String = "",              // Correo electrónico
    val clave: String = "",               // Clave del usuario
    val aceptaTerminos: Boolean = false,  // Confirmación de términos
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)