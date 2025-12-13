package com.example.fororata.api.repository

import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.service.UserApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    // Obtener todos los usuarios
    suspend fun getUsers(): List<UserDTO> = withContext(Dispatchers.IO) {
        userApiService.getUsers()
    }

    // Obtener usuario por ID
    suspend fun getUserById(id: Long): UserDTO = withContext(Dispatchers.IO) {
        userApiService.getUserById(id)
    }

    // Crear usuario
    suspend fun createUser(user: UserDTO): UserDTO = withContext(Dispatchers.IO) {
        userApiService.createUser(user)
    }

    // Actualizar usuario
    suspend fun updateUser(id: Long, user: UserDTO): UserDTO = withContext(Dispatchers.IO) {
        userApiService.updateUser(id, user)
    }

    // Eliminar usuario
    suspend fun deleteUser(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            userApiService.deleteUser(id)
            true
        } catch (e: Exception) {
            // Log del error para debugging
            println("Error al eliminar usuario $id: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}