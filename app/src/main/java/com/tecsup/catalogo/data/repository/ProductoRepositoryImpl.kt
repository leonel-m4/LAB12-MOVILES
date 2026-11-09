package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.data.remote.ApiService
import com.tecsup.catalogo.data.remote.dto.toProducto
import com.tecsup.catalogo.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio que consulta DummyJSON con Retrofit.
 *
 * Mantiene la lista en memoria y superpone los favoritos para que la
 * interfaz reciba siempre un [Producto] completo. En el siguiente hito
 * Room pasará a ser la fuente de verdad.
 */
class ProductoRepositoryImpl(
    private val api: ApiService
) : ProductoRepository {

    private val productos = MutableStateFlow<List<Producto>>(emptyList())
    private val idsFavoritos = MutableStateFlow<Set<Int>>(emptySet())

    override fun observarProductos(): Flow<List<Producto>> =
        combine(productos, idsFavoritos) { lista, favoritos ->
            lista.map { producto ->
                producto.copy(favorito = producto.id in favoritos)
            }
        }

    override fun observarFavoritos(): Flow<List<Producto>> =
        observarProductos().map { lista ->
            lista.filter { producto -> producto.favorito }
        }

    override suspend fun obtenerProducto(id: Int): Producto? {
        val productoLocal = productos.value.firstOrNull { it.id == id }
        if (productoLocal != null) {
            return productoLocal.copy(favorito = id in idsFavoritos.value)
        }
        return runCatching { api.obtenerProducto(id).toProducto() }.getOrNull()
    }

    override suspend fun refrescarProductos(): Result<Unit> = runCatching {
        val respuesta = api.obtenerSmartphones()
        productos.value = respuesta.products.map { dto -> dto.toProducto() }
    }

    override suspend fun cambiarFavorito(id: Int, favorito: Boolean) {
        idsFavoritos.value = if (favorito) {
            idsFavoritos.value + id
        } else {
            idsFavoritos.value - id
        }
    }
}
