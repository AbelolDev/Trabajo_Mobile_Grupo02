package com.example.fororata.api.dto

data class UserDTO(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    val clave: String,
    val acepta_terminos: Int,
    val rol: RolDTO
)
