package com.tecsup.catalogo.data.notificacion

/**
 * Contrato para enviar notificaciones locales.
 *
 * La implementación concreta se agregará en la fase de notificaciones
 * usando NotificationManager, sin acoplar los ViewModels a Android.
 */
interface Notificador {

    /** Muestra una notificación local con [titulo] y [mensaje]. */
    fun notificar(titulo: String, mensaje: String)
}
