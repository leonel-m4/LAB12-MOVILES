package com.tecsup.catalogo.domain.model

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
    val favorito: Boolean = false,
    // Características que vienen de la API
    val descuento: Double = 0.0,
    val sku: String = "",
    val peso: Double = 0.0,
    val ancho: Double = 0.0,
    val alto: Double = 0.0,
    val grosor: Double = 0.0,
    val garantia: String = "",
    val envio: String = "",
    val disponibilidad: String = "",
    val devolucion: String = "",
    val pedidoMinimo: Int = 1
)
