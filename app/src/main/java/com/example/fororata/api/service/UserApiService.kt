package com.example.fororata.api.service

import com.example.fororata.api.dto.UserDTO
import retrofit2.http.*

interface UserApiService {
    @GET("users")
    suspend fun getUsers(): List<UserDTO>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDTO

    @POST("users")
    suspend fun createUser(@Body user: UserDTO): UserDTO

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: Unit): UserDTO

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)
}