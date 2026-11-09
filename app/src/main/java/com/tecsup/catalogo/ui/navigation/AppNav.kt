package com.tecsup.catalogo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.catalogo.ui.detalle.DetalleRoute
import com.tecsup.catalogo.ui.favoritos.FavoritosRoute
import com.tecsup.catalogo.ui.lista.ListaRoute

/**
 * Navegación principal de la aplicación.
 *
 * Tiene una barra inferior con Catálogo y Favoritos, y la ruta de detalle
 * que recibe el id del producto.
 */
@Composable
fun AppNav() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        bottomBar = {
            // La barra solo se muestra en las pantallas principales.
            if (rutaActual == "lista" || rutaActual == "favoritos") {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == "lista",
                        onClick = { irA(navController, "lista") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == "favoritos",
                        onClick = { irA(navController, "favoritos") },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text("Favoritos") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            composable("lista") {
                ListaRoute(onProductoClick = { id ->
                    navController.navigate("detalle/$id")
                })
            }

            composable("favoritos") {
                FavoritosRoute(onProductoClick = { id ->
                    navController.navigate("detalle/$id")
                })
            }

            composable(
                route = "detalle/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleRoute(
                    id = id,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}

/** Cambia de pestaña sin apilar pantallas repetidas. */
private fun irA(navController: androidx.navigation.NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
