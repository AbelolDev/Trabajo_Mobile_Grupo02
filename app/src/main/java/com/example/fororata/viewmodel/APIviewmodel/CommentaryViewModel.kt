package com.example.fororata.viewmodel.APIviewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.api.dto.CommentaryDTO
import com.example.fororata.api.repository.CommentaryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CommentaryViewModel @Inject constructor(
    private val commentaryRepository: CommentaryRepository
) : ViewModel() {

    private val _commentaries = mutableStateOf<List<CommentaryDTO>>(emptyList())
    val commentaries: State<List<CommentaryDTO>> = _commentaries

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun loadCommentaries(publicacionId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _commentaries.value = commentaryRepository.getCommentaries(publicacionId)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar comentarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCommentary(publicacionId: Long, autorId: Long, texto: String, estrellas: Int? = null) {
        viewModelScope.launch {
            try {
                commentaryRepository.createCommentary(publicacionId, autorId, texto, estrellas)
                loadCommentaries(publicacionId) // Recargar comentarios
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear comentario: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}