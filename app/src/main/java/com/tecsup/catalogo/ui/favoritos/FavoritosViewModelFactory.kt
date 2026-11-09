package com.tecsup.catalogo.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.catalogo.AppContainer

/**
 * Fábrica del ViewModel de favoritos que recibe el contenedor de dependencias.
 */
class FavoritosViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        FavoritosViewModel::class.java -> FavoritosViewModel(container.productoRepository) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
