# CatalogoApp — Lab Integrador 11-12

Aplicación Android en Kotlin + Jetpack Compose para consultar **smartphones** de
[DummyJSON](https://dummyjson.com), guardar favoritos, funcionar sin conexión,
iniciar sesión con Firebase y mostrar un panel de administración.

> **Estado actual (08/11/2026):**
> - **Leonel:** H1–H6 + documentación (8 commits).
> - **Karim:** correcciones de estructura + H7–H11 (6 commits).
> - **Pendiente:** H12–H16, Fase 2 (`mejora-ia`) y entrega final.
>
> La app ya compila y funciona: lista desde Room, detalle, favoritos y modo sin
> conexión. Este README es la guía de trabajo. En H16 se convierte en el README
> final de la entrega: se agregan las capturas y se completa el reparto de tareas.

---

## 0. Pendientes para Karim (empezar aquí)

### Código: H12–H16

- [ ] **H12 — Configurar Firebase**
  - Crear el proyecto `CatalogoApp Tecsup` (ID sugerido: `catalogoapp-tecsup`).
  - Registrar la app Android con el paquete **`com.tecsup.catalogo`**.
  - Descargar `google-services.json` y colocarlo en `app/` (no commitearlo).
  - Agregar plugin de Google Services y dependencias de Auth, Firestore y
    `kotlinx-coroutines-play-services`.
- [ ] **H13 — Login con Firebase Auth**
  - `FirebaseAuthRepository` con `iniciarSesion`, `sesionActual` y `cerrarSesion`.
  - `LoginScreen`, `LoginViewModel` y factory en `ui/login/`.
  - Sesión persistente y botón **Cerrar sesión**.
  - **No programar registro**: los usuarios los crea el docente.
  - Rol desde Firestore: `usuarios/{uid}` → `rol: "admin"` o `"usuario"`.
- [ ] **H14 — Roles y panel Admin**
  - `AdminScreen` + `AdminViewModel` en `ui/admin/`.
  - Mostrar **total guardado** y **total de favoritos**.
  - Botones **Sincronizar** y **Limpiar caché**.
  - Pestaña Admin visible solo para `Rol.ADMIN`; proteger la ruta.
- [ ] **H15 — Notificación local**
  - `NotificadorAndroid` con canal `catalogo_canal`.
  - Permiso `POST_NOTIFICATIONS` en el manifest y solicitud en Android 13+.
  - Notificación **al terminar de sincronizar**: «Se guardaron N elementos».
  - Al tocarla debe abrir la app; si se niega el permiso, no debe crashear.
- [ ] **H16 — README final**
  - Capturas de login, lista, detalle, favoritos, Admin y notificación.
  - Reparto de tareas real y cómo crear los usuarios de prueba.

### Fase 2 (`mejora-ia`)

- [ ] Al menos **3 commits** en `mejora-ia`.
- [ ] **R12:** mínimo **3 pruebas unitarias** de `ListaViewModel` con repositorio
  falso y `Notificador` falso.
- [ ] **R13:** `PROMPTS.md` con las consultas a IA.
- [ ] Pull Request de `mejora-ia` hacia `main`.

> La rama `mejora-ia` ya existe en GitHub, pero está al mismo commit que `main`
> (`73f7a2b`). Termina primero H12–H16 en `main`; después actualízala con
> `git switch mejora-ia && git merge main` antes de empezar la Fase 2.

### Pruebas y entrega

- [ ] Ejecutar **PA-01 a PA-14**, sobre todo login, roles, Admin, notificación y
  modo avión.
- [ ] Tag `v1.0` y APK debug adjunto en el Release de GitHub.
- [ ] Repositorio privado con el docente como colaborador.
- [ ] Renombrar el repo a `lab-integrador-gXX-apellido1-apellido2`.
- [ ] Verificar que no se suban `google-services.json`, claves ni contraseñas.
- [ ] Clean + Rebuild antes del APK final.

### Datos que faltan confirmar

- Número de grupo y apellidos para el nombre del repositorio.
- Usuario de GitHub del docente.
- Correos de los usuarios de prueba (`admin` y `usuario`).
- Crear en Firestore la colección `usuarios` con el campo `rol` de cada usuario.

---

## 1. Resumen del proyecto

| Dato | Valor |
|---|---|
| Tema | Productos |
| Variante | Smartphones |
| API | `https://dummyjson.com/` |
| Lista | `GET /products/category/smartphones` |
| Detalle | `GET /products/{id}` |
| Paquete | `com.tecsup.catalogo` |
| App | `CatalogoApp` |
| Repositorio | `https://github.com/leonel-m4/LAB12-MOVILES` (ver sección 8) |

Tecnologías: Kotlin, Jetpack Compose + Material 3, MVVM, Retrofit +
kotlinx.serialization, Room, Firebase Auth + Firestore, Coil y notificaciones
locales.

---

## 2. Puesta en marcha

### 2.1 Requisitos

- Android Studio con JDK 25 (el proyecto incluye
  `gradle/gradle-daemon-jvm.properties` y lo solicita).
- Un dispositivo o emulador con Android 8.0 (API 24) o superior. Para probar
  Firebase se recomienda un emulador **con Google Play**.
- `local.properties` con la ruta del SDK. **No se sube al repositorio.**

### 2.2 Clonar y compilar

```bash
git clone https://github.com/leonel-m4/LAB12-MOVILES.git
cd LAB12-MOVILES
.\gradlew.bat assembleDebug
```

El APK de depuración queda en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 2.3 Identidad de Git

Cada integrante debe commitear con **su propia cuenta**. Si usas la PC de
Leonel, configura la identidad solo para este repositorio:

```bash
git config user.name "Tu Nombre"
git config user.email "tu-correo@ejemplo.com"
```

Comprueba con:

```bash
git config user.name
git config user.email
git shortlog -sn
```

---

## 3. Estructura obligatoria (PDF, sección 5)

La estructura de carpetas es **obligatoria**. Al terminar debe verse así
(adaptada al tema Productos):

```text
app/src/main/java/com/tecsup/catalogo/
├── MainActivity.kt
├── AppContainer.kt                 ← crea API, base de datos y repositorios
├── CatalogoApp.kt
├── data/
│   ├── remote/
│   │   ├── ApiService.kt           ← interface Retrofit (@GET)
│   │   ├── RetrofitClient.kt
│   │   └── dto/ProductoDto.kt      ← lo que responde la API
│   ├── local/
│   │   ├── ProductoEntity.kt       ← tabla de Room
│   │   ├── ProductoDao.kt
│   │   └── AppDatabase.kt
│   ├── auth/
│   │   ├── AuthRepository.kt       ← interface
│   │   └── FirebaseAuthRepository.kt
│   ├── repository/
│   │   ├── ProductoRepository.kt   ← interface
│   │   └── ProductoRepositoryImpl.kt
│   └── mapper/Mappers.kt           ← Dto → Entity → modelo
├── domain/model/
│   ├── Producto.kt                 ← modelo que usa la UI
│   └── Sesion.kt                   ← Sesion + enum Rol
├── notification/
│   ├── Notificador.kt              ← interface
│   └── NotificadorAndroid.kt       ← implementación con NotificationCompat
└── ui/
    ├── navigation/AppNav.kt
    ├── UiState.kt
    ├── login/
    │   ├── LoginScreen.kt
    │   └── LoginViewModel.kt
    ├── lista/
    │   ├── ListaScreen.kt
    │   ├── ListaViewModel.kt
    │   └── ListaViewModelFactory.kt
    ├── detalle/
    │   ├── DetalleScreen.kt
    │   └── DetalleViewModel.kt
    ├── favoritos/
    │   ├── FavoritosScreen.kt
    │   └── FavoritosViewModel.kt
    ├── admin/
    │   ├── AdminScreen.kt
    │   └── AdminViewModel.kt
    ├── components/
    │   ├── Cargando.kt
    │   ├── ErrorView.kt
    │   ├── EstadoVacioView.kt
    │   └── ProductoCard.kt
    └── theme/
```

Reglas MVVM que se revisan en la rúbrica:

- Las pantallas de `ui/` solo observan el estado con
  `collectAsStateWithLifecycle()` y llaman funciones del ViewModel.
- **Prohibido** usar Retrofit, Room o Firebase dentro de un composable.
- El ViewModel no recibe `Context` ni importa clases de vista. La notificación se
  pide mediante la interfaz `Notificador`.
- El repositorio es una **interface**. El ViewModel depende de la interfaz,
  nunca de la clase concreta.
- Hay tres modelos distintos: `ProductoDto` (API), `ProductoEntity` (Room) y
  `Producto` (UI). Los mappers los convierten.
- **Room es la fuente única de verdad:** la lista que ve el usuario sale siempre
  de Room; Retrofit solo la actualiza.
- Cada ViewModel expone un `StateFlow<UiState<T>>` (`Cargando`, `Exito`,
  `Error`) y nadie más lo modifica.
- Los ViewModel se crean con un `ViewModelProvider.Factory` que recibe el
  `AppContainer`.

---

## 4. Correcciones de estructura (ya aplicadas ✅)

Karim aplicó estas correcciones en el commit `f3b6e0a`
(`refactor: alinear estructura y contratos con el PDF`). Se dejan como
referencia; **no hay que repetirlas**. El código de H1–H6 ya cumple la estructura
y los contratos del PDF.

### 4.1 Mover archivos a la ruta obligatoria

| Archivo actual | Ruta obligatoria |
|---|---|
| `model/Producto.kt` | `domain/model/Producto.kt` |
| `model/Sesion.kt` | `domain/model/Sesion.kt` |
| `model/Rol.kt` | Se elimina: el enum pasa a `domain/model/Sesion.kt` |
| `model/UiState.kt` | `ui/UiState.kt` |
| `data/repository/AuthRepository.kt` | `data/auth/AuthRepository.kt` |
| `data/notificacion/Notificador.kt` | `notification/Notificador.kt` |
| `data/remote/dto/ProductoMapper.kt` | `data/mapper/Mappers.kt` |
| `ui/screens/ListaScreen.kt` | `ui/lista/ListaScreen.kt` |
| `viewmodel/ListaViewModel.kt` | `ui/lista/ListaViewModel.kt` |
| `viewmodel/ListaViewModelFactory.kt` | `ui/lista/ListaViewModelFactory.kt` |
| `ui/components/CargandoView.kt` | `ui/components/Cargando.kt` |

Además:

- Renombra la función `CargandoView` a `Cargando`.
- Actualiza todos los `import` y el `package` de cada archivo movido.
- `MainActivity` debe importar `com.tecsup.catalogo.ui.lista.ListaRoute`.
- Borra las carpetas `model/`, `viewmodel/`, `ui/screens/` y
  `data/notificacion/` cuando queden vacías.
- Crea las carpetas `data/local/`, `ui/navigation/`, `ui/login/`,
  `ui/detalle/`, `ui/favoritos/` y `ui/admin/` cuando las necesites en los
  siguientes hitos.

### 4.2 Contratos exactos

Los nombres de métodos y propiedades **no se cambian**: el docente revisa todos
los proyectos con el mismo criterio. Copia estos contratos tal cual.

`domain/model/Sesion.kt`:

```kotlin
package com.tecsup.catalogo.domain.model

enum class Rol { ADMIN, USUARIO }

data class Sesion(
    val uid: String,
    val correo: String,
    val rol: Rol
)
```

`ui/UiState.kt`:

```kotlin
package com.tecsup.catalogo.ui

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}
```

`data/auth/AuthRepository.kt`:

```kotlin
package com.tecsup.catalogo.data.auth

import com.tecsup.catalogo.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?   // null si no hay sesión
    fun cerrarSesion()
}
```

`data/repository/ProductoRepository.kt`:

```kotlin
package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    val items: Flow<List<Producto>>          // sale de Room
    val favoritos: Flow<List<Producto>>      // sale de Room
    suspend fun refrescar(): Result<Int>     // Retrofit -> Room; cuántos guardó
    suspend fun obtenerDetalle(id: Int): Result<Producto>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}
```

`notification/Notificador.kt`:

```kotlin
package com.tecsup.catalogo.notification

interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
```

### 4.3 Ajustes que se desprenden de los contratos

1. **`Rol.CLIENTE` ya no existe.** Usa `Rol.USUARIO` en todo el proyecto.
2. **`AuthRepository` ya no tiene `observarSesion()` ni `registrar()`.** La
   sesión se consulta con `sesionActual()` y se cierra con `cerrarSesion()`.
   El registro no se programa: el docente crea los usuarios de prueba en
   Firebase (ver sección 7.1).
3. **`ProductoRepositoryImpl` debe adaptarse al contrato** aunque todavía use
   memoria:
   - `items` y `favoritos` son propiedades `Flow`.
   - `refrescar()` devuelve `Result<Int>` con la cantidad de productos
     descargados.
   - `obtenerDetalle(id)` devuelve `Result<Producto>`.
   - `alternarFavorito(id)` invierte el estado (no recibe un `Boolean`).
   - `cantidadGuardada()` y `cantidadFavoritos()` devuelven los totales.
   - `limpiarCache()` vacía la lista y los favoritos en memoria.
4. **`ListaViewModel` debe usar los nombres nuevos:**
   `repo.items`, `repo.refrescar()` y `repo.alternarFavorito(id)`.
5. **`ListaViewModelFactory` debe recibir `AppContainer`**, no el repositorio:

```kotlin
class ListaViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        ListaViewModel::class.java -> ListaViewModel(container.productoRepository) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
```

   En `ListaRoute`, crea la fábrica con `ListaViewModelFactory(app.container)`.
6. **`Producto` sigue igual** (id, nombre, descripcion, precio, marca,
   categoria, stock, imagen, rating, favorito). Solo cambia su paquete a
   `domain.model`.

### 4.4 Verificación y commit (hecho ✅)

```bash
.\gradlew.bat assembleDebug
git add .
git commit -m "refactor: alinear estructura y contratos con el PDF"
git push
```

Criterios de aceptación (cumplidos):

- El proyecto compila.
- La app sigue mostrando los 16 smartphones.
- No queda ningún archivo en `model/`, `viewmodel/` ni `ui/screens/`.
- `git grep "Rol.CLIENTE"` no devuelve nada.
- `git grep "observarProductos\|refrescarProductos\|cambiarFavorito\|notificar("` no devuelve nada.

---

## 5. Hitos H7–H16

### Estado de los hitos

| Hito | Estado |
|---|---|
| H7 — Pantalla de detalle | ✅ Hecho por Karim |
| H8 — Base de datos Room | ✅ Hecho por Karim |
| H9 — Room como fuente de verdad | ✅ Hecho por Karim |
| H10 — Favoritos | ✅ Hecho por Karim |
| H11 — Modo sin conexión | ✅ Hecho por Karim |
| **H12 — Configurar Firebase** | ⏳ **Pendiente** |
| **H13 — Login con Firebase Auth** | ⏳ **Pendiente** |
| **H14 — Roles y panel Admin** | ⏳ **Pendiente** |
| **H15 — Notificación local** | ⏳ **Pendiente** |
| **H16 — README final** | ⏳ **Pendiente** |

Reglas para todos los hitos:

- **Un commit = una idea.** No mezcles Room con Firebase.
- **Cada commit debe compilar** antes de subirse.
- Mensajes en español: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`.
- Antes de commitear, revisa el cambio y asegúrate de poder explicarlo.

### H7 — `feat: pantalla de detalle`

**Objetivo:** tocar una tarjeta y ver el detalle del producto usando el segundo
endpoint.

**Archivos:**

- `ui/navigation/AppNav.kt`
- `ui/detalle/DetalleScreen.kt`
- `ui/detalle/DetalleViewModel.kt`
- `ui/detalle/DetalleViewModelFactory.kt`

**Pasos:**

1. Crea un `NavHost` con las rutas `lista` y `detalle/{id}`.
2. En `lista`, muestra `ListaRoute(onProductoClick = { id ->
   navController.navigate("detalle/$id") })`.
3. En `detalle/{id}`, recibe el id con `NavType.IntType`.
4. `DetalleViewModel` recibe `ProductoRepository` y expone
   `StateFlow<UiState<Producto>>`. En `init`, llama a
   `repository.obtenerDetalle(id)` y convierte el `Result` a `Exito` o `Error`.
5. `DetalleScreen` muestra imagen, marca, nombre, precio, rating, stock,
   descripción y botón de favorito.
6. El botón de favorito llama a `repository.alternarFavorito(id)` y vuelve a
   cargar el detalle (o combina con `repository.items` para reflejar el cambio).
7. Agrega un `TopAppBar` con flecha de regreso
   (`navController.popBackStack()`).
8. `MainActivity` debe mostrar `AppNav()` en lugar de `ListaRoute()`.

**Criterios de aceptación:**

- Tocar una tarjeta abre el detalle correcto.
- La flecha de regreso vuelve a la lista.
- El corazón cambia en la lista y en el detalle.
- Al rotar la pantalla no se pierde el producto.
- El detalle usa `GET /products/{id}` (no solo los datos de la lista).

**Commit:** `feat: pantalla de detalle`

---

### H8 — `feat: base de datos Room`

**Objetivo:** crear la base de datos local que después será la fuente de verdad.

**Dependencias:**

En `gradle/libs.versions.toml` agrega la versión de KSP que coincide con Kotlin
2.2.10:

```toml
ksp = "2.2.10-2.0.2"
```

Y el plugin:

```toml
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

En el `build.gradle.kts` de la raíz:

```kotlin
alias(libs.plugins.ksp) apply false
```

En `app/build.gradle.kts`:

```kotlin
alias(libs.plugins.ksp)
```

Y las dependencias:

```kotlin
implementation("androidx.room:room-runtime:2.8.5")
implementation("androidx.room:room-ktx:2.8.5")
ksp("androidx.room:room-compiler:2.8.5")
```

**Archivos:**

- `data/local/ProductoEntity.kt`
- `data/local/ProductoDao.kt`
- `data/local/AppDatabase.kt`
- `data/mapper/Mappers.kt` (agrega los mappers de Room)

**Pasos:**

1. `ProductoEntity` tiene los mismos campos que `Producto`:

```kotlin
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val marca: String,
    val categoria: String,
    val stock: Int,
    val imagen: String,
    val rating: Double,
    val favorito: Boolean = false
)
```

2. `ProductoDao` debe exponer como mínimo:

```kotlin
@Query("SELECT * FROM productos ORDER BY nombre")
fun todos(): Flow<List<ProductoEntity>>

@Query("SELECT * FROM productos WHERE favorito = 1 ORDER BY nombre")
fun favoritos(): Flow<List<ProductoEntity>>

@Query("SELECT id FROM productos WHERE favorito = 1")
suspend fun idsFavoritos(): List<Int>

@Query("SELECT COUNT(*) FROM productos")
suspend fun contar(): Int

@Query("SELECT COUNT(*) FROM productos WHERE favorito = 1")
suspend fun contarFavoritos(): Int

@Query("SELECT * FROM productos WHERE id = :id")
suspend fun obtenerPorId(id: Int): ProductoEntity?

@Upsert
suspend fun guardarTodos(productos: List<ProductoEntity>)

@Query("UPDATE productos SET favorito = NOT favorito WHERE id = :id")
suspend fun alternarFavorito(id: Int)

@Query("DELETE FROM productos")
suspend fun borrarTodo()
```

3. `AppDatabase`:

```kotlin
@Database(
    entities = [ProductoEntity::class],
    version = 1,
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
                ).build().also { instancia = it }
            }
    }
}
```

4. En `data/mapper/Mappers.kt` agrega `ProductoEntity.toModelo()` y
   `ProductoDto.toEntity()`.

**Criterios de aceptación:**

- El proyecto compila con KSP.
- La base de datos se crea sin errores.
- Todavía no es necesario conectar el repositorio; eso es H9.

**Commit:** `feat: base de datos Room`

---

### H9 — `refactor: Room como fuente de verdad`

**Objetivo:** la lista se lee de Room; la API solo actualiza Room.

**Pasos:**

1. `ProductoRepositoryImpl` ahora recibe `ApiService` y `ProductoDao`.
2. `AppContainer` crea la base de datos con `context` y pasa el DAO:

```kotlin
class AppContainer(context: Context) {
    private val db = AppDatabase.obtener(context)
    private val api = RetrofitClient.api

    val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(api, db.productoDao())
    }
}
```

   `CatalogoApp` debe pasar `applicationContext`:

```kotlin
class CatalogoApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
```

3. Implementa el contrato exacto:

```kotlin
class ProductoRepositoryImpl(
    private val api: ApiService,
    private val dao: ProductoDao
) : ProductoRepository {

    override val items = dao.todos().map { lista ->
        lista.map { it.toModelo() }
    }

    override val favoritos = dao.favoritos().map { lista ->
        lista.map { it.toModelo() }
    }

    override suspend fun refrescar(): Result<Int> = try {
        val idsFavoritos = dao.idsFavoritos().toSet()
        val nuevos = api.obtenerSmartphones().products
            .map { it.toEntity(favorito = it.id in idsFavoritos) }
        dao.guardarTodos(nuevos)
        Result.success(nuevos.size)
    } catch (e: IOException) {
        Result.failure(e)          // sin internet
    } catch (e: HttpException) {
        Result.failure(e)          // 404, 500...
    }

    override suspend fun obtenerDetalle(id: Int): Result<Producto> = try {
        // R04/PA-06: el detalle sale del segundo endpoint.
        val dto = api.obtenerProducto(id)
        val local = dao.obtenerPorId(id)
        val entity = dto.toEntity(favorito = local?.favorito ?: false)
        dao.guardarTodos(listOf(entity))   // conserva el favorito
        Result.success(entity.toModelo())
    } catch (e: Exception) {
        // Sin internet: si está en Room, se muestra lo guardado.
        val local = dao.obtenerPorId(id)
        if (local != null) Result.success(local.toModelo())
        else Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
    override suspend fun limpiarCache() = dao.borrarTodo()
}
```

4. **Conserva los favoritos al sincronizar** (PA-10). El código anterior ya lo
   hace con `idsFavoritos` y `copy(favorito = ...)`.

**Criterios de aceptación:**

- Al cerrar y volver a abrir la app, la lista sigue visible sin esperar a la
  API.
- En modo avión se ven los productos guardados.
- Un favorito sobrevive a un refresh y a un reinicio.
- Si la API falla y hay caché, no se pierde la lista.
- `obtenerDetalle` ya no depende solo de la lista: usa Room o el endpoint de
  detalle.

**Commit:** `refactor: Room como fuente de verdad`

---

### H10 — `feat: favoritos`

**Objetivo:** pantalla o pestaña para ver solo los favoritos.

**Archivos:**

- `ui/favoritos/FavoritosScreen.kt`
- `ui/favoritos/FavoritosViewModel.kt`
- `ui/favoritos/FavoritosViewModelFactory.kt`
- Actualizar `ui/navigation/AppNav.kt`

**Pasos:**

1. Agrega una `NavigationBar` con dos destinos: **Catálogo** y **Favoritos**.
2. `FavoritosViewModel` observa `repository.favoritos` y expone
   `StateFlow<UiState<List<Producto>>>`.
3. Reutiliza `ProductoCard`, `Cargando`, `ErrorView` y `EstadoVacioView`.
4. Al tocar una tarjeta de favoritos también se abre el detalle.
5. El corazón llama a `repository.alternarFavorito(id)` y quita el producto de
   la lista.
6. Si no hay favoritos, muestra el estado vacío con un mensaje claro.

**Criterios de aceptación:**

- Marcar un producto lo hace aparecer en Favoritos.
- Desmarcarlo lo quita.
- Los favoritos siguen ahí después de cerrar la app.
- Si no hay favoritos, se muestra el estado vacío.

**Commit:** `feat: favoritos`

---

### H11 — `feat: modo sin conexion`

**Objetivo:** avisar cuando no hay internet y seguir mostrando la caché de Room.

**Pasos:**

1. En `ListaViewModel` agrega:

```kotlin
private val _aviso = MutableStateFlow<String?>(null)
val aviso: StateFlow<String?> = _aviso.asStateFlow()
```

2. Cuando `refrescar()` falle y `cantidadGuardada() > 0`, mantén
   `UiState.Exito` y publica:

```kotlin
_aviso.value = "Sin conexión: mostrando datos guardados"
```

3. Solo muestra `UiState.Error` si no hay ningún dato guardado.
4. En `ListaScreen`, muestra un banner o `Snackbar` con `aviso`. Agrega un botón
   o gesto para reintentar.
5. Opcional: crea `data/network/ConectividadObserver.kt` con
   `ConnectivityManager` y `callbackFlow` para detectar el regreso de la
   conexión.

**Criterios de aceptación:**

- Activar modo avión: la lista sigue visible y aparece el aviso.
- Desactivar modo avión: el refresh vuelve a funcionar.
- Sin caché y sin internet: se muestra `ErrorView` con **Reintentar**.
- Al girar la pantalla no se pierde el aviso ni se vuelve a descargar.

**Commit:** `feat: modo sin conexion`

---

### H12 — `chore: configurar Firebase`

**Objetivo:** dejar Firebase listo para Auth y Firestore.

**Pasos:**

1. En Firebase Console crea un proyecto (o usa el que comparta Leonel).
2. Registra una app Android con el paquete **`com.tecsup.catalogo`**.
3. Descarga `google-services.json` y colócalo en `app/`. **No lo commitees**:
   ya está en `.gitignore`.

> **Consola nueva de Firebase (rediseño de octubre 2026):** la pestaña
> **Reglas** ya no aparece en la vista de **Datos**. El editor está en
> **Bases de datos y almacenamiento → Firestore → Seguridad → Reglas**, o
> directamente en:
>
> `https://console.firebase.google.com/project/catalogoapp-e7bc0/firestore/databases/-default-/security/rules`
>
> El proyecto de este laboratorio ya tiene `firebase.json`, `firestore.rules` y
> `.firebaserc`. Para publicar las reglas sin entrar a la consola:
>
> ```bash
> npx -y firebase-tools@latest login
> npx -y firebase-tools@latest deploy --only firestore:rules --project catalogoapp-e7bc0
> ```
>
> Si `firebase login` pide un código, abre la URL que imprime, inicia sesión con
> la cuenta dueña del proyecto y pega el código en la terminal.
4. Agrega el plugin y las dependencias:

```kotlin
// build.gradle.kts (raíz)
id("com.google.gms.google-services") version "4.5.0" apply false

// app/build.gradle.kts
id("com.google.gms.google-services")
implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
```

5. En `AppContainer` crea las instancias de `FirebaseAuth` y `FirebaseFirestore`.
6. Verifica que `git status` no muestre el JSON ni claves.

**Criterios de aceptación:**

- El proyecto compila con tu `google-services.json` local.
- La app abre sin errores.
- `git status` no muestra el JSON ni claves.

**Commit:** `chore: configurar Firebase`

> Si no tienes el JSON, pídeselo a Leonel por un medio privado. Nunca lo subas
> al repositorio.

---

### H13 — `feat: login con Firebase Auth`

**Objetivo:** iniciar y cerrar sesión con los usuarios que crea el docente. **No
se programa registro.**

**Archivos:**

- `data/auth/FirebaseAuthRepository.kt`
- `ui/login/LoginScreen.kt`
- `ui/login/LoginViewModel.kt`
- `ui/login/LoginViewModelFactory.kt`
- Actualizar `ui/navigation/AppNav.kt`

**Pasos:**

1. Implementa el contrato exacto de `AuthRepository`:

```kotlin
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user!!.uid
        Result.success(leerSesion(uid, correo))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sesionActual(): Sesion? {
        val u = auth.currentUser ?: return null
        return leerSesion(u.uid, u.email.orEmpty())
    }

    override fun cerrarSesion() = auth.signOut()

    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }
}
```

   `.await()` necesita `kotlinx-coroutines-play-services` (H12).

2. El rol **no se decide en la app**: se lee de Firestore en
   `usuarios/{uid}`, campo `rol` con valor `"admin"` o `"usuario"`.
3. `LoginViewModel` expone `sesion: StateFlow<Sesion?>` y un
   `estado: StateFlow<UiState<Unit>>` para carga y error. En `init`, llama a
   `sesionActual()`; si hay sesión, publica la sesión y `AppNav` muestra el
   catálogo.
4. `LoginScreen` tiene campos de correo y contraseña, indicador de carga,
   mensajes de error y botón **Ingresar**. No agregues pestaña de registro.
5. Valida antes de llamar a Firebase (PA-04): correo vacío o inválido,
   contraseña vacía o menor a 6 caracteres.
6. En `AppNav`, si no hay sesión muestra `LoginScreen`; si hay sesión muestra el
   catálogo.
7. Agrega un botón **Cerrar sesión** que llame a `cerrarSesion()` y regrese al
   login (PA-12).

**Criterios de aceptación:**

- Un usuario creado por el docente puede iniciar sesión.
- Puede cerrar sesión y volver al login.
- La sesión se mantiene al cerrar y abrir la app.
- Correo inválido, campos vacíos o contraseña incorrecta muestran un mensaje
  claro y no llaman a Firebase cuando la validación falla.
- El rol se lee de Firestore, no está escrito en el código.

**Commit:** `feat: login con Firebase Auth`

---

### H14 — `feat: roles y panel Admin`

**Objetivo:** que el rol `ADMIN` vea un panel con totales y acciones.

**Archivos:**

- `ui/admin/AdminScreen.kt`
- `ui/admin/AdminViewModel.kt`
- `ui/admin/AdminViewModelFactory.kt`
- Actualizar `ui/navigation/AppNav.kt`

**Pasos:**

1. `AdminViewModel` recibe `ProductoRepository` y expone
   `StateFlow<UiState<ResumenAdmin>>`. `ResumenAdmin` puede ser:

```kotlin
data class ResumenAdmin(
    val totalGuardado: Int,
    val totalFavoritos: Int
)
```

2. Carga los totales con `cantidadGuardada()` y `cantidadFavoritos()`.
3. `AdminScreen` muestra:
   - **Total guardado**
   - **Total de favoritos**
   - Botón **Sincronizar** → `repository.refrescar()` y actualiza los totales.
   - Botón **Limpiar caché** → `repository.limpiarCache()` y deja los totales
     en 0.
4. En la `NavigationBar`, muestra el destino **Admin** solo si
   `sesion.rol == Rol.ADMIN`.
5. Protege la ruta: si un `Rol.USUARIO` intenta entrar, regrésalo al catálogo.
6. Para probar, el docente cambia manualmente en Firestore el campo `rol` del
   usuario a `"admin"` o `"usuario"`.

**Criterios de aceptación:**

- Un `Rol.USUARIO` no ve el panel Admin.
- Un `Rol.ADMIN` sí lo ve y puede entrar.
- El panel muestra los dos totales.
- **Sincronizar** vuelve a descargar y mantiene los favoritos.
- **Limpiar caché** deja la lista vacía y los totales en 0 (PA-11).
- El rol se mantiene al reiniciar la sesión.

**Commit:** `feat: roles y panel Admin`

---

### H15 — `feat: notificacion local`

**Objetivo:** mostrar la notificación **después de sincronizar**, con el texto
«Se guardaron N elementos», y manejar el permiso.

**Archivos:**

- `notification/NotificadorAndroid.kt`
- `res/drawable/ic_notificacion.xml`
- Actualizar `AndroidManifest.xml`, `MainActivity` y `ListaViewModel`

**Pasos:**

1. Implementa `Notificador` con `NotificationManager` y
   `NotificationCompat.Builder`:

```kotlin
class NotificadorAndroid(
    private val context: Context
) : Notificador {

    override fun mostrar(titulo: String, texto: String) {
        val manager = NotificationManagerCompat.from(context)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return // permiso negado: no crashea
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notificacion)
    }

    companion object {
        const val CANAL = "catalogo_canal"

        fun crearCanal(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canal = NotificationChannel(
                    CANAL,
                    "Catálogo",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                context.getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(canal)
            }
        }
    }
}
```

2. Crea el canal en `CatalogoApp.onCreate()` o en `MainActivity`.
3. Declara en el manifest:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

4. En Android 13 o superior, solicita el permiso en `MainActivity` con
   `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`.
5. **Dispara la notificación al terminar de sincronizar**, dentro del
   `onSuccess` de `refrescar()` en `ListaViewModel`:

```kotlin
.onSuccess { n ->
    _aviso.value = null
    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
}
```

   `ListaViewModel` recibe `Notificador` por constructor (a través de la
   fábrica y del `AppContainer`).
6. Si el permiso está denegado, la app no debe crashear: solo no muestra la
   notificación (PA-14).
7. Al tocar la notificación se abre la app (PA-05).

> **Corrección importante:** la notificación **no** se dispara al marcar un
> favorito ni desde un botón «Probar notificación». El PDF exige que sea al
> terminar de sincronizar y con el texto «Se guardaron N elementos».

**Criterios de aceptación:**

- La primera vez se pide el permiso.
- Si se acepta, al sincronizar aparece «Se guardaron N elementos».
- Si se niega, la app sigue funcionando.
- Al tocar la notificación se abre la app.
- El canal `catalogo_canal` aparece en los ajustes de notificaciones.

**Commit:** `feat: notificacion local`

---

### H16 — `docs: README`

**Objetivo:** convertir este archivo en el README final de la entrega.

Debe incluir:

1. Descripción de la app, tema (Productos) y variante (Smartphones).
2. Tecnologías: Kotlin, Compose, MVVM, Retrofit, Room, Firebase, Coil.
3. Árbol de paquetes final.
4. Diagrama de la arquitectura MVVM (puede ser texto o Mermaid).
5. Capturas reales de: **login, lista, detalle, favoritos, panel Admin y
   notificación**. Guárdalas en `docs/capturas/` y enlázalas aquí.
6. **Reparto de tareas:** Leonel H1–H6 + guía; compañero correcciones + H7–H16.
7. Cómo ejecutar el proyecto (Android Studio, JDK, `local.properties`,
   Firebase).
8. Cómo crear los usuarios de prueba y cómo asignar el rol `admin` en Firestore.
9. Conclusiones o sección «Para pensar».

**Criterios de aceptación:**

- El README se entiende sin abrir el código.
- Las capturas son de la app real.
- El reparto de tareas coincide con los commits.

**Commit:** `docs: README`

---

## 6. Fase 2 con IA (rama `mejora-ia`)

Cuando `main` esté completo:

```bash
git switch -c mejora-ia
git push -u origin mejora-ia
```

Haz al menos **3 commits** con mejoras reales, por ejemplo:

1. `test: pruebas unitarias de ListaViewModel`
2. `feat: busqueda de productos por nombre`
3. `docs: registrar prompts de IA`

### 6.1 R12 — Pruebas unitarias de `ListaViewModel`

Mínimo **3 pruebas** con un repositorio falso y un `Notificador` falso. Deben
estar en `app/src/test/java/...` y ejecutarse con:

```bash
.\gradlew.bat testDebugUnitTest
```

Casos mínimos:

1. **Sincronización exitosa:** `refrescar()` devuelve `Result.success(16)`; el
   estado pasa a `UiState.Exito` con los productos y el notificador recibe
   «Se guardaron 16 elementos».
2. **Sin conexión con caché:** `refrescar()` devuelve `Result.failure(...)`
   pero `cantidadGuardada() > 0`; el estado sigue en `UiState.Exito` y
   `aviso` indica «Sin conexión: mostrando datos guardados».
3. **Sin conexión sin caché:** `refrescar()` falla y `cantidadGuardada() == 0`;
   el estado pasa a `UiState.Error`.
4. **Alternar favorito:** llamar a `alternarFavorito(id)` invoca al repositorio
   falso con el id correcto.

Dependencias de test:

```kotlin
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
```

Recuerda usar `Dispatchers.setMain(StandardTestDispatcher())` porque
`viewModelScope` usa el dispatcher principal.

> R12 pertenece a la **Fase 2**: las pruebas se agregan en `mejora-ia`, no en
> `main`.

### 6.2 R13 — `PROMPTS.md`

Crea `PROMPTS.md` con una entrada por consulta a la IA:

```markdown
## Consulta 1
- Fecha:
- Herramienta:
- Prompt:
- Qué respondió (resumen):
- Qué usamos / qué cambiamos y por qué:
- Qué aprendimos:
```

Registra qué pediste, qué aceptaste, qué rechazaste y qué aprendiste. Al
terminar, abre el Pull Request de `mejora-ia` hacia `main` y describe los
cambios.

---

## 7. Pruebas de aceptación (PA-01 a PA-14)

Se prueban en el emulador con Google Play durante la sustentación. Marca cada
una antes de entregar.

| Código | Caso | Resultado esperado |
|---|---|---|
| PA-01 | Ingresar con el usuario `usuario`. | Entra al catálogo. No ve el panel Admin. |
| PA-02 | Ingresar con el usuario `admin`. | Entra al catálogo y ve el acceso al panel Admin. |
| PA-03 | Contraseña incorrecta. | Mensaje de error claro. No entra. |
| PA-04 | Dejar correo o clave vacíos. | Muestra validación. No llama a Firebase. |
| PA-05 | Primera apertura con internet. | Se ve Cargando, luego la lista y la notificación «Se guardaron N elementos». |
| PA-06 | Tocar un elemento. | Muestra el detalle con datos del segundo endpoint. |
| PA-07 | Marcar un favorito y cerrar la app. | Al reabrir sigue en Favoritos. |
| PA-08 | Modo avión con datos guardados. | La lista se ve y aparece el aviso de sin conexión. |
| PA-09 | Modo avión sin datos (app recién instalada). | Pantalla de error con botón Reintentar. |
| PA-10 | Sincronizar después de marcar un favorito. | El favorito no se pierde. |
| PA-11 | Admin pulsa Limpiar caché. | La lista queda vacía y los totales en 0. Al sincronizar vuelven los datos. |
| PA-12 | Cerrar sesión y reabrir la app. | Vuelve al login y pide credenciales. |
| PA-13 | Girar la pantalla. | No se pierde el estado ni se vuelve a descargar. |
| PA-14 | Negar el permiso de notificaciones. | La app funciona normal, sin avisos. |

### 7.1 Usuarios de prueba

El docente crea los usuarios en Firebase Console. No se programa registro.

1. Firebase Console → **Authentication** → **Users** → **Add user**.
2. Crea un usuario `usuario@…` y otro `admin@…` con contraseñas seguras.
3. En **Firestore** crea la colección `usuarios`.
4. Para cada usuario, crea el documento `usuarios/{uid}` con:

```text
correo: "usuario@…"     rol: "usuario"
correo: "admin@…"       rol: "admin"
```

5. El campo `rol` debe ser exactamente `"admin"` o `"usuario"` (minúsculas).

### 7.2 Reglas de Firestore

Sin estas reglas la app recibe `403 PERMISSION_DENIED` y el login no entra.
Pégalas en **Firestore → Seguridad → Reglas** (consola nueva) y pulsa
**Publicar**:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    // Cada usuario solo puede leer y escribir su propio documento de rol.
    match /usuarios/{uid} {
      allow read: if request.auth != null && request.auth.uid == uid;
      allow write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

El archivo `firestore.rules` del repositorio contiene exactamente estas reglas.
También se pueden publicar con:

```bash
npx -y firebase-tools@latest deploy --only firestore:rules --project catalogoapp-e7bc0
```

---

## 8. Git, ramas y entrega

### 8.1 Reglas

- Repositorio **privado**, con el docente como colaborador.
- Ramas: `main` (Fase 1) y `mejora-ia` (Fase 2).
- **Mínimo 15 commits en `main` y 6 por integrante.** Verifica con
  `git shortlog -sn`.
- **Mínimo 3 commits en `mejora-ia`** más `PROMPTS.md`.
- Cada integrante commitea desde su propia cuenta.
- No se sube: `build/`, `.idea/`, `local.properties`, `google-services.json`,
  keystores ni contraseñas.
- Mensajes en español con el formato `tipo: qué se hizo`.

### 8.2 Entrega final

- [ ] 15+ commits en `main` y 6+ por integrante.
- [ ] 3+ commits en `mejora-ia` y `PROMPTS.md`.
- [ ] README final con reparto de tareas y capturas.
- [ ] Pull Request de `mejora-ia` hacia `main` con descripción.
- [ ] Tag `v1.0`:

```bash
git tag -a v1.0 -m "Version 1.0"
git push origin v1.0
```

- [ ] APK debug adjunto en el Release de GitHub.
- [ ] Sin `google-services.json`, `local.properties`, keystores ni contraseñas.
- [ ] Ambos integrantes pueden explicar cualquier archivo.
- [ ] Clean + Rebuild antes de generar el APK final.

### 8.3 Nombre del repositorio

El PDF pide el formato:

```text
lab-integrador-gXX-apellido1-apellido2
```

El repositorio actual se llama `LAB12-MOVILES`. Antes de la entrega, renómbralo
en GitHub (**Settings → Rename**) con el número de grupo y los apellidos de
ambos integrantes. GitHub redirige la URL anterior, pero actualiza el enlace en
el aula virtual.

---

## 9. Preguntas de sustentación para practicar

- ¿Por qué el repositorio es una interfaz y no una clase concreta?
- ¿Por qué la lista sale de Room y no directamente de Retrofit?
- ¿Qué pasa con los favoritos cuando sincronizas? Muéstralo en el código.
- ¿Dónde se decide el rol de la persona y por qué no en la pantalla?
- ¿Qué diferencia hay entre `ProductoDto`, `ProductoEntity` y `Producto`?
- ¿Para qué sirve `UiState`?
- ¿Qué pasa si se niega el permiso de notificaciones?
- Muestra un commit tuyo y explica qué cambiaste.
- ¿Cómo funciona el modo sin conexión?
- ¿Qué harías si la API cambia el nombre de un campo?

---

## 10. Datos pendientes de completar

Estos datos los deben confirmar el equipo antes de la entrega. **No escribas
contraseñas ni claves en este archivo.**

| Dato | Valor |
|---|---|
| Nombre del segundo integrante | Karim Sovero |
| Usuario de GitHub del segundo integrante | `karimSOVC` |
| Número de grupo | _pendiente_ |
| Apellidos para el nombre del repositorio | _pendiente_ |
| Usuario de GitHub del docente | _pendiente_ |
| Proyecto de Firebase | `CatalogoApp Tecsup` (ID sugerido: `catalogoapp-tecsup`) |
| Correos de los usuarios de prueba | _pendiente_ |

---

## 11. Reparto de tareas

| Integrante | Hitos | Commits |
|---|---|---|
| Leonel | H1–H6 + README/guía | 8 |
| Karim | Correcciones + H7–H11 (hechos) + H12–H16 (pendientes) | 6 hechos; 11 al terminar |

> Actualiza esta tabla en H16 con los nombres reales y verifica que coincida con
> `git shortlog -sn`.
