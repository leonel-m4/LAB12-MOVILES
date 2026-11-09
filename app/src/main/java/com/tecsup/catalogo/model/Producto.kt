package com.tecsup.catalogo.model

/**
 * Producto del catálogo que consumen las pantallas.
 *
 * Es independiente del JSON de la API y de la tabla de Room, de modo que
 * la interfaz no cambia si cambia la fuente de datos.
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val marca: String,
    val categoria: String,
    val stock: Int,
    val imagen: String,
    val rating: Double,
    val favorito: Boolean = false
)
