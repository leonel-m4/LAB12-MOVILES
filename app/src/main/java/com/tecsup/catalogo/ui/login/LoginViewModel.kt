package com.tecsup.catalogo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.auth.AuthRepository
import com.tecsup.catalogo.domain.model.Sesion
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del inicio de sesión.
 *
 * Expone la sesión activa y el estado de carga o error del login.
 * Valida los campos antes de llamar a Firebase.
 */
class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    private val _estado = MutableStateFlow<UiState<Unit>>(UiState.Cargando)
    val estado: StateFlow<UiState<Unit>> = _estado.asStateFlow()

    init {
        // Si ya había una sesión guardada, se entra directo al catálogo.
        viewModelScope.launch {
            _sesion.value = runCatching { repository.sesionActual() }.getOrNull()
            _estado.value = UiState.Exito(Unit)
        }
    }

    /** Valida los campos y, si están bien, inicia sesión en Firebase. */
    fun iniciarSesion(correo: String, clave: String) {
        val mensaje = validar(correo.trim(), clave)
        if (mensaje != null) {
            _estado.value = UiState.Error(mensaje)
            return
        }

        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repository.iniciarSesion(correo.trim(), clave)
                .onSuccess { sesion ->
                    _sesion.value = sesion
                    _estado.value = UiState.Exito(Unit)
                }
                .onFailure {
                    _estado.value = UiState.Error(
                        "No se pudo iniciar sesión. Revisa tu correo y contraseña."
                    )
                }
        }
    }

    /** Cierra la sesión y vuelve al login. */
    fun cerrarSesion() {
        repository.cerrarSesion()
        _sesion.value = null
        _estado.value = UiState.Exito(Unit)
    }

    /** Devuelve el mensaje de error de validación o null si todo está bien. */
    private fun validar(correo: String, clave: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo."
        !correo.matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) -> "El correo no es válido."
        clave.isBlank() -> "Ingresa tu contraseña."
        clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
        else -> null
    }
}
