package com.example.fororata.model

data class ComentariosUIState(
    val id: Int = 0,
    val texto: String = "",
    val estrellasComentario: Int = 0,
    val errores: ComentariosErrores = ComentariosErrores()
)