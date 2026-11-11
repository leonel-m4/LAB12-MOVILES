package com.tecsup.catalogo.data.mapper

/*
 * Datos que se muestran como en una tienda de celulares (Movistar, Claro,
 * Entel). DummyJSON es una API de prueba y algunos de sus valores no son
 * reales (por ejemplo, el peso viene como "5" sin unidad). Estos datos
 * reemplazan esos valores al pasar al modelo que usa la pantalla.
 */

/** Garantía que se ofrece en tienda: 1 año del fabricante. */
const val GARANTIA_TIENDA = "1 año"

/** Peso real de cada smartphone en gramos, según su id en la API. */
private val pesosEnGramos = mapOf(
    121 to 112.0, // iPhone 5s
    122 to 129.0, // iPhone 6
    123 to 203.0, // iPhone 13 Pro
    124 to 174.0, // iPhone X
    125 to 147.0, // Oppo A57
    126 to 173.0, // Oppo F19 Pro Plus
    127 to 156.0, // Oppo K1
    128 to 189.0, // Realme C35
    129 to 191.0, // Realme X
    130 to 183.0, // Realme XT
    131 to 152.0, // Samsung Galaxy S7
    132 to 155.0, // Samsung Galaxy S8
    133 to 157.0, // Samsung Galaxy S10
    134 to 179.0, // Vivo S1
    135 to 150.0, // Vivo V9
    136 to 156.0  // Vivo X21
)

/** Peso real en gramos; 0 si el modelo no está en la lista. */
fun pesoEnGramos(id: Int): Double = pesosEnGramos[id] ?: 0.0
