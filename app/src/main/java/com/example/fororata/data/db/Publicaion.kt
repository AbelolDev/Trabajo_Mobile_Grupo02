package com.example.fororata.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publicaciones")
data class Publicacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val autorId: Int, // ID del usuario que creó la publicación
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaModificacion: Long = System.currentTimeMillis()
)

// Nueva entidad para comentarios con calificación
@Entity(tableName = "comentarios")
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val publicacionId: Int,
    val autorId: Int,
    val texto: String,
    val estrellas: Int, // Calificación de 1 a 5
    val fechaCreacion: Long = System.currentTimeMillis()
)