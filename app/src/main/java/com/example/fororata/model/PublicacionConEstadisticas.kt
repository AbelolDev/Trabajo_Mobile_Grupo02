package com.example.fororata.model

import com.example.fororata.data.db.Publicacion

data class PublicacionConEstadisticas(
    val publicacion: Publicacion,
    val promedioEstrellas: Double = 0.0,
    val cantidadComentarios: Int = 0,
    val nombreAutor: String = "Usuario desconocido"
)