package com.tecsup.catalogo.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.catalogo.AppContainer

/**
 * Fábrica del ViewModel de detalle.
 *
 * Recibe el contenedor de dependencias y el id del producto elegido.
 */
class DetalleViewModelFactory(
    private val container: AppContainer,
    private val id: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        DetalleViewModel::class.java -> DetalleViewModel(container.productoRepository, id) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
