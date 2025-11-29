package com.example.fororata.api.repository

import com.example.fororata.api.dto.CommentaryDTO
import com.example.fororata.api.service.CommentaryApiService
import com.example.fororata.api.service.CreateCommentaryRequest
import javax.inject.Inject

class CommentaryRepository @Inject constructor(
    private val commentaryApiService: CommentaryApiService
) {
    suspend fun getCommentaries(publicacionId: Long): List<CommentaryDTO> {
        return commentaryApiService.getCommentaries(publicacionId)
    }

    suspend fun createCommentary(publicacionId: Long, autorId: Long, texto: String, estrellas: Int? = null): CommentaryDTO {
        val request = CreateCommentaryRequest(publicacionId, autorId, texto, estrellas)
        return commentaryApiService.createCommentary(request)
    }

    suspend fun updateCommentary(id: Long, publicacionId: Long, autorId: Long, texto: String, estrellas: Int? = null): CommentaryDTO {
        val request = CreateCommentaryRequest(publicacionId, autorId, texto, estrellas)
        return commentaryApiService.updateCommentary(id, request)
    }

    suspend fun deleteCommentary(id: Long) {
        commentaryApiService.deleteCommentary(id)
    }
}