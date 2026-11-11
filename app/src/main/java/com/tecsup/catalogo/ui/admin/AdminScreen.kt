package com.tecsup.catalogo.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.catalogo.CatalogoApp
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView
import com.tecsup.catalogo.ui.components.coloresBarra

/**
 * Conecta el ViewModel del panel Admin con la pantalla y recoge su estado.
 */
@Composable
fun AdminRoute() {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) { AdminViewModelFactory(app.container) }
    val viewModel: AdminViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    AdminScreen(
        estado = estado,
        mensaje = mensaje,
        onMensajeMostrado = viewModel::mensajeMostrado,
        onSincronizar = viewModel::sincronizar,
        onLimpiarCache = viewModel::limpiarCache,
        onReintentar = viewModel::cargarTotales
    )
}

/**
 * Panel de administración con totales y acciones sobre la caché.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    estado: UiState<ResumenAdmin>,
    onSincronizar: () -> Unit,
    onLimpiarCache: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    mensaje: String? = null,
    onMensajeMostrado: () -> Unit = {}
) {
    val snackbar = remember { SnackbarHostState() }
    var confirmarLimpieza by rememberSaveable { mutableStateOf(false) }

    // Cuando el ViewModel publica un mensaje, se muestra una sola vez.
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbar.showSnackbar(mensaje)
            onMensajeMostrado()
        }
    }

    // Confirmación antes de borrar, para no limpiar la caché por error.
    if (confirmarLimpieza) {
        AlertDialog(
            onDismissRequest = { confirmarLimpieza = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("¿Limpiar la caché?") },
            text = {
                Text("Se borrarán los productos guardados en el celular y sus favoritos. Podrás volver a descargarlos con Sincronizar.")
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarLimpieza = false
                    onLimpiarCache()
                }) {
                    Text("Sí, limpiar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarLimpieza = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(title = { Text(text = "Panel Admin") }, colors = coloresBarra())
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumen de datos guardados",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TarjetaTotal(
                            titulo = "Total guardado",
                            valor = estado.datos.totalGuardado,
                            icono = Icons.Default.ShoppingCart,
                            fondo = MaterialTheme.colorScheme.primaryContainer,
                            colorTexto = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaTotal(
                            titulo = "Total de favoritos",
                            valor = estado.datos.totalFavoritos,
                            icono = Icons.Default.Favorite,
                            fondo = MaterialTheme.colorScheme.errorContainer,
                            colorTexto = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Acciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onSincronizar,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sincronizar")
                    }
                    OutlinedButton(
                        onClick = { confirmarLimpieza = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Limpiar caché")
                    }
                }
            }
        }
    }
}

/** Tarjeta con ícono, un número grande y su título. */
@Composable
private fun TarjetaTotal(
    titulo: String,
    valor: Int,
    icono: ImageVector,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondo, contentColor = colorTexto)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$valor",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
