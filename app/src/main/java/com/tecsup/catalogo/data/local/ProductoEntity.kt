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
    val favorito: Boolean = false,
    // Características (se agregaron en la versión 2 de la base de datos)
    val descuento: Double = 0.0,
    val sku: String = "",
    val peso: Double = 0.0,
    val ancho: Double = 0.0,
    val alto: Double = 0.0,
    val grosor: Double = 0.0,
    val garantia: String = "",
    val envio: String = "",
    val disponibilidad: String = "",
    val devolucion: String = "",
    val pedidoMinimo: Int = 1
)
