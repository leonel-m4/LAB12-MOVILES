package com.tecsup.catalogo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulCatalogo,
    onPrimary = Color.White,
    primaryContainer = AzulCatalogoClaro,
    onPrimaryContainer = AzulCatalogoTextoClaro,
    secondary = VerdeCatalogo,
    onSecondary = Color.White,
    secondaryContainer = VerdeCatalogoClaro,
    tertiary = AmbarCatalogo,
    tertiaryContainer = AmbarCatalogoClaro,
    // Fondo y superficies en blanco; los grises suaves separan las secciones.
    background = FondoClaro,
    onBackground = TextoPrincipalClaro,
    surface = Color.White,
    onSurface = TextoPrincipalClaro,
    surfaceVariant = SuperficieVarianteClara,
    onSurfaceVariant = GrisTextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = GrisContenedorClaro,
    surfaceContainerHighest = GrisContenedorClaro,
    outline = GrisBordeClaro,
    outlineVariant = GrisBordeClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulCatalogoOscuro,
    primaryContainer = AzulCatalogoContenedorOscuro,
    secondary = VerdeCatalogoOscuro,
    secondaryContainer = VerdeCatalogoContenedorOscuro,
    tertiary = AmbarCatalogoOscuro,
    background = FondoOscuro,
    surface = SuperficieOscura,
    surfaceVariant = SuperficieVarianteOscura
)

/**
 * Tema de CatalogoApp.
 *
 * Usa siempre los colores propios de la app (no los colores del fondo de
 * pantalla del celular), así se ve igual en todos los dispositivos.
 */
@Composable
fun CatalogoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
