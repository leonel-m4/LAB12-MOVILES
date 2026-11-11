package com.tecsup.catalogo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local de la aplicación.
 *
 * Se crea una sola instancia para toda la app.
 */
@Database(
    entities = [ProductoEntity::class],
    version = 3, // 2: columnas de características · 3: fotos grandes
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "catalogo.db"
                )
                    // Room solo guarda una copia de la API: si cambia la versión,
                    // se borra la tabla y se vuelve a descargar al sincronizar.
                    .fallbackToDestructiveMigration(true)
                    .build().also { instancia = it }
            }
    }
}
