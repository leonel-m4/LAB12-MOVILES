package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.data.mapper.toProducto
import com.tecsup.catalogo.data.remote.ApiService
import com.tecsup.catalogo.domain.model.Producto
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

    override val items: Flow<List<Producto>> =
        combine(productos, idsFavoritos) { lista, favoritos ->
            lista.map { producto ->
                producto.copy(favorito = producto.id in favoritos)
            }
        }

    override val favoritos: Flow<List<Producto>> =
        items.map { lista ->
            lista.filter { producto -> producto.favorito }
        }

    override suspend fun refrescar(): Result<Int> = runCatching {
        val respuesta = api.obtenerSmartphones()
        productos.value = respuesta.products.map { dto -> dto.toProducto() }
        productos.value.size
    }

    override suspend fun obtenerDetalle(id: Int): Result<Producto> = runCatching {
        // El detalle sale del segundo endpoint; si falla, se usa la lista en memoria.
        val producto = runCatching { api.obtenerProducto(id).toProducto() }.getOrNull()
            ?: productos.value.first { it.id == id }
        producto.copy(favorito = id in idsFavoritos.value)
    }

    override suspend fun alternarFavorito(id: Int) {
        idsFavoritos.value = if (id in idsFavoritos.value) {
            idsFavoritos.value - id
        } else {
            idsFavoritos.value + id
        }
    }

    override suspend fun cantidadGuardada(): Int = productos.value.size

    override suspend fun cantidadFavoritos(): Int = idsFavoritos.value.size

    override suspend fun limpiarCache() {
        productos.value = emptyList()
        idsFavoritos.value = emptySet()
    }
}
