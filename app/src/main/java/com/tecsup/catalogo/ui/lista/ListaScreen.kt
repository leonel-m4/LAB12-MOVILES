package com.tecsup.catalogo.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.tecsup.catalogo.ui.components.coloresBarra
import com.tecsup.catalogo.ui.theme.CatalogoAppTheme
import kotlinx.coroutines.launch

/**
 * Conecta el ViewModel de la lista con la pantalla y recoge su estado.
 */
@Composable
fun ListaRoute(
    onProductoClick: (Int) -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) {
        ListaViewModelFactory(app.container)
    }
    val viewModel: ListaViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val sincronizando by viewModel.sincronizando.collectAsStateWithLifecycle()
    val marcas by viewModel.marcas.collectAsStateWithLifecycle()
    val marca by viewModel.marca.collectAsStateWithLifecycle()
    ListaScreen(
        estado = estado,
        aviso = aviso,
        busqueda = busqueda,
        sincronizando = sincronizando,
        marcas = marcas,
        marcaElegida = marca,
        onMarcaClick = viewModel::filtrarMarca,
        onBuscar = viewModel::buscar,
        onProductoClick = onProductoClick,
        onFavoritoClick = { producto -> viewModel.alternarFavorito(producto.id) },
        onReintentar = viewModel::cargarProductos,
        onCerrarSesion = onCerrarSesion
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
    modifier: Modifier = Modifier,
    aviso: String? = null,
    onCerrarSesion: () -> Unit = {},
    busqueda: String = "",
    onBuscar: (String) -> Unit = {},
    sincronizando: Boolean = false,
    marcas: List<String> = emptyList(),
    marcaElegida: String? = null,
    onMarcaClick: (String?) -> Unit = {}
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Catálogo de smartphones")
                        if (estado is UiState.Exito) {
                            Text(
                                text = "${estado.datos.size} productos",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
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
                Column(modifier = Modifier.padding(paddingValues)) {
                    // Aviso de modo sin conexión con opción de reintentar.
                    if (aviso != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aviso,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                TextButton(onClick = onReintentar) {
                                    Text(text = "Reintentar")
                                }
                            }
                        }
                    }

                    // Buscador por nombre.
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = onBuscar,
                        placeholder = { Text("Buscar por nombre") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (busqueda.isNotEmpty()) {
                                IconButton(onClick = { onBuscar("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    )

                    // Filtro por marca: "Todas" y una opción por cada marca.
                    if (marcas.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = marcaElegida == null,
                                    onClick = { onMarcaClick(null) },
                                    label = { Text("Todas") }
                                )
                            }
                            items(marcas) { marca ->
                                FilterChip(
                                    selected = marcaElegida == marca,
                                    onClick = { onMarcaClick(marca) },
                                    label = { Text(marca) }
                                )
                            }
                        }
                    }

                    // Deslizar hacia abajo vuelve a sincronizar con la API.
                    PullToRefreshBox(
                        isRefreshing = sincronizando,
                        onRefresh = onReintentar,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (estado.datos.isEmpty()) {
                            EstadoVacioView(
                                mensaje = if (busqueda.isBlank() && marcaElegida == null) {
                                    "No hay productos disponibles por ahora.\nDesliza hacia abajo para sincronizar."
                                } else {
                                    "No hay smartphones que coincidan con tu búsqueda."
                                }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(items = estado.datos, key = { it.id }) { producto ->
                                    ProductoCard(
                                        producto = producto,
                                        onClick = { onProductoClick(producto.id) },
                                        onFavoritoClick = {
                                            onFavoritoClick(producto)
                                            // Mensaje corto para confirmar la acción.
                                            val texto = if (producto.favorito) {
                                                "Quitado de favoritos"
                                            } else {
                                                "Agregado a favoritos"
                                            }
                                            scope.launch {
                                                snackbar.currentSnackbarData?.dismiss()
                                                snackbar.showSnackbar(texto, duration = SnackbarDuration.Short)
                                            }
                                        }
                                    )
                                }
                            }
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
                        stock = 5,
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
