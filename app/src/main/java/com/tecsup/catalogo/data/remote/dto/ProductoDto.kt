package com.tecsup.catalogo.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Representa un producto tal como lo devuelve DummyJSON.
 *
 * Los nombres siguen el JSON de la API; el mapeo al modelo [Producto]
 * se hace en ProductoMapper para no contaminar la interfaz.
 * Los campos de características tienen valor por defecto por si la API
 * no los envía.
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
    val thumbnail: String,
    val images: List<String> = emptyList(), // fotos grandes del producto
    // Características que trae la API
    val discountPercentage: Double = 0.0,
    val sku: String = "",
    val weight: Double = 0.0,
    val dimensions: DimensionesDto? = null,
    val warrantyInformation: String = "",
    val shippingInformation: String = "",
    val availabilityStatus: String = "",
    val returnPolicy: String = "",
    val minimumOrderQuantity: Int = 1
)

/** Medidas del producto: ancho, alto y grosor. */
@Serializable
data class DimensionesDto(
    val width: Double = 0.0,
    val height: Double = 0.0,
    val depth: Double = 0.0
)
