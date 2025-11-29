package com.example.fororata.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.api.dto.RolDTO
import com.example.fororata.api.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ========== SEALED CLASSES FUERA DEL VIEWMODEL ==========

// Estados de creación de usuario
sealed class CreateUserState {
    object Idle : CreateUserState()
    object Loading : CreateUserState()
    data class Success(val message: String) : CreateUserState()
    data class Error(val message: String) : CreateUserState()
}

// Estados de login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

// Estados del formulario de creación
data class AdminFormState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val nombreError: String? = null,
    val correoError: String? = null,
    val claveError: String? = null,
    val confirmarClaveError: String? = null
) {
    val isFormValid: Boolean
        get() = nombre.isNotEmpty() &&
                correo.isNotEmpty() &&
                clave.isNotEmpty() &&
                confirmarClave.isNotEmpty() &&
                nombreError == null &&
                correoError == null &&
                claveError == null &&
                confirmarClaveError == null
}

// Estados del formulario de login
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isFormValid: Boolean
        get() = email.isNotEmpty() &&
                password.isNotEmpty() &&
                emailError == null &&
                passwordError == null
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estados del formulario de creación
    private val _adminFormState = mutableStateOf(AdminFormState())
    val adminFormState: State<AdminFormState> = _adminFormState

    private val _createUserState = MutableStateFlow<CreateUserState>(CreateUserState.Idle)
    val createUserState: StateFlow<CreateUserState> = _createUserState.asStateFlow()

    // Estados del formulario de login
    private val _loginFormState = mutableStateOf(LoginFormState())
    val loginFormState: State<LoginFormState> = _loginFormState

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _users = MutableStateFlow<List<UserDTO>>(emptyList())
    val users: StateFlow<List<UserDTO>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    // Actualizar campos del formulario de creación
    fun onNombreChange(nombre: String) {
        _adminFormState.value = _adminFormState.value.copy(
            nombre = nombre,
            nombreError = if (nombre.isNotEmpty() && nombre.length < 2) "El nombre debe tener al menos 2 caracteres" else null
        )
    }

    fun onCorreoChange(correo: String) {
        _adminFormState.value = _adminFormState.value.copy(
            correo = correo,
            correoError = if (correo.isNotEmpty() && !isValidEmail(correo)) "Ingresa un correo electrónico válido" else null
        )
    }

    fun onClaveChange(clave: String) {
        _adminFormState.value = _adminFormState.value.copy(
            clave = clave,
            claveError = if (clave.isNotEmpty()) "La contraseña debe tener al menos 6 caracteres" else null
        )
        // Revalidar confirmación cuando cambia la contraseña principal
        validatePasswordMatch()
    }

    fun onConfirmarClaveChange(confirmarClave: String) {
        _adminFormState.value = _adminFormState.value.copy(
            confirmarClave = confirmarClave
        )
        validatePasswordMatch()
    }

    private fun validatePasswordMatch() {
        val state = _adminFormState.value
        _adminFormState.value = state.copy(
            confirmarClaveError = if (state.confirmarClave.isNotEmpty() && state.clave != state.confirmarClave)
                "Las contraseñas no coinciden" else null
        )
    }

    // Validaciones del formulario de creación
    fun validateForm(): Boolean {
        val state = _adminFormState.value

        val nombreError = if (state.nombre.length < 2) "El nombre debe tener al menos 2 caracteres" else null
        val correoError = if (!isValidEmail(state.correo)) "Ingresa un correo electrónico válido" else null
        val claveError = if (state.clave.length < 0) "La contraseña debe tener al menos 6 caracteres" else null
        val confirmarClaveError = if (state.clave != state.confirmarClave) "Las contraseñas no coinciden" else null

        _adminFormState.value = state.copy(
            nombreError = nombreError,
            correoError = correoError,
            claveError = claveError,
            confirmarClaveError = confirmarClaveError
        )

        return nombreError == null && correoError == null && claveError == null && confirmarClaveError == null
    }

    // Crear administrador
    fun createAdmin() {
        if (!validateForm()) {
            _createUserState.value = CreateUserState.Error("Por favor corrige los errores del formulario")
            return
        }

        _createUserState.value = CreateUserState.Loading

        viewModelScope.launch {
            try {
                val nuevoAdmin = UserDTO(
                    id = null,
                    nombre = _adminFormState.value.nombre,
                    correo = _adminFormState.value.correo,
                    clave = _adminFormState.value.clave,
                    acepta_terminos = 1,
                    rol = RolDTO(
                        id_rol = 1, // ID del rol Administrador
                        nombre_rol = "Administrador",
                        descripcion_rol = "Control total del sistema"
                    )
                )

                val resultado = userRepository.createUser(nuevoAdmin)

                _createUserState.value = CreateUserState.Success("Administrador creado exitosamente")
                clearForm()

            } catch (e: Exception) {
                _createUserState.value = CreateUserState.Error("Error al crear administrador: ${e.message}")
            }
        }
    }

    // Limpiar formulario de creación
    fun clearForm() {
        _adminFormState.value = AdminFormState()
    }

    // Limpiar mensajes de creación
    fun clearMessages() {
        _createUserState.value = CreateUserState.Idle
    }

    // ========== MÉTODOS PARA LOGIN ==========

    // Métodos para actualizar el formulario de login
    fun onLoginEmailChange(email: String) {
        _loginFormState.value = _loginFormState.value.copy(
            email = email,
            emailError = if (email.isNotEmpty() && !isValidEmail(email))
                "Ingresa un correo electrónico válido" else null
        )
    }

    fun onLoginPasswordChange(password: String) {
        _loginFormState.value = _loginFormState.value.copy(
            password = password,
            passwordError = if (password.isNotEmpty() && password.length < 0)
                "La contraseña debe tener al menos 6 caracteres" else null
        )
    }

    // Método para login
    fun loginAdmin() {
        if (!validateLoginForm()) {
            _loginState.value = LoginState.Error("Por favor corrige los errores del formulario")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // Verificar credenciales contra los usuarios cargados
                val isAdmin = verifyAdminCredentials(
                    _loginFormState.value.email,
                    _loginFormState.value.password,
                    _users.value
                )

                if (isAdmin) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(
                        "Credenciales incorrectas o no tienes permisos de administrador"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error al verificar credenciales: ${e.message}")
            }
        }
    }

    // Cargar usuarios
    fun loadUsers() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val usersList = userRepository.getUsers()
                _users.value = usersList
            } catch (e: Exception) {
                // Manejar error silenciosamente o mostrar en logs
                println("Error cargando usuarios: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Validar formulario de login
    private fun validateLoginForm(): Boolean {
        val state = _loginFormState.value

        val emailError = if (!isValidEmail(state.email)) "Ingresa un correo electrónico válido" else null
        val passwordError = if (state.password.length < 0) "La contraseña debe tener al menos 6 caracteres" else null

        _loginFormState.value = state.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    // Función para verificar credenciales
    private fun verifyAdminCredentials(
        email: String,
        password: String,
        users: List<UserDTO>
    ): Boolean {
        return users.any { user ->
            val emailMatch = user.correo.equals(email, ignoreCase = true)
            val passwordMatch = user.clave == password
            val isAdmin = user.rol?.nombre_rol?.equals("Administrador", ignoreCase = true) == true

            emailMatch && passwordMatch && isAdmin
        }
    }

    // Limpiar estado de login
    fun clearLoginState() {
        _loginState.value = LoginState.Idle
        _loginFormState.value = LoginFormState()
    }

    // Limpiar todos los estados
    fun clearAllStates() {
        clearForm()
        clearMessages()
        clearLoginState()
        _users.value = emptyList()
        _isLoading.value = false
    }

    // Validación de email (compartida)
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailRegex.matches(email)
    }
}