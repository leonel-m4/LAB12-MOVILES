package com.tecsup.catalogo.ui.lista

import com.tecsup.catalogo.data.repository.ProductoRepository
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Pruebas unitarias de [ListaViewModel] con un repositorio falso y un
 * notificador falso, sin Android, sin red y sin Room.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // viewModelScope usa el dispatcher principal.
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun sincronizacionExitosa_muestraProductosYNotifica() = runTest(dispatcher) {
        val repo = RepositorioFalso(
            productosIniciales = productosDePrueba(16),
            resultadoRefrescar = Result.success(16)
        )
        val notificador = NotificadorFalso()
        val viewModel = ListaViewModel(repo, notificador)
        backgroundScope.launch { viewModel.estado.collect {} }

        advanceUntilIdle()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals(16, (estado as UiState.Exito).datos.size)
        assertEquals("Se guardaron 16 elementos", notificador.ultimoTexto)
        assertNull(viewModel.aviso.value)
    }

    @Test
    fun sinConexionConCache_mantieneExitoYMuestraAviso() = runTest(dispatcher) {
        val repo = RepositorioFalso(
            productosIniciales = productosDePrueba(16),
            resultadoRefrescar = Result.failure(IOException("sin internet")),
            cantidad = 16
        )
        val notificador = NotificadorFalso()
        val viewModel = ListaViewModel(repo, notificador)
        backgroundScope.launch { viewModel.estado.collect {} }

        advanceUntilIdle()

        assertTrue(viewModel.estado.value is UiState.Exito)
        assertEquals("Sin conexión: mostrando datos guardados", viewModel.aviso.value)
        assertNull(notificador.ultimoTexto)
    }

    @Test
    fun sinConexionSinCache_muestraError() = runTest(dispatcher) {
        val repo = RepositorioFalso(
            productosIniciales = emptyList(),
            resultadoRefrescar = Result.failure(IOException("sin internet")),
            cantidad = 0
        )
        val viewModel = ListaViewModel(repo, NotificadorFalso())
        backgroundScope.launch { viewModel.estado.collect {} }

        advanceUntilIdle()

        assertTrue(viewModel.estado.value is UiState.Error)
    }

    @Test
    fun alternarFavorito_llamaAlRepositorioConElIdCorrecto() = runTest(dispatcher) {
        val repo = RepositorioFalso(
            productosIniciales = productosDePrueba(3),
            resultadoRefrescar = Result.success(3)
        )
        val viewModel = ListaViewModel(repo, NotificadorFalso())

        viewModel.alternarFavorito(2)
        advanceUntilIdle()

        assertEquals(2, repo.ultimoIdAlternado)
    }

    /** Crea [cantidad] productos de prueba con ids del 1 en adelante. */
    private fun productosDePrueba(cantidad: Int): List<Producto> =
        (1..cantidad).map { id ->
            Producto(
                id = id,
                nombre = "Smartphone $id",
                descripcion = "Descripción $id",
                precio = 100.0 * id,
                marca = "Marca",
                categoria = "smartphones",
                stock = 10,
                imagen = "",
                rating = 4.5
            )
        }
}

/**
 * Repositorio falso: devuelve lo que se le indica y guarda el último id
 * que se marcó como favorito.
 */
private class RepositorioFalso(
    productosIniciales: List<Producto>,
    private val resultadoRefrescar: Result<Int>,
    private val cantidad: Int = productosIniciales.size
) : ProductoRepository {

    private val lista = MutableStateFlow(productosIniciales)
    var ultimoIdAlternado: Int? = null

    override val items: Flow<List<Producto>> = lista
    override val favoritos: Flow<List<Producto>> = lista.map { it.filter { p -> p.favorito } }
    override suspend fun refrescar(): Result<Int> = resultadoRefrescar
    override suspend fun obtenerDetalle(id: Int): Result<Producto> =
        Result.success(lista.value.first { it.id == id })
    override suspend fun alternarFavorito(id: Int) {
        ultimoIdAlternado = id
    }
    override suspend fun cantidadGuardada(): Int = cantidad
    override suspend fun cantidadFavoritos(): Int = 0
    override suspend fun limpiarCache() {
        lista.value = emptyList()
    }
}

/** Notificador falso que recuerda el último texto que se pidió mostrar. */
private class NotificadorFalso : Notificador {
    var ultimoTexto: String? = null

    override fun mostrar(titulo: String, texto: String) {
        ultimoTexto = texto
    }
}
