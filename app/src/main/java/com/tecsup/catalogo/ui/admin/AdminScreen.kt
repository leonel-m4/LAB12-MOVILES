package com.tecsup.catalogo.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.catalogo.CatalogoApp
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView

/**
 * Conecta el ViewModel del panel Admin con la pantalla y recoge su estado.
 */
@Composable
fun AdminRoute() {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) { AdminViewModelFactory(app.container) }
    val viewModel: AdminViewModel = viewModel(factory = factory)
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    AdminScreen(
        estado = estado,
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
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Panel Admin") })
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
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TarjetaTotal(
                            titulo = "Total guardado",
                            valor = estado.datos.totalGuardado,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaTotal(
                            titulo = "Total de favoritos",
                            valor = estado.datos.totalFavoritos,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSincronizar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sincronizar")
                    }
                    OutlinedButton(
                        onClick = onLimpiarCache,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar caché")
                    }
                }
            }
        }
    }
}

/** Tarjeta con un número grande y su título. */
@Composable
private fun TarjetaTotal(
    titulo: String,
    valor: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$valor",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
