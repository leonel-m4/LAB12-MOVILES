package com.tecsup.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la lista de productos.
 *
 * Observa el repositorio y combina su lista con los indicadores de carga
 * y error para exponer un único [UiState] a la pantalla.
 */
class ListaViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val estado: StateFlow<UiState<List<Producto>>> =
        combine(repository.items, _cargando, _error) { productos, cargando, error ->
            when {
                cargando -> UiState.Cargando
                error != null && productos.isEmpty() -> UiState.Error(error)
                else -> UiState.Exito(productos)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Cargando
        )

    init {
        cargarProductos()
    }

    /** Solicita al repositorio que vuelva a consultar la API. */
    fun cargarProductos() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            repository.refrescar().onFailure { fallo ->
                _error.value = fallo.message ?: "No se pudo conectar con el servidor."
            }
            _cargando.value = false
        }
    }

    /** Invierte el estado de favorito del producto indicado. */
    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }
}
