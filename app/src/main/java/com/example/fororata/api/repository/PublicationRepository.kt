package com.example.fororata.api.repository

import com.example.fororata.api.dto.PublicationDTO
import com.example.fororata.api.service.CreatePublicationRequest
import com.example.fororata.api.service.PublicationApiService
import javax.inject.Inject

class PublicationRepository @Inject constructor(
    private val publicationApiService: PublicationApiService
) {
    suspend fun getPublications(): List<PublicationDTO> {
        return publicationApiService.getPublications()
    }

    suspend fun getPublication(id: Long): PublicationDTO {
        return publicationApiService.getPublication(id)
    }

    suspend fun createPublication(titulo: String, contenido: String, autorId: Long): PublicationDTO {
        val request = CreatePublicationRequest(titulo, contenido, autorId)
        return publicationApiService.createPublication(request)
    }

    suspend fun updatePublication(id: Long, titulo: String, contenido: String, autorId: Long): PublicationDTO {
        val request = CreatePublicationRequest(titulo, contenido, autorId)
        return publicationApiService.updatePublication(id, request)
    }

    suspend fun deletePublication(id: Long) {
        publicationApiService.deletePublication(id)
    }
}