package com.tecsup.catalogo.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Representa un producto tal como lo devuelve DummyJSON.
 *
 * Los nombres siguen el JSON de la API; el mapeo al modelo [Producto]
 * se hace en ProductoMapper para no contaminar la interfaz.
 */
@Serializable
data class ProductoDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val brand: String? = null,
    val category: String,
    val stock: Int,
    val rating: Double,
    val thumbnail: String
)
