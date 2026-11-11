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
import androidx.compose.material3.MaterialTheme
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
 * Pantalla para crear una cuenta nueva con correo y contraseña.
 *
 * La cuenta se crea en Firebase Authentication y entra con rol USUARIO.
 */
@Composable
fun RegistroScreen(
    estado: UiState<Unit>,
    onRegistrar: (correo: String, clave: String, confirmar: String) -> Unit,
    onVolverALogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
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
                titulo = "Crear cuenta",
                subtitulo = "Regístrate para ver el catálogo de smartphones"
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
                        habilitado = !cargando
                    )
                    CampoClave(
                        valor = confirmar,
                        onCambio = { confirmar = it },
                        etiqueta = "Confirmar contraseña",
                        habilitado = !cargando,
                        ultimoCampo = true,
                        onListo = { onRegistrar(correo, clave, confirmar) }
                    )
                    Text(
                        text = "Mínimo 6 caracteres.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (estado is UiState.Error) {
                        MensajeError(estado.mensaje)
                    }
                    BotonPrincipal(
                        texto = "Registrarme",
                        cargando = cargando,
                        onClick = { onRegistrar(correo, clave, confirmar) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onVolverALogin, enabled = !cargando) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
