package com.example.fororata.api.service

import com.example.fororata.api.dto.UserDTO
import retrofit2.http.*

interface UserApiService {
    @GET("usuarios")
    suspend fun getUsers(): List<UserDTO>

    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDTO

    @POST("usuarios")
    suspend fun createUser(@Body user: UserDTO): UserDTO

    @PUT("usuarios/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserDTO): UserDTO

    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Long)
}