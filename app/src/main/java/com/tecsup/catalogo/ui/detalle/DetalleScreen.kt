package com.tecsup.catalogo.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.tecsup.catalogo.CatalogoApp
import com.tecsup.catalogo.domain.model.Producto
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView
import com.tecsup.catalogo.ui.components.Etiqueta
import com.tecsup.catalogo.ui.components.EtiquetaStock
import com.tecsup.catalogo.ui.components.Rating
import com.tecsup.catalogo.ui.components.coloresBarra
import com.tecsup.catalogo.ui.components.formatoPrecio

/**
 * Conecta el ViewModel de detalle con la pantalla y recoge su estado.
 */
@Composable
fun DetalleRoute(
    id: Int,
    onVolver: () -> Unit
) {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app, id) {
        DetalleViewModelFactory(app.container, id)
    }
    val viewModel: DetalleViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    DetalleScreen(
        estado = estado,
        onVolver = onVolver,
        onFavoritoClick = viewModel::alternarFavorito,
        onReintentar = viewModel::cargarDetalle
    )
}

/**
 * Pantalla con toda la información de un producto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    estado: UiState<Producto>,
    onVolver: () -> Unit,
    onFavoritoClick: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = coloresBarra()
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
                val producto = estado.datos
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Imagen grande sobre un fondo suave.
                    AsyncImage(
                        model = producto.imagen,
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Marca y categoría como etiquetas.
                        Row {
                            Etiqueta(
                                texto = producto.marca.ifBlank { "Sin marca" },
                                fondo = MaterialTheme.colorScheme.primaryContainer,
                                colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Etiqueta(
                                texto = producto.categoria,
                                fondo = MaterialTheme.colorScheme.surfaceVariant,
                                colorTexto = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatoPrecio(producto.precio),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Rating(producto.rating)
                            Spacer(modifier = Modifier.width(12.dp))
                            EtiquetaStock(producto.stock)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Descripción",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = producto.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        // Botón lleno para agregar; con borde si ya es favorito.
                        if (producto.favorito) {
                            OutlinedButton(
                                onClick = onFavoritoClick,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Quitar de favoritos")
                            }
                        } else {
                            Button(
                                onClick = onFavoritoClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar a favoritos")
                            }
                        }
                    }
                }
            }
        }
    }
}
