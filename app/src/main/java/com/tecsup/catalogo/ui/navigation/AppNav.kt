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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.catalogo.CatalogoApp
import com.tecsup.catalogo.ui.detalle.DetalleRoute
import com.tecsup.catalogo.ui.favoritos.FavoritosRoute
import com.tecsup.catalogo.ui.lista.ListaRoute
import com.tecsup.catalogo.ui.login.LoginScreen
import com.tecsup.catalogo.ui.login.LoginViewModel
import com.tecsup.catalogo.ui.login.LoginViewModelFactory

/**
 * Navegación principal de la aplicación.
 *
 * Si no hay sesión muestra el login; si hay sesión muestra el catálogo con
 * la barra inferior (Catálogo y Favoritos) y la ruta de detalle.
 */
@Composable
fun AppNav() {
    val app = LocalContext.current.applicationContext as CatalogoApp
    val factory = remember(app) { LoginViewModelFactory(app.container) }
    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val sesion by loginViewModel.sesion.collectAsStateWithLifecycle()
    val estadoLogin by loginViewModel.estado.collectAsStateWithLifecycle()

    if (sesion == null) {
        LoginScreen(
            estado = estadoLogin,
            onIngresar = loginViewModel::iniciarSesion
        )
    } else {
        Catalogo(onCerrarSesion = loginViewModel::cerrarSesion)
    }
}

/**
 * Pantallas que se ven después de iniciar sesión.
 */
@Composable
private fun Catalogo(onCerrarSesion: () -> Unit) {
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
                ListaRoute(
                    onProductoClick = { id -> navController.navigate("detalle/$id") },
                    onCerrarSesion = onCerrarSesion
                )
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
private fun irA(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
