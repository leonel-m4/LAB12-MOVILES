package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a los productos.
 *
 * La implementación decide si los datos vienen de la API, de Room o de ambos,
 * mientras que los ViewModels solo conocen esta interfaz.
 */
interface ProductoRepository {

    /** Emite la lista de productos que debe observar la pantalla principal. */
    fun observarProductos(): Flow<List<Producto>>

    /** Emite únicamente los productos marcados como favoritos. */
    fun observarFavoritos(): Flow<List<Producto>>

    /** Busca un producto por su identificador. */
    suspend fun obtenerProducto(id: Int): Producto?

    /** Vuelve a consultar la API y devuelve el resultado de la operación. */
    suspend fun refrescarProductos(): Result<Unit>

    /** Actualiza el estado de favorito de un producto. */
    suspend fun cambiarFavorito(id: Int, favorito: Boolean)
}
