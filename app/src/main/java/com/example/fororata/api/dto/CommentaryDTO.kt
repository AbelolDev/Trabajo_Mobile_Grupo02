package com.example.fororata.api.dto

data class CommentaryDTO(
    val id: Long,
    val publicacion: PublicationDTO,
    val autor: UserDTO,
    val texto: String,
    val estrellas: Int,
    val fecha_creacion: Long
)
