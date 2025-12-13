package com.example.fororata.viewmodel.APIviewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.api.dto.RolDTO
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

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

    // Estado para la creación/edición de usuario
    private val _userForm = mutableStateOf<UserDTO?>(null)
    val userForm: State<UserDTO?> = _userForm

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _users.value = userRepository.getUsers()
                println("DEBUG: ${_users.value.size} usuarios cargados")
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
                println("ERROR en loadUsers: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectUser(user: UserDTO) {
        _selectedUser.value = user
    }

    fun getUserById(id: Long, onComplete: (UserDTO?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getUserById(id)
                onComplete(user)
            } catch (e: Exception) {
                println("ERROR obteniendo usuario $id: ${e.message}")
                _errorMessage.value = "Error obteniendo usuario: ${e.message}"
                onComplete(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createUser(user: UserDTO, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                println("DEBUG: Creando usuario - Nombre: ${user.nombre}, Correo: ${user.correo}, Rol: ${user.rol.nombre_rol}")

                // Validaciones
                val validationError = validateUser(user, isNewUser = true)
                if (validationError != null) {
                    throw IllegalArgumentException(validationError)
                }

                val createdUser = userRepository.createUser(user)
                println("DEBUG: Usuario creado exitosamente - ID: ${createdUser.id}, Nombre: ${createdUser.nombre}")

                _successMessage.value = "Usuario '${createdUser.nombre}' creado exitosamente"
                loadUsers() // Recargar lista para incluir el nuevo usuario
                _userForm.value = null // Limpiar formulario
                onComplete(true, "Usuario creado exitosamente")

            } catch (e: IllegalArgumentException) {
                println("DEBUG: Error de validación: ${e.message}")
                _errorMessage.value = e.message
                onComplete(false, e.message ?: "Error de validación")

            } catch (e: Exception) {
                println("DEBUG: Error al crear usuario: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al crear usuario: ${e.message}"
                onComplete(false, "Error: ${e.message}")

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(id: Long, user: UserDTO, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                println("DEBUG: Actualizando usuario ID: $id - Nombre: ${user.nombre}, Correo: ${user.correo}")

                // Validaciones
                val validationError = validateUser(user, isNewUser = false)
                if (validationError != null) {
                    throw IllegalArgumentException(validationError)
                }

                val userToUpdate = if (user.clave.isBlank()) {
                    user.copy(clave = "NO_CHANGE")
                } else {
                    user
                }

                val updatedUser = userRepository.updateUser(id, userToUpdate)
                println("DEBUG: Usuario actualizado exitosamente - ID: ${updatedUser.id}")

                _successMessage.value = "Usuario '${updatedUser.nombre}' actualizado exitosamente"
                loadUsers() // Recargar lista
                _selectedUser.value = null // Deseleccionar usuario
                onComplete(true, "Usuario actualizado exitosamente")

            } catch (e: IllegalArgumentException) {
                println("DEBUG: Error de validación: ${e.message}")
                _errorMessage.value = e.message
                onComplete(false, e.message ?: "Error de validación")

            } catch (e: Exception) {
                println("DEBUG: Error al actualizar usuario: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al actualizar usuario: ${e.message}"
                onComplete(false, "Error: ${e.message}")

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(id: Long, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val userName = _users.value.find { it.id == id }?.nombre ?: "Usuario $id"
                println("DEBUG: Eliminando usuario ID: $id ($userName)")

                userRepository.deleteUser(id)
                println("DEBUG: Usuario eliminado exitosamente")

                _successMessage.value = "Usuario '$userName' eliminado exitosamente"
                loadUsers() // Recargar lista
                _selectedUser.value = null // Deseleccionar usuario si era el seleccionado
                onComplete(true, "Usuario eliminado exitosamente")

            } catch (e: Exception) {
                println("DEBUG: Error al eliminar usuario: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error al eliminar usuario: ${e.message}"
                onComplete(false, "Error: ${e.message}")

            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funciones para manejar el formulario
    fun startCreateUser(rolDTO: RolDTO) {
        _userForm.value = createEmptyUserDTO(rolDTO)
        _selectedUser.value = null
    }

    fun startEditUser(user: UserDTO) {
        _userForm.value = user.copy() // Crear copia para edición
        _selectedUser.value = user
    }

    fun updateUserForm(user: UserDTO) {
        _userForm.value = user
    }

    fun clearUserForm() {
        _userForm.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    // Función de utilidad para crear un objeto UserDTO básico
    fun createEmptyUserDTO(rolDTO: RolDTO): UserDTO {
        return UserDTO(
            id = null,
            nombre = "",
            correo = "",
            clave = "",
            acepta_terminos = 0,
            rol = rolDTO
        )
    }

    // Función de validación reutilizable
    private fun validateUser(user: UserDTO, isNewUser: Boolean): String? {
        return when {
            user.nombre.isBlank() -> "El nombre es requerido"
            user.nombre.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            user.correo.isBlank() -> "El correo es requerido"
            !user.correo.contains("@") -> "El correo debe ser válido"
            isNewUser && user.clave.isBlank() -> "La contraseña es requerida"
            isNewUser && user.clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            isNewUser && user.acepta_terminos != 1 -> "Debe aceptar los términos y condiciones"
            else -> null
        }
    }

    // Función para buscar usuario por ID en la lista local
    fun findUserById(id: Long): UserDTO? {
        return _users.value.find { it.id == id }
    }

    // Función para refrescar datos
    fun refresh() {
        loadUsers()
        clearError()
        clearSuccess()
    }
}