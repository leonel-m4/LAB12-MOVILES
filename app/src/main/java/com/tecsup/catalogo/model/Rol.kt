package com.tecsup.catalogo.model

/**
 * Roles disponibles dentro de CatalogoApp.
 *
 * CLIENTE solo consulta el catálogo y sus favoritos.
 * ADMIN además puede acceder al panel de administración.
 */
enum class Rol {
    CLIENTE,
    ADMIN
}
