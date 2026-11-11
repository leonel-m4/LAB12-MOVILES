package com.tecsup.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la lista de productos.
 *
 * Observa el repositorio y combina su lista con los indicadores de carga
 * y error para exponer un único [UiState] a la pantalla.
 */
class ListaViewModel(
    private val repository: ProductoRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    /** true mientras se descarga de la API (para el gesto de deslizar). */
    val sincronizando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Marca elegida en el filtro (null = todas las marcas).
    private val _marca = MutableStateFlow<String?>(null)
    val marca: StateFlow<String?> = _marca.asStateFlow()

    /** Marcas que hay en Room, sin repetir y en orden alfabético. */
    val marcas: StateFlow<List<String>> =
        repository.items
            .map { lista -> lista.map { it.marca }.filter { it.isNotBlank() }.distinct().sorted() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val estado: StateFlow<UiState<List<Producto>>> =
        combine(repository.items, _cargando, _error, _busqueda, _marca) { productos, cargando, error, texto, marca ->
            when {
                // Si Room ya tiene datos, se muestran sin esperar a la API.
                cargando && productos.isEmpty() -> UiState.Cargando
                error != null && productos.isEmpty() -> UiState.Error(error)
                // Se filtra por nombre (sin importar mayúsculas) y por marca.
                else -> UiState.Exito(
                    productos.filter { producto ->
                        producto.nombre.contains(texto.trim(), ignoreCase = true) &&
                            (marca == null || producto.marca == marca)
                    }
                )
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
            repository.refrescar()
                .onSuccess { n ->
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { fallo ->
                    if (repository.cantidadGuardada() > 0) {
                        // Hay caché en Room: se sigue mostrando la lista con un aviso.
                        _aviso.value = "Sin conexión: mostrando datos guardados"
                    } else {
                        // Sin caché y sin internet: se muestra el error.
                        _error.value = fallo.message ?: "No se pudo conectar con el servidor."
                    }
                }
            _cargando.value = false
        }
    }

    /** Actualiza el texto con el que se filtra la lista por nombre. */
    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** Elige la marca del filtro; null muestra todas. */
    fun filtrarMarca(marca: String?) {
        _marca.value = marca
    }

    /** Invierte el estado de favorito del producto indicado. */
    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }
}
