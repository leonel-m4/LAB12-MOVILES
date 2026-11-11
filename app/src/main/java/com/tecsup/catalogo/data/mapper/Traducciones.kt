package com.tecsup.catalogo.data.mapper

/*
 * DummyJSON envía los textos en inglés. Aquí están sus traducciones al
 * español para mostrarlas en la app. Si llega un texto que no está en la
 * lista, se muestra tal como vino de la API.
 */

/** Descripción en español de cada smartphone, según su id en la API. */
private val descripcionesEs = mapOf(
    121 to "El iPhone 5s es un clásico de Apple, conocido por su tamaño compacto. Aunque ya es un modelo antiguo, sigue funcionando bien para el uso diario.",
    122 to "El iPhone 6 tiene una pantalla más grande y mejor rendimiento que los modelos anteriores. Fue uno de los celulares más populares de su época.",
    123 to "El iPhone 13 Pro es un celular de gama alta con un sistema de cámaras muy bueno, un procesador potente y una pantalla de gran calidad.",
    124 to "El iPhone X fue el primer iPhone con pantalla OLED casi sin bordes y con desbloqueo facial (Face ID). Tiene muy buen rendimiento.",
    125 to "El Oppo A57 es un celular de gama media con un diseño delgado. Ofrece un buen equilibrio entre rendimiento y precio.",
    126 to "El Oppo F19 Pro Plus destaca por sus cámaras. Toma muy buenas fotos y tiene un rendimiento rápido.",
    127 to "El Oppo K1 tiene un diseño moderno y un rendimiento confiable. Es una buena opción para el uso de todos los días.",
    128 to "El Realme C35 es un celular económico con lo necesario para el día a día. Es fácil de usar y funciona de forma confiable.",
    129 to "El Realme X es un celular de gama media con un diseño delgado y una buena pantalla. Tiene un buen equilibrio entre rendimiento y cámara.",
    130 to "El Realme XT está pensado para quienes les gusta la fotografía. Sus cámaras toman fotos y videos de buena calidad.",
    131 to "El Samsung Galaxy S7 fue un celular de gama alta con diseño elegante, pantalla de alta resolución y una buena cámara.",
    132 to "El Samsung Galaxy S8 tiene una pantalla Infinity casi sin bordes que se ve muy bien. También tiene una cámara de buena calidad.",
    133 to "El Samsung Galaxy S10 tiene una pantalla AMOLED, varias cámaras para distintos tipos de foto y un rendimiento potente.",
    134 to "El Vivo S1 es un celular de gama media con un diseño llamativo, una pantalla de colores vivos y una cámara cumplidora.",
    135 to "El Vivo V9 es conocido por sus buenas selfies. Tiene una pantalla con notch, doble cámara trasera y un diseño moderno.",
    136 to "El Vivo X21 es un celular de gama alta con el lector de huella dentro de la pantalla, una pantalla de alta resolución y buenas cámaras."
)

/** Devuelve la descripción en español; si no existe, la original. */
fun descripcionEnEspanol(id: Int, original: String): String =
    descripcionesEs[id] ?: original

/** Traduce el texto de envío que manda la API. */
fun traducirEnvio(texto: String): String = when (texto) {
    "Ships overnight" -> "Al día siguiente"
    "Ships in 1-2 business days" -> "De 1 a 2 días hábiles"
    "Ships in 3-5 business days" -> "De 3 a 5 días hábiles"
    "Ships in 1 week" -> "En 1 semana"
    "Ships in 2 weeks" -> "En 2 semanas"
    "Ships in 1 month" -> "En 1 mes"
    else -> texto
}

/** Traduce el texto de devolución que manda la API. */
fun traducirDevolucion(texto: String): String = when (texto) {
    "No return policy" -> "No acepta devoluciones"
    "7 days return policy" -> "7 días"
    "30 days return policy" -> "30 días"
    "60 days return policy" -> "60 días"
    "90 days return policy" -> "90 días"
    else -> texto
}
