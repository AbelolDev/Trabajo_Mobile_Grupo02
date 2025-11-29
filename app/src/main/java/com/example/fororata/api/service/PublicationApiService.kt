package com.example.fororata.api.service

import com.example.fororata.api.dto.PublicationDTO
import retrofit2.http.*

interface PublicationApiService {

    @GET("publicaciones")
    suspend fun getPublications(): List<PublicationDTO>

    @GET("publicaciones/{id}")
    suspend fun getPublication(@Path("id") id: Long): PublicationDTO

    @POST("publicaciones")
    suspend fun createPublication(@Body request: CreatePublicationRequest): PublicationDTO

    @PUT("publicaciones/{id}")
    suspend fun updatePublication(
        @Path("id") id: Long,
        @Body request: CreatePublicationRequest
    ): PublicationDTO

    @DELETE("publicaciones/{id}")
    suspend fun deletePublication(@Path("id") id: Long)
}

data class CreatePublicationRequest(
    val titulo: String,
    val contenido: String,
    val autorId: Long
)