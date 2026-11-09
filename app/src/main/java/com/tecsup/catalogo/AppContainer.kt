package com.tecsup.catalogo

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.catalogo.data.auth.AuthRepository
import com.tecsup.catalogo.data.auth.FirebaseAuthRepository
import com.tecsup.catalogo.data.local.AppDatabase
import com.tecsup.catalogo.data.remote.RetrofitClient
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.data.repository.ProductoRepositoryImpl

/**
 * Contenedor de dependencias manual de la aplicación.
 *
 * Crea la API, la base de datos, Firebase y los repositorios para que los
 * ViewModels no construyan nada ni conozcan implementaciones concretas.
 */
class AppContainer(context: Context) {
    private val db = AppDatabase.obtener(context)
    private val api = RetrofitClient.api

    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(firebaseAuth, firestore)
    }

    val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(api, db.productoDao())
    }
}
