package com.tecsup.catalogo.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Respuesta paginada de DummyJSON para la lista de productos.
 *
 * Solo se declaran los campos que usa la aplicación; los demás se ignoran
 * gracias a la configuración de Json en RetrofitClient.
 */
@Serializable
data class ProductosResponseDto(
    val products: List<ProductoDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
