package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.model.Sesion
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de autenticación de la aplicación.
 *
 * En la fase de Firebase se implementará con Firebase Auth y Firestore,
 * pero la interfaz de usuario solo depende de esta abstracción.
 */
interface AuthRepository {

    /** Emite la sesión activa o null cuando no hay usuario autenticado. */
    fun observarSesion(): Flow<Sesion?>

    /** Inicia sesión con correo y contraseña. */
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>

    /** Registra un usuario nuevo con correo y contraseña. */
    suspend fun registrar(correo: String, clave: String): Result<Sesion>

    /** Cierra la sesión activa. */
    fun cerrarSesion()
}
