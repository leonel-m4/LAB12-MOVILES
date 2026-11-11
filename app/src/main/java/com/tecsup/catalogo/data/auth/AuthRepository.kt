package com.tecsup.catalogo.data.auth

import com.tecsup.catalogo.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?   // null si no hay sesión
    fun cerrarSesion()

    // Fase 2: crea la cuenta en Firebase Authentication.
    suspend fun registrar(correo: String, clave: String): Result<Sesion>
}
