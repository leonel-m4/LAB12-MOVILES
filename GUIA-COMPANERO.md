# Guía de trabajo — CatalogoApp (Lab 11-12)

> **Archivo temporal.** Esta guía es para el segundo integrante del equipo.
> Cuando el proyecto esté terminado, bórrala con `git rm GUIA-COMPANERO.md`
> y haz el commit `docs: eliminar guia temporal`. No debe quedar en la entrega final.

## 1. Estado actual

Leonel ya completó los hitos H1–H6 en `main`:

| # | Commit | Contenido |
|---|---|---|
| 1 | `chore: crear proyecto base con Compose` | Proyecto Android, tema, manifest, `.gitignore` |
| 2 | `chore: crear estructura MVVM y contratos` | `Producto`, `Sesion`, `Rol`, `UiState`, repositorios, `Notificador` |
| 3 | `feat: agregar componentes de la lista` | `ProductoCard`, `CargandoView`, `ErrorView`, `EstadoVacioView` |
| 4 | `feat: lista con datos de prueba` | `ListaScreen` y `ListaViewModel` |
| 5 | `feat: ApiService y RetrofitClient` | DTOs, mapper, Retrofit + kotlinx.serialization |
| 6 | `feat: la lista consume la API` | `ProductoRepositoryImpl`, `AppContainer`, `CatalogoApp` |

La app ya muestra los **16 smartphones** de DummyJSON (`https://dummyjson.com/products/category/smartphones`).
Los favoritos todavía viven en memoria: se pierden al cerrar la app. Eso se corrige en H8–H10.

## 2. Puesta en marcha

### Si usas tu propia PC

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
git clone https://github.com/leonel-m4/LAB12-MOVILES.git
```

### Si usas la PC de Leonel

Configura tu identidad **solo para este repositorio** (no toques la global):

```bash
git config user.name "Tu Nombre"
git config user.email "tu-correo@ejemplo.com"
git pull
```

Abre la carpeta en Android Studio, espera el *Gradle Sync* y compila:

```bash
.\gradlew.bat assembleDebug
```

> **JDK:** el proyecto incluye `gradle/gradle-daemon-jvm.properties`, que pide JDK 25.
> Si Gradle lo solicita, instálalo o deja que lo descargue automáticamente.

## 3. Reglas de los commits

- Todos los commits deben salir con **tu** `user.name` y `user.email`.
- Mínimo **9 commits** tuyos en `main` (H7–H16). Leonel ya tiene 6; el total mínimo es 15.
- **Un commit = una idea.** No mezcles Room con Firebase en el mismo commit.
- **Cada commit debe compilar** antes de subirse.
- Nunca subas `google-services.json`, `local.properties`, keystores ni contraseñas.
- Mensajes en español, con el estilo del proyecto: `feat:`, `refactor:`, `docs:`, `chore:`.
- Antes de commitear, revisa lo que hiciste y asegúrate de poder explicarlo.

## 4. Hitos pendientes

### H7 — `feat: pantalla de detalle`

**Objetivo:** tocar una tarjeta y ver el detalle del producto.

**Archivos sugeridos:**
- `ui/navigation/NavCatalogo.kt`
- `ui/screens/DetalleScreen.kt`
- `viewmodel/DetalleViewModel.kt`
- `viewmodel/DetalleViewModelFactory.kt`

**Pasos:**
1. Crea un `NavHost` con las rutas `lista` y `detalle/{id}`.
2. En `lista`, llama a `ListaRoute(onProductoClick = { id -> navController.navigate("detalle/$id") })`.
3. En `detalle/{id}`, recibe el `id` con `NavType.IntType` y pásalo al ViewModel.
4. El ViewModel observa `repository.observarProductos()` y filtra por `id`, así el corazón se actualiza solo. También puede usar `repository.obtenerProducto(id)`.
5. `DetalleScreen` debe mostrar imagen, marca, nombre, precio, rating, stock, descripción y botón de favorito.
6. Agrega un `TopAppBar` con flecha de regreso (`navController.popBackStack()`).
7. Cambia `MainActivity` para que muestre `NavCatalogo()`.

**Criterios de aceptación:**
- Tocar una tarjeta abre el detalle correcto.
- La flecha de regreso vuelve a la lista.
- El botón de favorito cambia el corazón en la lista y en el detalle.
- Al rotar la pantalla no se pierde el producto.

**Commit:** `feat: pantalla de detalle`

---

### H8 — `feat: base de datos Room`

**Objetivo:** crear la base de datos local que después será la fuente de verdad.

**Dependencias (versiones usadas en el curso):**
```kotlin
id("com.google.devtools.ksp") version "2.2.10-2.0.2"
implementation("androidx.room:room-runtime:2.8.5")
implementation("androidx.room:room-ktx:2.8.5")
ksp("androidx.room:room-compiler:2.8.5")
```

**Archivos sugeridos:**
- `data/local/entity/ProductoEntity.kt`
- `data/local/dao/ProductoDao.kt`
- `data/local/CatalogoDatabase.kt`
- `data/local/ProductoLocalMapper.kt`

**Pasos:**
1. `ProductoEntity` debe tener los mismos campos que `Producto` más `favorito: Boolean = false`.
2. El DAO debe exponer como mínimo:
```kotlin
@Query("SELECT * FROM productos ORDER BY nombre")
fun observarTodos(): Flow<List<ProductoEntity>>

@Query("SELECT * FROM productos WHERE favorito = 1 ORDER BY nombre")
fun observarFavoritos(): Flow<List<ProductoEntity>>

@Query("SELECT * FROM productos WHERE id = :id")
suspend fun obtenerPorId(id: Int): ProductoEntity?

@Query("SELECT id FROM productos WHERE favorito = 1")
suspend fun obtenerIdsFavoritos(): List<Int>

@Upsert
suspend fun guardarTodos(productos: List<ProductoEntity>)

@Query("UPDATE productos SET favorito = :favorito WHERE id = :id")
suspend fun actualizarFavorito(id: Int, favorito: Boolean)
```
3. `CatalogoDatabase` debe ser `@Database(entities = [ProductoEntity::class], version = 1, exportSchema = false)` y tener un singleton `obtener(context)`.
4. Crea el mapper `ProductoEntity.toProducto()` y `Producto.toEntity()`.

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
2. `AppContainer` crea la base de datos con `context` y pasa el DAO. `CatalogoApp` debe pasar `applicationContext`.
3. `observarProductos()` devuelve `dao.observarTodos().map { ... }`.
4. `observarFavoritos()` devuelve `dao.observarFavoritos().map { ... }`.
5. `obtenerProducto(id)` busca primero en Room y, si no existe, consulta la API.
6. `cambiarFavorito(id, favorito)` usa `dao.actualizarFavorito(...)`.
7. `refrescarProductos()` consulta la API y guarda en Room **sin borrar los favoritos**:
```kotlin
override suspend fun refrescarProductos(): Result<Unit> = runCatching {
    val respuesta = api.obtenerSmartphones()
    val idsFavoritos = dao.obtenerIdsFavoritos().toSet()
    val entidades = respuesta.products.map { dto ->
        dto.toEntity().copy(favorito = dto.id in idsFavoritos)
    }
    dao.guardarTodos(entidades)
}
```

**Criterios de aceptación:**
- Al cerrar y volver a abrir la app, la lista sigue visible sin esperar a la API.
- En modo avión se ven los productos guardados.
- Un favorito sobrevive a un refresh y a un reinicio.
- Si la API falla y hay caché, no se pierde la lista.

**Commit:** `refactor: Room como fuente de verdad`

---

### H10 — `feat: favoritos`

**Objetivo:** pantalla o pestaña para ver solo los favoritos.

**Archivos sugeridos:**
- `ui/screens/FavoritosScreen.kt`
- `viewmodel/FavoritosViewModel.kt`
- `viewmodel/FavoritosViewModelFactory.kt`
- Actualizar `ui/navigation/NavCatalogo.kt`

**Pasos:**
1. Agrega una `NavigationBar` con dos destinos: **Catálogo** y **Favoritos**.
2. `FavoritosViewModel` observa `repository.observarFavoritos()`.
3. Reutiliza `ProductoCard`, `CargandoView` y `EstadoVacioView`.
4. Al tocar una tarjeta de favoritos también se abre el detalle.
5. El corazón debe quitar el producto de la lista de favoritos.

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
1. Crea un observador de conectividad con `ConnectivityManager` y `callbackFlow` (por ejemplo `data/network/ConectividadObserver.kt`).
2. Expón en el ViewModel un estado `sinConexion` o un mensaje para la UI.
3. En `ListaScreen`, muestra un banner o `Snackbar`: "Sin conexión. Mostrando productos guardados."
4. Si el refresh falla pero hay productos en Room, mantén `UiState.Exito` y muestra el aviso. Solo muestra `ErrorView` si no hay ningún dato guardado.
5. Agrega un botón o gesto para reintentar cuando vuelva la conexión.

**Criterios de aceptación:**
- Activar modo avión: la lista sigue visible y aparece el aviso.
- Desactivar modo avión: el refresh vuelve a funcionar.
- Sin caché y sin internet: se muestra `ErrorView` con "Reintentar".

**Commit:** `feat: modo sin conexion`

---

### H12 — `chore: configurar Firebase`

**Objetivo:** dejar Firebase listo para Auth y Firestore.

**Pasos:**
1. En Firebase Console crea un proyecto (o usa el que comparta Leonel).
2. Registra una app Android con el paquete **`com.tecsup.catalogo`**.
3. Descarga `google-services.json` y colócalo en `app/`. **No lo commitees**: ya está en `.gitignore`.
4. Agrega el plugin y las dependencias:
```kotlin
// build.gradle.kts (raíz)
id("com.google.gms.google-services") version "4.5.0" apply false

// app/build.gradle.kts
id("com.google.gms.google-services")
implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
```
5. En `AppContainer` crea las instancias de `FirebaseAuth` y `FirebaseFirestore`.

**Criterios de aceptación:**
- El proyecto compila con tu `google-services.json` local.
- La app abre sin errores.
- `git status` no muestra el JSON ni claves.

**Commit:** `chore: configurar Firebase`

> Si no tienes el JSON, pídeselo a Leonel por un medio privado. Nunca lo subas al repositorio.

---

### H13 — `feat: login con Firebase Auth`

**Objetivo:** registrar, iniciar y cerrar sesión.

**Archivos sugeridos:**
- `data/repository/AuthRepositoryImpl.kt`
- `ui/screens/LoginScreen.kt`
- `viewmodel/SesionViewModel.kt`
- `viewmodel/SesionViewModelFactory.kt`

**Pasos:**
1. Implementa `AuthRepository`:
   - `observarSesion()` con `callbackFlow` y `FirebaseAuth.addAuthStateListener`.
   - `iniciarSesion(correo, clave)` con `signInWithEmailAndPassword`.
   - `registrar(correo, clave)` con `createUserWithEmailAndPassword`.
   - `cerrarSesion()` con `signOut()`.
2. Al registrar, crea el documento `usuarios/{uid}` en Firestore con `correo` y `rol = "CLIENTE"`.
3. Al observar la sesión, lee el rol desde Firestore. Si el documento no existe, usa `Rol.CLIENTE`.
4. `LoginScreen` con dos modos (Ingresar / Registrarse), campos de correo y contraseña, indicador de carga y mensajes de error.
5. En `NavCatalogo`, si no hay sesión muestra el login; si hay sesión muestra el catálogo.

**Criterios de aceptación:**
- Un usuario nuevo puede registrarse.
- Puede iniciar y cerrar sesión.
- La sesión se mantiene al cerrar y abrir la app.
- Correo inválido o contraseña corta muestran un mensaje claro.

**Commit:** `feat: login con Firebase Auth`

---

### H14 — `feat: roles y panel Admin`

**Objetivo:** que el rol `ADMIN` vea opciones extra.

**Pasos:**
1. `Sesion.rol` ya existe. Asegúrate de que `AuthRepositoryImpl` lo lea de Firestore.
2. Crea `ui/screens/AdminScreen.kt` con un panel simple: cantidad de productos, cantidad de favoritos, lista de productos y un botón para limpiar la caché o destacar un producto.
3. En la `NavigationBar`, muestra el destino **Admin** solo si `sesion.rol == Rol.ADMIN`.
4. Para probar, cambia manualmente en Firestore el campo `rol` de tu usuario a `"ADMIN"`.
5. Protege la ruta: si un `CLIENTE` intenta entrar, regrésalo al catálogo.

**Criterios de aceptación:**
- Un `CLIENTE` no ve el panel Admin.
- Un `ADMIN` sí lo ve y puede entrar.
- El rol se mantiene al reiniciar la sesión.

**Commit:** `feat: roles y panel Admin`

---

### H15 — `feat: notificacion local`

**Objetivo:** mostrar una notificación local y manejar el permiso.

**Archivos sugeridos:**
- `data/notificacion/NotificadorImpl.kt`
- `res/drawable/ic_notificacion.xml`
- Actualizar `AndroidManifest.xml` y `MainActivity`

**Pasos:**
1. Implementa `Notificador` con `NotificationManager` y `NotificationCompat.Builder`.
2. Crea un canal llamado `catalogo_canal` para Android 8 o superior.
3. Declara `android.permission.POST_NOTIFICATIONS` en el manifest.
4. En Android 13 o superior, solicita el permiso en `MainActivity` con `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`.
5. Dispara la notificación en un evento real: por ejemplo, al marcar un producto como favorito o desde un botón "Probar notificación" en el panel Admin.
6. Si el permiso está denegado, la app no debe crashear: solo no muestra la notificación.

**Criterios de aceptación:**
- La primera vez se pide el permiso.
- Si se acepta, aparece la notificación.
- Si se niega, la app sigue funcionando.
- El canal aparece en los ajustes de notificaciones del sistema.

**Commit:** `feat: notificacion local`

---

### H16 — `docs: README`

**Objetivo:** documentar el proyecto para la entrega.

**El README debe incluir:**
1. Descripción de la app y tema (smartphones).
2. Tecnologías: Kotlin, Compose, MVVM, Retrofit, Room, Firebase, Coil.
3. Árbol de paquetes.
4. Diagrama de la arquitectura MVVM (puede ser texto o Mermaid).
5. Capturas de: lista, detalle, favoritos, login, panel Admin y notificación.
6. **Reparto de tareas:** Leonel H1–H6, compañero H7–H16.
7. Cómo ejecutar el proyecto (Android Studio, JDK, `local.properties`, Firebase).
8. Cómo crear un usuario `ADMIN` en Firestore.
9. Conclusiones o sección "Para pensar".

**Criterios de aceptación:**
- El README se entiende sin abrir el código.
- Las capturas son de la app real.
- El reparto de tareas coincide con los commits.

**Commit:** `docs: README`

---

## 5. Mejora con IA (rama `mejora-ia`)

Cuando `main` esté completo:

```bash
git checkout -b mejora-ia
```

Haz al menos **3 commits** con mejoras reales, por ejemplo:

1. `feat: busqueda de productos por nombre`
2. `feat: filtro de favoritos y orden por precio`
3. `docs: registrar prompts de IA`

Crea `PROMPTS.md` con una tabla:

| Prompt | Objetivo | Resultado | Ajustes |
|---|---|---|---|
| ... | ... | ... | ... |

Registra qué pediste, qué aceptaste, qué rechazaste y qué aprendiste. Sube la rama:

```bash
git push -u origin mejora-ia
```

## 6. Checklist de entrega

- [ ] 15+ commits en `main` y 6+ por integrante.
- [ ] 3+ commits en `mejora-ia` y `PROMPTS.md`.
- [ ] README con reparto de tareas y capturas.
- [ ] Tag `v1.0`:
```bash
git tag -a v1.0 -m "Version 1.0"
git push origin v1.0
```
- [ ] APK debug adjunto en el Release de GitHub.
- [ ] Sin `google-services.json`, `local.properties`, keystores ni contraseñas.
- [ ] Ambos pueden explicar cualquier archivo.
- [ ] **Borrar esta guía**:
```bash
git rm GUIA-COMPANERO.md
git commit -m "docs: eliminar guia temporal"
git push
```

## 7. Preguntas de sustentación para practicar

- ¿Por qué el repositorio es una interfaz y no una clase concreta?
- ¿Por qué la lista sale de Room y no directamente de la API?
- ¿Qué pasa con los favoritos cuando se sincroniza la API?
- ¿Dónde se decide el rol del usuario?
- ¿Qué diferencia hay entre `ProductoDto`, `ProductoEntity` y `Producto`?
- ¿Para qué sirve `UiState`?
- ¿Qué pasa si se niega el permiso de notificaciones?
- Muestra un commit tuyo y explica qué cambiaste.
- ¿Cómo funciona el modo sin conexión?
- ¿Qué harías si la API cambia el nombre de un campo?
