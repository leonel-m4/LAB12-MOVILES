package com.tecsup.catalogo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.tecsup.catalogo.domain.model.Rol
import com.tecsup.catalogo.ui.admin.AdminRoute
import com.tecsup.catalogo.ui.detalle.DetalleRoute
import com.tecsup.catalogo.ui.favoritos.FavoritosRoute
import com.tecsup.catalogo.ui.lista.ListaRoute
import com.tecsup.catalogo.ui.login.LoginScreen
import com.tecsup.catalogo.ui.login.LoginViewModel
import com.tecsup.catalogo.ui.login.LoginViewModelFactory
import com.tecsup.catalogo.ui.login.RegistroScreen

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

    // Indica si se muestra el registro en lugar del login.
    var enRegistro by rememberSaveable { mutableStateOf(false) }

    if (sesion == null) {
        if (enRegistro) {
            RegistroScreen(
                estado = estadoLogin,
                onRegistrar = loginViewModel::registrar,
                onVolverALogin = {
                    loginViewModel.limpiarError()
                    enRegistro = false
                }
            )
        } else {
            LoginScreen(
                estado = estadoLogin,
                onIngresar = loginViewModel::iniciarSesion,
                onIrARegistro = {
                    loginViewModel.limpiarError()
                    enRegistro = true
                }
            )
        }
    } else {
        // Al entrar, la próxima vez que se cierre sesión se vuelve al login.
        LaunchedEffect(Unit) { enRegistro = false }
        Catalogo(
            esAdmin = sesion?.rol == Rol.ADMIN,
            onCerrarSesion = loginViewModel::cerrarSesion
        )
    }
}

/**
 * Pantallas que se ven después de iniciar sesión.
 */
@Composable
private fun Catalogo(
    esAdmin: Boolean,
    onCerrarSesion: () -> Unit
) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        bottomBar = {
            // La barra solo se muestra en las pantallas principales.
            if (rutaActual == "lista" || rutaActual == "favoritos" || rutaActual == "admin") {
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
                    // La pestaña Admin solo la ve el rol ADMIN.
                    if (esAdmin) {
                        NavigationBarItem(
                            selected = rutaActual == "admin",
                            onClick = { irA(navController, "admin") },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("Admin") }
                        )
                    }
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

            composable("admin") {
                if (esAdmin) {
                    AdminRoute()
                } else {
                    // Ruta protegida: un USUARIO vuelve al catálogo.
                    LaunchedEffect(Unit) {
                        navController.navigate("lista") {
                            popUpTo("lista") { inclusive = true }
                        }
                    }
                }
            }

            composable(
                route = "detalle/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleRoute(
                    id = id,
                    onVolver = { navController.popBackStack() },
                    // Al tocar otro smartphone se abre su detalle.
                    onOtroClick = { otroId -> navController.navigate("detalle/$otroId") }
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
