package com.tecsup.catalogo.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Consultas de Room sobre la tabla de productos.
 */
@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun todos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE favorito = 1 ORDER BY nombre")
    fun favoritos(): Flow<List<ProductoEntity>>

    @Query("SELECT id FROM productos WHERE favorito = 1")
    suspend fun idsFavoritos(): List<Int>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM productos WHERE favorito = 1")
    suspend fun contarFavoritos(): Int

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    @Upsert
    suspend fun guardarTodos(productos: List<ProductoEntity>)

    @Query("UPDATE productos SET favorito = NOT favorito WHERE id = :id")
    suspend fun alternarFavorito(id: Int)

    @Query("DELETE FROM productos")
    suspend fun borrarTodo()
}
