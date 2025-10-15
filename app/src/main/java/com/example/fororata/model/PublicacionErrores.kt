package com.example.fororata.model

data class PublicacionErrores (
    val id: Int? = null,
    val titulo: String? = null,
    val estrellas: Int? = null,
    val comentarios: MutableList<ComentariosErrores> = mutableListOf()
)