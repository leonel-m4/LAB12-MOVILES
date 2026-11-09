package com.tecsup.catalogo.model

/**
 * Estado genérico de una pantalla mientras carga datos.
 *
 * @param T tipo de dato que se muestra cuando la operación termina bien.
 */
sealed interface UiState<out T> {

    /** La pantalla está esperando la respuesta de la API o de Room. */
    data object Cargando : UiState<Nothing>

    /** La operación terminó y [datos] contiene lo que se debe mostrar. */
    data class Exito<T>(val datos: T) : UiState<T>

    /** La operación falló y [mensaje] explica el problema al usuario. */
    data class Error(val mensaje: String) : UiState<Nothing>
}
