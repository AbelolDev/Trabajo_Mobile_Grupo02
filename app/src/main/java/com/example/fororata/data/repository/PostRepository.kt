package com.example.fororata.data.repository

import com.example.fororata.data.dataModel.Post
import com.example.fororata.data.remote.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio responsable de obtener publicaciones desde la API externa.
 * Mantiene las funciones ya existentes y agrega soporte para Top 10 posts.
 */
class PostRepository {

    /**
     * Obtiene publicaciones generales (endpoint random o normal).
     */
    suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        api.getPost()
    }

    /**
     * Obtiene las Top 10 publicaciones del día desde la API.
     * Esta función es usada por el ViewModel en la sección Top Posts.
     */
    suspend fun getTopPosts(): List<Post> = withContext(Dispatchers.IO) {
        api.getTopPosts()
    }
}
