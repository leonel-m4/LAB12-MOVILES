package com.tecsup.catalogo.data.remote

import com.tecsup.catalogo.data.remote.dto.ProductoDto
import com.tecsup.catalogo.data.remote.dto.ProductosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Endpoints de DummyJSON que consume la aplicación.
 *
 * La categoría se fija en smartphones porque es el tema del catálogo.
 */
interface ApiService {

    /** Devuelve todos los smartphones disponibles. */
    @GET("products/category/smartphones")
    suspend fun obtenerSmartphones(): ProductosResponseDto

    /** Devuelve el detalle de un smartphone por su identificador. */
    @GET("products/{id}")
    suspend fun obtenerProducto(@Path("id") id: Int): ProductoDto
}
