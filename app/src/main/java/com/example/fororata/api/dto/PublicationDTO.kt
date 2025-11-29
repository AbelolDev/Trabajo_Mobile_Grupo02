package com.example.fororata.api.dto

data class PublicationDTO(
    val id: Long,
    val titulo: String,
    val autor: UserDTO,
    val fecha_creacion: Long,
    val fecha_modificacion: Long?
)
