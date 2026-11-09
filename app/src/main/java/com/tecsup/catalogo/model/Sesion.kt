package com.tecsup.catalogo.model

/**
 * Representa al usuario autenticado en la aplicación.
 *
 * @param uid identificador único que entrega Firebase Auth.
 * @param correo correo con el que el usuario inició sesión.
 * @param rol rol que determina las opciones visibles en la interfaz.
 */
data class Sesion(
    val uid: String,
    val correo: String,
    val rol: Rol
)
