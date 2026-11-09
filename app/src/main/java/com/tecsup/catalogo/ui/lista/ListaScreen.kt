package com.tecsup.catalogo.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.catalogo.CatalogoApp
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView
import com.tecsup.catalogo.ui.components.EstadoVacioView
import com.tecsup.catalogo.ui.components.ProductoCard
import com.tecsup.catalogo.ui.theme.CatalogoAppTheme

/**
 * Conecta el ViewModel de la lista con la pantalla y recoge su estado.
 */
@Composable
fun ListaRoute(
    onProductoClick: (Int) -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) {
        ListaViewModelFactory(app.container)
    }
    val viewModel: ListaViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    ListaScreen(
        estado = estado,
        onProductoClick = onProductoClick,
        onFavoritoClick = { producto -> viewModel.alternarFavorito(producto.id) },
        onReintentar = viewModel::cargarProductos
    )
}

/**
 * Pantalla principal del catálogo.
 *
 * Es una función sin estado propio: recibe [estado] y las acciones para que
 * pueda probarse y reutilizarse con datos distintos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    estado: UiState<List<Producto>>,
    onProductoClick: (Int) -> Unit,
    onFavoritoClick: (Producto) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Catálogo de smartphones") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when (estado) {
            UiState.Cargando -> {
                Cargando(modifier = Modifier.padding(paddingValues))
            }

            is UiState.Error -> {
                ErrorView(
                    mensaje = estado.mensaje,
                    onReintentar = onReintentar,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    EstadoVacioView(
                        mensaje = "No hay productos disponibles por ahora.",
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = estado.datos, key = { it.id }) { producto ->
                            ProductoCard(
                                producto = producto,
                                onClick = { onProductoClick(producto.id) },
                                onFavoritoClick = { onFavoritoClick(producto) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaScreenPreview() {
    CatalogoAppTheme {
        ListaScreen(
            estado = UiState.Exito(
                datos = listOf(
                    Producto(
                        id = 1,
                        nombre = "iPhone 13 Pro",
                        descripcion = "Equipo con cámara avanzada.",
                        precio = 1099.99,
                        marca = "Apple",
                        categoria = "smartphones",
                        stock = 56,
                        imagen = "",
                        rating = 4.12,
                        favorito = true
                    ),
                    Producto(
                        id = 2,
                        nombre = "Samsung Galaxy S10",
                        descripcion = "Pantalla AMOLED de gama alta.",
                        precio = 699.99,
                        marca = "Samsung",
                        categoria = "smartphones",
                        stock = 19,
                        imagen = "",
                        rating = 3.06
                    )
                )
            ),
            onProductoClick = {},
            onFavoritoClick = {},
            onReintentar = {}
        )
    }
}
