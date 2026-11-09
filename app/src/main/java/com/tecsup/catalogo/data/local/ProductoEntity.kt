package com.tecsup.catalogo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de Room donde se guardan los productos descargados.
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val marca: String,
    val categoria: String,
    val stock: Int,
    val imagen: String,
    val rating: Double,
    val favorito: Boolean = false
)
