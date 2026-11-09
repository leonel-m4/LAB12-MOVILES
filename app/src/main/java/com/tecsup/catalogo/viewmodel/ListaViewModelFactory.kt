package com.tecsup.catalogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.catalogo.data.repository.ProductoRepository

/**
 * Fábrica de ViewModels que recibe el repositorio desde el contenedor.
 */
class ListaViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        ListaViewModel::class.java -> ListaViewModel(repository) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
