package com.tecsup.catalogo

import android.content.Context
import com.tecsup.catalogo.data.local.AppDatabase
import com.tecsup.catalogo.data.remote.RetrofitClient
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.data.repository.ProductoRepositoryImpl

/**
 * Contenedor de dependencias manual de la aplicación.
 *
 * Crea la API, la base de datos y los repositorios para que los ViewModels
 * no construyan Retrofit ni Room ni conozcan implementaciones concretas.
 */
class AppContainer(context: Context) {
    private val db = AppDatabase.obtener(context)
    private val api = RetrofitClient.api

    val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(api, db.productoDao())
    }
}
