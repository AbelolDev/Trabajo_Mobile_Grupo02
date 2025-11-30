package com.example.fororata.viewmodel.APIviewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

private val Any.nombre_rol: String
    get() {
        TODO()
    }
private val Unit.rol: Any
    get() {
        TODO()
    }
private val Unit.correo: String
    get() {
        TODO()
    }
private val Unit.nombre: String
    get() {
        TODO()
    }

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = mutableStateOf<List<UserDTO>>(emptyList())
    val users: State<List<UserDTO>> = _users

    private val _selectedUser = mutableStateOf<UserDTO?>(null)
    val selectedUser: State<UserDTO?> = _selectedUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _users.value = userRepository.getUsers()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectUser(user: UserDTO) {
        _selectedUser.value = user
    }

    fun updateUser(id: Long, user: Unit, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔵 DEBUG: Actualizando usuario ID: $id - Nombre: ${user.nombre}, Correo: ${user.correo}, Rol: ${user.rol?.nombre_rol}")
                val updatedUser = userRepository.updateUser(id, user)
                println("✅ DEBUG: Usuario actualizado exitosamente - ID: ${updatedUser.id}")
                _successMessage.value = "Usuario actualizado exitosamente"
                loadUsers() // Recargar lista
                onComplete(true, "Usuario actualizado exitosamente")
            } catch (e: Exception) {
                println("❌ DEBUG: Error al actualizar usuario: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar usuario: ${e.message}"
                onComplete(false, "Error: ${e.message}")
            }
        }
    }

    fun deleteUser(id: Long, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔵 DEBUG: Eliminando usuario ID: $id")
                val success = userRepository.deleteUser(id)
                if (success) {
                    println("✅ DEBUG: Usuario eliminado exitosamente")
                    _successMessage.value = "Usuario eliminado exitosamente"
                    loadUsers() // Recargar lista
                    onComplete(true, "Usuario eliminado exitosamente")
                } else {
                    println("❌ DEBUG: No se pudo eliminar el usuario")
                    _errorMessage.value = "No se pudo eliminar el usuario"
                    onComplete(false, "No se pudo eliminar el usuario")
                }
            } catch (e: Exception) {
                println("❌ DEBUG: Error al eliminar usuario: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar usuario: ${e.message}"
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