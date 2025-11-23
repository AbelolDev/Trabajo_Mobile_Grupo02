package com.example.fororata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.data.dataModel.Post
import com.example.fororata.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    // Lista completa de posts
    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    // Nueva lista: top 10 del día
    private val _topPosts = MutableStateFlow<List<Post>>(emptyList())
    val topPosts: StateFlow<List<Post>> = _topPosts

    // Estado de carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    // Se ejecuta automáticamente al iniciar
    init {
        fetchPosts()
        getTopPosts()
    }


    /**
     * Obtiene TODAS las publicaciones desde la API
     */
    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }


    /**
     * Obtiene las top 10 publicaciones del día desde la API externa.
     * Esta función es nueva.
     */
    fun getTopPosts() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                // Obtenemos publicaciones de la API
                val posts = repository.getPosts()

                // Forzamos top 10
                _topPosts.value = posts.take(10)

            } catch (e: Exception) {
                _errorMessage.value =
                    "Error al obtener las top publicaciones: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }
}
