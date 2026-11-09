package com.tecsup.catalogo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Totales que muestra el panel de administración.
 */
data class ResumenAdmin(
    val totalGuardado: Int,
    val totalFavoritos: Int
)

/**
 * ViewModel del panel Admin.
 *
 * Carga los totales de Room y permite sincronizar o limpiar la caché.
 */
class AdminViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<ResumenAdmin>>(UiState.Cargando)
    val estado: StateFlow<UiState<ResumenAdmin>> = _estado.asStateFlow()

    init {
        cargarTotales()
    }

    /** Lee de Room el total guardado y el total de favoritos. */
    fun cargarTotales() {
        viewModelScope.launch {
            _estado.value = UiState.Exito(
                ResumenAdmin(
                    totalGuardado = repository.cantidadGuardada(),
                    totalFavoritos = repository.cantidadFavoritos()
                )
            )
        }
    }

    /** Vuelve a descargar de la API (conserva favoritos) y actualiza los totales. */
    fun sincronizar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repository.refrescar()
                .onSuccess { cargarTotales() }
                .onFailure { fallo ->
                    _estado.value = UiState.Error(
                        fallo.message ?: "No se pudo sincronizar."
                    )
                }
        }
    }

    /** Borra la caché de Room y deja los totales en 0. */
    fun limpiarCache() {
        viewModelScope.launch {
            repository.limpiarCache()
            cargarTotales()
        }
    }
}
