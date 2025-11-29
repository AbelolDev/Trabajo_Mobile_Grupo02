package com.example.fororata.api.repository

import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.service.UserApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getUsers(): List<UserDTO> = withContext(Dispatchers.IO) {
        userApiService.getUsers()
    }

    // Añade otros métodos que necesites
    suspend fun getUserById(id: Long): UserDTO = withContext(Dispatchers.IO) {
        userApiService.getUserById(id)
    }

    suspend fun createUser(user: UserDTO): UserDTO = withContext(Dispatchers.IO) {
        userApiService.createUser(user)
    }
}