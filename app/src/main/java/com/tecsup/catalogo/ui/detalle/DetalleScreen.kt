package com.tecsup.catalogo.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.tecsup.catalogo.ui.components.PrecioConDescuento
import com.tecsup.catalogo.ui.components.EtiquetaStock
import com.tecsup.catalogo.ui.components.Rating
import com.tecsup.catalogo.ui.components.coloresBarra
import com.tecsup.catalogo.ui.components.formatoPrecio
import java.util.Locale

/**
 * Conecta el ViewModel de detalle con la pantalla y recoge su estado.
 */
@Composable
fun DetalleRoute(
    id: Int,
    onVolver: () -> Unit,
    onOtroClick: (Int) -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app, id) {
        DetalleViewModelFactory(app.container, id)
    }
    val viewModel: DetalleViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val otros by viewModel.otros.collectAsStateWithLifecycle()
    DetalleScreen(
        estado = estado,
        otros = otros,
        onVolver = onVolver,
        onFavoritoClick = viewModel::alternarFavorito,
        onReintentar = viewModel::cargarDetalle,
        onOtroClick = onOtroClick
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
    modifier: Modifier = Modifier,
    otros: List<Producto> = emptyList(),
    onOtroClick: (Int) -> Unit = {}
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
                    // Carrusel con las fotos grandes de la API.
                    CarruselFotos(
                        fotos = producto.imagenes.ifEmpty { listOf(producto.imagen) },
                        nombre = producto.nombre
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
                        // Precio con el descuento al lado.
                        PrecioConDescuento(
                            precio = producto.precio,
                            descuento = producto.descuento,
                            grande = true
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

                        Spacer(modifier = Modifier.height(12.dp))
                        TarjetaCaracteristicas(producto)

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

                        // Los demás smartphones, para seguir viendo sin volver a la lista.
                        if (otros.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Otros smartphones",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(items = otros, key = { it.id }) { otro ->
                                    TarjetaOtro(otro, onClick = { onOtroClick(otro.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta con las características del producto que trae la API
 * (segundo endpoint): disponibilidad, garantía, envío, medidas, etc.
 */
@Composable
private fun TarjetaCaracteristicas(producto: Producto) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Características",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            FilaCaracteristica("Garantía", producto.garantia)
            FilaCaracteristica("Envío", producto.envio)
            FilaCaracteristica("Devolución", producto.devolucion)
            FilaCaracteristica("Pedido mínimo", "${producto.pedidoMinimo} unidades")
            FilaCaracteristica("Peso", numero(producto.peso))
            FilaCaracteristica(
                "Medidas (ancho × alto × grosor)",
                "${numero(producto.ancho)} × ${numero(producto.alto)} × ${numero(producto.grosor)}"
            )
            FilaCaracteristica("SKU", producto.sku, ultima = true)
        }
    }
}

/** Una fila "nombre ........ valor" con una línea divisoria debajo. */
@Composable
private fun FilaCaracteristica(etiqueta: String, valor: String, ultima: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = valor.ifBlank { "No indicado" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
    if (!ultima) HorizontalDivider()
}

/** Muestra un número sin decimales de sobra: 2.0 -> "2", 5.29 -> "5.29". */
private fun numero(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString()
    else String.format(Locale.US, "%.2f", valor)

/** Tarjeta pequeña de otro smartphone: imagen, nombre y precio. */
@Composable
private fun TarjetaOtro(producto: Producto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(150.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatoPrecio(producto.precio * (1 - producto.descuento / 100)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Carrusel para deslizar entre las fotos grandes del producto.
 * Los puntos de abajo indican en qué foto se está.
 */
@Composable
private fun CarruselFotos(fotos: List<String>, nombre: String) {
    val pagina = rememberPagerState(pageCount = { fotos.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        HorizontalPager(state = pagina, modifier = Modifier.fillMaxSize()) { indice ->
            AsyncImage(
                model = fotos[indice],
                contentDescription = "$nombre, foto ${indice + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Puntos indicadores (solo si hay más de una foto).
        if (fotos.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(fotos.size) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == pagina.currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == pagina.currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }
    }
}
