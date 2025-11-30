package com.example.fororata.viewmodel.APIviewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.api.dto.PublicationDTO
import com.example.fororata.api.repository.PublicationRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class PublicationViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _publications = mutableStateOf<List<PublicationDTO>>(emptyList())
    val publications: State<List<PublicationDTO>> = _publications

    private val _selectedPublication = mutableStateOf<PublicationDTO?>(null)
    val selectedPublication: State<PublicationDTO?> = _selectedPublication

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    init {
        loadPublications()
    }

    fun loadPublications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _publications.value = publicationRepository.getPublications()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar publicaciones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectPublication(publication: PublicationDTO) {
        _selectedPublication.value = publication
    }

    fun createPublication(titulo: String, contenido: String, autorId: Long, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔵 DEBUG: Creando publicación - Título: $titulo, Contenido: $contenido, AutorID: $autorId")
                val result = publicationRepository.createPublication(titulo, contenido, autorId)
                println("✅ DEBUG: Publicación creada exitosamente - ID: ${result.id}")
                _successMessage.value = "Publicación creada exitosamente"
                loadPublications() // Recargar lista
                onComplete(true, "Publicación creada exitosamente")
            } catch (e: Exception) {
                println("❌ DEBUG: Error al crear publicación: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al crear publicación: ${e.message}"
                onComplete(false, "Error: ${e.message}")
            }
        }
    }

    fun updatePublication(id: Long, titulo: String, contenido: String, autorId: Long, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔵 DEBUG: Actualizando publicación ID: $id - Título: $titulo, Contenido: $contenido")
                val result = publicationRepository.updatePublication(id, titulo, contenido, autorId)
                println("✅ DEBUG: Publicación actualizada exitosamente - ID: ${result.id}")
                _successMessage.value = "Publicación actualizada exitosamente"
                loadPublications() // Recargar lista
                onComplete(true, "Publicación actualizada exitosamente")
            } catch (e: Exception) {
                println("❌ DEBUG: Error al actualizar publicación: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar publicación: ${e.message}"
                onComplete(false, "Error: ${e.message}")
            }
        }
    }

    fun deletePublication(id: Long, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔵 DEBUG: Eliminando publicación ID: $id")
                publicationRepository.deletePublication(id)
                println("✅ DEBUG: Publicación eliminada exitosamente")
                _successMessage.value = "Publicación eliminada exitosamente"
                loadPublications() // Recargar lista
                onComplete(true, "Publicación eliminada exitosamente")
            } catch (e: Exception) {
                println("❌ DEBUG: Error al eliminar publicación: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar publicación: ${e.message}"
                onComplete(false, "Error: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }
}