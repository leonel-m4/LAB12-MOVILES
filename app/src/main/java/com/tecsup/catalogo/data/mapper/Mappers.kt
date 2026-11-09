package com.tecsup.catalogo.data.mapper

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
