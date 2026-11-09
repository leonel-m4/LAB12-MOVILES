package com.tecsup.catalogo.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

/**
 * Conecta el ViewModel de favoritos con la pantalla y recoge su estado.
 */
@Composable
fun FavoritosRoute(
    onProductoClick: (Int) -> Unit
) {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) {
        FavoritosViewModelFactory(app.container)
    }
    val viewModel: FavoritosViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    FavoritosScreen(
        estado = estado,
        onProductoClick = onProductoClick,
        onFavoritoClick = { producto -> viewModel.alternarFavorito(producto.id) }
    )
}

/**
 * Pantalla que muestra solo los productos marcados como favoritos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    estado: UiState<List<Producto>>,
    onProductoClick: (Int) -> Unit,
    onFavoritoClick: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Mis favoritos") })
        }
    ) { paddingValues ->
        when (estado) {
            UiState.Cargando -> {
                Cargando(modifier = Modifier.padding(paddingValues))
            }

            is UiState.Error -> {
                ErrorView(
                    mensaje = estado.mensaje,
                    onReintentar = {},
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    EstadoVacioView(
                        mensaje = "Todavía no tienes productos favoritos.\nToca el corazón de un smartphone para guardarlo aquí.",
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
