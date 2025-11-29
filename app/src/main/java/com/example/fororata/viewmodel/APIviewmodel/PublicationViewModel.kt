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

    fun createPublication(titulo: String, contenido: String, autorId: Long) {
        viewModelScope.launch {
            try {
                publicationRepository.createPublication(titulo, contenido, autorId)
                loadPublications() // Recargar lista
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear publicación: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}