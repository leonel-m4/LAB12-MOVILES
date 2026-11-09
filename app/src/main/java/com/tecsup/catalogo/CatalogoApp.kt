package com.tecsup.catalogo

import android.app.Application
import com.tecsup.catalogo.notification.NotificadorAndroid

/**
 * Punto de entrada de la aplicación.
 *
 * Expone el contenedor de dependencias que usan las pantallas para
 * obtener los repositorios y crea el canal de notificaciones.
 */
class CatalogoApp : Application() {

    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        NotificadorAndroid.crearCanal(this)
    }
}
