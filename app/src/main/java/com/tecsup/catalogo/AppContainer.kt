package com.tecsup.catalogo

import com.tecsup.catalogo.data.remote.RetrofitClient
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.data.repository.ProductoRepositoryImpl

/**
 * Contenedor de dependencias manual de la aplicación.
 *
 * Centraliza la creación de repositorios para que los ViewModels no
 * construyan Retrofit ni conozcan implementaciones concretas.
 */
class AppContainer {

    val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(RetrofitClient.api)
    }
}
