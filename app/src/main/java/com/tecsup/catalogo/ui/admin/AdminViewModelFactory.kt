package com.tecsup.catalogo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.catalogo.AppContainer

/**
 * Fábrica del ViewModel del panel Admin que recibe el contenedor de dependencias.
 */
class AdminViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        AdminViewModel::class.java -> AdminViewModel(container.productoRepository) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
