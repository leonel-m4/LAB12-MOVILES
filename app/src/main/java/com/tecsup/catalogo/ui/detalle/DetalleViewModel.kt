package com.tecsup.catalogo.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel del detalle de un producto.
 *
 * Pide el producto al repositorio con el segundo endpoint de la API y
 * expone el resultado como un [UiState] para la pantalla.
 */
class DetalleViewModel(
    private val repository: ProductoRepository,
    private val id: Int
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Producto>>(UiState.Cargando)
    val estado: StateFlow<UiState<Producto>> = _estado.asStateFlow()

    /** Los demás smartphones guardados en Room (sin el que se está viendo). */
    val otros: StateFlow<List<Producto>> =
        repository.items
            .map { lista -> lista.filter { it.id != id } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        cargarDetalle()
    }

    /** Consulta el detalle del producto y convierte el resultado en estado. */
    fun cargarDetalle() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repository.obtenerDetalle(id)
                .onSuccess { producto -> _estado.value = UiState.Exito(producto) }
                .onFailure { fallo ->
                    _estado.value = UiState.Error(
                        fallo.message ?: "No se pudo cargar el producto."
                    )
                }
        }
    }

    /** Invierte el favorito y vuelve a cargar el detalle para reflejarlo. */
    fun alternarFavorito() {
        viewModelScope.launch {
            repository.alternarFavorito(id)
            repository.obtenerDetalle(id)
                .onSuccess { producto -> _estado.value = UiState.Exito(producto) }
        }
    }
}
