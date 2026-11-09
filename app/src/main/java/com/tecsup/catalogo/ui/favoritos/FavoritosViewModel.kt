package com.tecsup.catalogo.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de favoritos.
 *
 * Observa los favoritos guardados en Room y los expone como [UiState].
 */
class FavoritosViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    val estado: StateFlow<UiState<List<Producto>>> =
        repository.favoritos
            .map<List<Producto>, UiState<List<Producto>>> { lista -> UiState.Exito(lista) }
            .catch { fallo ->
                emit(UiState.Error(fallo.message ?: "No se pudieron cargar los favoritos."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState.Cargando
            )

    /** Quita o agrega el producto de favoritos. */
    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }
}
