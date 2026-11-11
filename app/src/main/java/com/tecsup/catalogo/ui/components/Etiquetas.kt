package com.tecsup.catalogo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

/*
 * Pequeñas piezas visuales que se repiten en varias pantallas.
 */

/** Etiqueta redondeada con fondo de color suave. */
@Composable
fun Etiqueta(
    texto: String,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = fondo,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Etiqueta de stock: verde si hay suficiente, ámbar si quedan pocos
 * y roja si está agotado.
 */
@Composable
fun EtiquetaStock(stock: Int, modifier: Modifier = Modifier) {
    when {
        stock == 0 -> Etiqueta(
            texto = "Agotado",
            fondo = MaterialTheme.colorScheme.errorContainer,
            colorTexto = MaterialTheme.colorScheme.onErrorContainer,
            modifier = modifier
        )
        stock < 10 -> Etiqueta(
            texto = "Quedan $stock",
            fondo = MaterialTheme.colorScheme.tertiaryContainer,
            colorTexto = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = modifier
        )
        else -> Etiqueta(
            texto = "En stock: $stock",
            fondo = MaterialTheme.colorScheme.secondaryContainer,
            colorTexto = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier
        )
    }
}

/** Estrella con el rating del producto, por ejemplo ★ 4.5. */
@Composable
fun Rating(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format(Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Precio con el formato S/ 0.00. */
fun formatoPrecio(precio: Double): String =
    "S/ ${String.format(Locale.US, "%.2f", precio)}"

/** Colores de la barra superior: fondo azul de la app y texto blanco. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun coloresBarra(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
)
