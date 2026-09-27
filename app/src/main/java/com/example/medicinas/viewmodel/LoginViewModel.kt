package com.example.medicinas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinas.data.local.dao.UsuarioDao
import kotlinx.coroutines.delay // Importación necesaria para el retraso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMensaje: String? = null,
    val loginExitoso: Boolean = false,
    val rolUsuario: String = ""
)

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(usuario: String, contrasena: String) {
        if (usuario.isBlank() || contrasena.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMensaje = "Los campos no pueden estar vacíos")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMensaje = null)

        viewModelScope.launch {
            // RETRASO ARTIFICIAL: Espera 2 segundos (2000 milisegundos) para que el loader sea visible
            delay(2000)

            val userLogueado = usuarioDao.login(usuario, contrasena)

            if (userLogueado != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginExitoso = true,
                    rolUsuario = userLogueado.rol
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMensaje = "Credenciales incorrectas"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}