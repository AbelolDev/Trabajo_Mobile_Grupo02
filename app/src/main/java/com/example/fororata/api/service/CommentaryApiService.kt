package com.example.fororata.api.service

import com.example.fororata.api.dto.CommentaryDTO
import retrofit2.http.*

interface CommentaryApiService {

    @GET("publicaciones/{publicacionId}/comentarios")
    suspend fun getCommentaries(@Path("publicacionId") publicacionId: Long): List<CommentaryDTO>

    @POST("comentarios")
    suspend fun createCommentary(@Body request: CreateCommentaryRequest): CommentaryDTO

    @PUT("comentarios/{id}")
    suspend fun updateCommentary(
        @Path("id") id: Long,
        @Body request: CreateCommentaryRequest
    ): CommentaryDTO

    @DELETE("comentarios/{id}")
    suspend fun deleteCommentary(@Path("id") id: Long)
}

data class CreateCommentaryRequest(
    val publicacionId: Long,
    val autorId: Long,
    val texto: String,
    val estrellas: Int? = null
)