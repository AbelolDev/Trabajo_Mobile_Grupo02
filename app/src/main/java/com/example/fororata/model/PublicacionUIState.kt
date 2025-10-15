package com.example.fororata.model

data class PublicacionUIState(
    val id: Int = 0,
    val titulo: String = "",
    val estrellas: Int = 0,
    val comentarios: MutableList<ComentariosUIState> = mutableListOf(),
    val errores: PublicacionErrores = PublicacionErrores()
)

