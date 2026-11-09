package com.tecsup.catalogo

import android.app.Application

/**
 * Punto de entrada de la aplicación.
 *
 * Expone el contenedor de dependencias que usan las pantallas para
 * obtener los repositorios.
 */
class CatalogoApp : Application() {

    val container: AppContainer by lazy { AppContainer() }
}
