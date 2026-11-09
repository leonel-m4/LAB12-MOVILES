package com.tecsup.catalogo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.catalogo.ui.detalle.DetalleRoute
import com.tecsup.catalogo.ui.lista.ListaRoute

/**
 * Navegación principal de la aplicación.
 *
 * Define la ruta de la lista y la ruta de detalle que recibe el id.
 */
@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            ListaRoute(onProductoClick = { id ->
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
