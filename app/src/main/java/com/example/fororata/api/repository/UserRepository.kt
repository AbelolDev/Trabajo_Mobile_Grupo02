package com.example.fororata.api.repository

import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.service.UserApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getUsers(): List<UserDTO> {
        return userApiService.getUsers()
    }

    suspend fun getUser(id: Long): UserDTO {
        return userApiService.getUserById(id)
    }

    suspend fun createUser(user: UserDTO): UserDTO {
        return userApiService.createUser(user)
    }

    suspend fun updateUser(id: Long, user: UserDTO): UserDTO {
        return userApiService.updateUser(id, user)
    }

    suspend fun deleteUser(id: Long) {
        userApiService.deleteUser(id)
    }
}