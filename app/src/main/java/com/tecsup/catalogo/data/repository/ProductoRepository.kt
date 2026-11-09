package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    val items: Flow<List<Producto>>          // sale de Room
    val favoritos: Flow<List<Producto>>      // sale de Room
    suspend fun refrescar(): Result<Int>     // Retrofit -> Room; cuántos guardó
    suspend fun obtenerDetalle(id: Int): Result<Producto>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}
