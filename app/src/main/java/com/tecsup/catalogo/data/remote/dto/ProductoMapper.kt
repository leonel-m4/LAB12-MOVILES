package com.tecsup.catalogo.data.remote.dto

import com.tecsup.catalogo.model.Producto

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
