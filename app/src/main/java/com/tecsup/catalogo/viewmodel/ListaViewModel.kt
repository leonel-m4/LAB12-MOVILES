package com.tecsup.catalogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.model.Producto
import com.tecsup.catalogo.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la lista de productos.
 *
 * Por ahora trabaja con una lista de prueba para validar la interfaz.
 * En el siguiente hito se conectará al repositorio que consulta la API.
 */
class ListaViewModel : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Producto>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Producto>>> = _estado.asStateFlow()

    init {
        cargarProductos()
    }

    /** Vuelve a cargar la lista y muestra el indicador de progreso. */
    fun cargarProductos() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            delay(500)
            _estado.value = UiState.Exito(productosDePrueba())
        }
    }

    /** Cambia el estado de favorito del producto indicado. */
    fun cambiarFavorito(id: Int) {
        val estadoActual = _estado.value
        if (estadoActual is UiState.Exito) {
            _estado.value = UiState.Exito(
                estadoActual.datos.map { producto ->
                    if (producto.id == id) {
                        producto.copy(favorito = !producto.favorito)
                    } else {
                        producto
                    }
                }
            )
        }
    }

    private fun productosDePrueba(): List<Producto> = listOf(
        Producto(
            id = 121,
            nombre = "iPhone 5s",
            descripcion = "Smartphone compacto con un diseño clásico de Apple.",
            precio = 199.99,
            marca = "Apple",
            categoria = "smartphones",
            stock = 25,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/iphone-5s/thumbnail.webp",
            rating = 2.83
        ),
        Producto(
            id = 123,
            nombre = "iPhone 13 Pro",
            descripcion = "Equipo con cámara avanzada y pantalla de alta gama.",
            precio = 1099.99,
            marca = "Apple",
            categoria = "smartphones",
            stock = 56,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/iphone-13-pro/thumbnail.webp",
            rating = 4.12
        ),
        Producto(
            id = 125,
            nombre = "Oppo A57",
            descripcion = "Smartphone de gama media con un diseño delgado.",
            precio = 249.99,
            marca = "Oppo",
            categoria = "smartphones",
            stock = 19,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/oppo-a57/thumbnail.webp",
            rating = 3.94
        ),
        Producto(
            id = 128,
            nombre = "Realme C35",
            descripcion = "Equipo económico pensado para el uso diario.",
            precio = 149.99,
            marca = "Realme",
            categoria = "smartphones",
            stock = 48,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/realme-c35/thumbnail.webp",
            rating = 4.2
        ),
        Producto(
            id = 133,
            nombre = "Samsung Galaxy S10",
            descripcion = "Pantalla AMOLED y cámara versátil de gama alta.",
            precio = 699.99,
            marca = "Samsung",
            categoria = "smartphones",
            stock = 19,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/samsung-galaxy-s10/thumbnail.webp",
            rating = 3.06
        ),
        Producto(
            id = 136,
            nombre = "Vivo X21",
            descripcion = "Smartphone con lector de huellas en pantalla.",
            precio = 499.99,
            marca = "Vivo",
            categoria = "smartphones",
            stock = 7,
            imagen = "https://cdn.dummyjson.com/product-images/smartphones/vivo-x21/thumbnail.webp",
            rating = 4.26
        )
    )
}
