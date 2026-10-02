package com.example.basicailikechatgpt.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicailikechatgpt.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val isUserLoggedIn: Boolean get() = repository.isUserLoggedIn()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}