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

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _users.value = userRepository.getUsers()
                _errorMessage.value = null
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

    fun clearError() {
        _errorMessage.value = null
    }
}