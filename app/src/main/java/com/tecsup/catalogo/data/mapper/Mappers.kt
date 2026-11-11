package com.tecsup.catalogo.data.mapper

import com.tecsup.catalogo.data.local.ProductoEntity
import com.tecsup.catalogo.data.remote.dto.ProductoDto
import com.tecsup.catalogo.domain.model.Producto

/**
 * Convierte la respuesta de la API en el modelo que usan las pantallas.
 */
fun ProductoDto.toProducto(): Producto = toEntity().toModelo()

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
    imagenes = images.joinToString("|"),
    rating = rating,
    favorito = favorito,
    descuento = discountPercentage,
    sku = sku,
    peso = weight,
    ancho = dimensions?.width ?: 0.0,
    alto = dimensions?.height ?: 0.0,
    grosor = dimensions?.depth ?: 0.0,
    garantia = warrantyInformation,
    envio = shippingInformation,
    disponibilidad = availabilityStatus,
    devolucion = returnPolicy,
    pedidoMinimo = minimumOrderQuantity
)

/**
 * Convierte la fila de Room en el modelo que usan las pantallas.
 */
fun ProductoEntity.toModelo(): Producto = Producto(
    id = id,
    nombre = nombre,
    // Los textos de la API llegan en inglés; se muestran en español.
    descripcion = descripcionEnEspanol(id, descripcion),
    precio = precio,
    marca = marca,
    categoria = categoria,
    stock = stock,
    imagen = imagen,
    imagenes = imagenes.split("|").filter { it.isNotBlank() },
    rating = rating,
    favorito = favorito,
    descuento = descuento,
    sku = sku,
    // Peso real del modelo (la API manda un valor de prueba sin unidad).
    peso = pesoEnGramos(id),
    ancho = ancho,
    alto = alto,
    grosor = grosor,
    // Garantía de tienda: 1 año del fabricante.
    garantia = GARANTIA_TIENDA,
    envio = traducirEnvio(envio),
    disponibilidad = disponibilidad,
    devolucion = traducirDevolucion(devolucion),
    pedidoMinimo = pedidoMinimo
)
