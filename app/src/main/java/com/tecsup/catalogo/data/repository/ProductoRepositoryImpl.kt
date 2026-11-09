package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.data.local.ProductoDao
import com.tecsup.catalogo.data.mapper.toEntity
import com.tecsup.catalogo.data.mapper.toModelo
import com.tecsup.catalogo.data.remote.ApiService
import com.tecsup.catalogo.domain.model.Producto
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

/**
 * Implementación del repositorio con Room como fuente única de verdad.
 *
 * La lista que ve el usuario sale siempre de Room; Retrofit solo la
 * actualiza y se conservan los favoritos al sincronizar.
 */
class ProductoRepositoryImpl(
    private val api: ApiService,
    private val dao: ProductoDao
) : ProductoRepository {

    override val items = dao.todos().map { lista ->
        lista.map { it.toModelo() }
    }

    override val favoritos = dao.favoritos().map { lista ->
        lista.map { it.toModelo() }
    }

    override suspend fun refrescar(): Result<Int> = try {
        val idsFavoritos = dao.idsFavoritos().toSet()
        val nuevos = api.obtenerSmartphones().products
            .map { it.toEntity(favorito = it.id in idsFavoritos) }
        dao.guardarTodos(nuevos)
        Result.success(nuevos.size)
    } catch (e: IOException) {
        Result.failure(e)          // sin internet
    } catch (e: HttpException) {
        Result.failure(e)          // 404, 500...
    }

    override suspend fun obtenerDetalle(id: Int): Result<Producto> = try {
        // R04/PA-06: el detalle sale del segundo endpoint.
        val dto = api.obtenerProducto(id)
        val local = dao.obtenerPorId(id)
        val entity = dto.toEntity(favorito = local?.favorito ?: false)
        dao.guardarTodos(listOf(entity))   // conserva el favorito
        Result.success(entity.toModelo())
    } catch (e: Exception) {
        // Sin internet: si está en Room, se muestra lo guardado.
        val local = dao.obtenerPorId(id)
        if (local != null) Result.success(local.toModelo())
        else Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
    override suspend fun limpiarCache() = dao.borrarTodo()
}
