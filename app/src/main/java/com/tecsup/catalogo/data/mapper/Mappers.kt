package com.tecsup.catalogo.data.mapper

import com.tecsup.catalogo.data.local.ProductoEntity
import com.tecsup.catalogo.data.remote.dto.ProductoDto
import com.tecsup.catalogo.domain.model.Producto

/**
 * Convierte la respuesta de la API en el modelo que usan las pantallas.
 */
fun ProductoDto.toProducto(): Producto = Producto(
    id = id,
    nombre = title,
    descripcion = description,
    precio = price,
    marca = brand.orEmpty(),
    categoria = category,
    stock = stock,
    imagen = thumbnail,
    rating = rating
)

/**
 * Convierte la respuesta de la API en la fila que se guarda en Room.
 */
fun ProductoDto.toEntity(favorito: Boolean = false): ProductoEntity = ProductoEntity(
    id = id,
    nombre = title,
    descripcion = description,
    precio = price,
    marca = brand.orEmpty(),
    categoria = category,
    stock = stock,
    imagen = thumbnail,
    rating = rating,
    favorito = favorito
)

/**
 * Convierte la fila de Room en el modelo que usan las pantallas.
 */
fun ProductoEntity.toModelo(): Producto = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    marca = marca,
    categoria = categoria,
    stock = stock,
    imagen = imagen,
    rating = rating,
    favorito = favorito
)
