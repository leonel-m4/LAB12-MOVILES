package com.tecsup.catalogo.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.catalogo.ui.UiState

/**
 * Pantalla de inicio de sesión con correo y contraseña.
 *
 * Tiene un enlace para ir a la pantalla de registro.
 */
@Composable
fun LoginScreen(
    estado: UiState<Unit>,
    onIngresar: (correo: String, clave: String) -> Unit,
    modifier: Modifier = Modifier,
    onIrARegistro: () -> Unit = {}
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    val cargando = estado is UiState.Cargando

    // El Box centra el formulario; la columna hace scroll si aparece el teclado.
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoAuth(
                titulo = "CatalogoApp",
                subtitulo = "Inicia sesión para ver el catálogo de smartphones"
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = EspacioCampos
                ) {
                    CampoCorreo(valor = correo, onCambio = { correo = it }, habilitado = !cargando)
                    CampoClave(
                        valor = clave,
                        onCambio = { clave = it },
                        etiqueta = "Contraseña",
                        habilitado = !cargando,
                        ultimoCampo = true,
                        onListo = { onIngresar(correo, clave) }
                    )
                    if (estado is UiState.Error) {
                        MensajeError(estado.mensaje)
                    }
                    BotonPrincipal(
                        texto = "Ingresar",
                        cargando = cargando,
                        onClick = { onIngresar(correo, clave) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onIrARegistro, enabled = !cargando) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
