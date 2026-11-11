# PROMPTS — Uso de IA en la Fase 2 (`mejora-ia`)

Registro de las consultas hechas a la IA durante el proyecto: qué se pidió, qué
se aceptó, qué se cambió o rechazó y qué se aprendió.

---

## Consulta 1
- Fecha: 08/11/2026
- Herramienta: Claude Code
- Prompt: «Lee el README del repositorio y dime qué me toca hacer como segundo
  integrante después de los hitos H1–H6.»
- Qué respondió (resumen): resumió la sección 4 (correcciones de estructura y
  contratos en un solo commit) y los hitos H7–H16, y señaló que H12 en adelante
  necesitaba el `google-services.json` del paquete `com.tecsup.catalogo`.
- Qué usamos / qué cambiamos y por qué: seguimos el orden del README; primero las
  correcciones y luego un commit por hito, con el mismo estilo de mensajes que
  los commits de Leonel (`tipo: descripcion`, una sola línea).
- Qué aprendimos: los contratos (nombres de métodos y propiedades) no se cambian
  porque el docente revisa todos los proyectos con el mismo criterio.

## Consulta 2
- Fecha: 08/11/2026
- Herramienta: Claude Code
- Prompt: «Al agregar Room con KSP `2.2.10-2.0.2` el build falla con "Using
  kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in
  Kotlin". ¿Cómo lo soluciono sin cambiar la versión del README?»
- Qué respondió (resumen): explicó que AGP 9 trae Kotlin integrado y bloquea que
  KSP agregue sus carpetas con `kotlin.sourceSets`; propuso la línea
  `android.disallowKotlinSourceSets=false` en `gradle.properties`, que es la que
  sugiere el propio mensaje de error.
- Qué usamos / qué cambiamos y por qué: usamos esa línea con un comentario.
  Rechazamos subir KSP a otra versión para respetar la versión exacta del README.
- Qué aprendimos: leer el mensaje de error completo; muchas veces ya trae la
  solución. También que AGP 9 cambia cómo se integra Kotlin.

## Consulta 3
- Fecha: 08/11/2026
- Herramienta: Claude Code
- Prompt: «Escribe pruebas unitarias de `ListaViewModel` con un repositorio falso
  y un `Notificador` falso: sincronización exitosa, sin conexión con caché, sin
  conexión sin caché y alternar favorito.»
- Qué respondió (resumen): creó `ListaViewModelTest` con `RepositorioFalso` y
  `NotificadorFalso`, usando `kotlinx-coroutines-test`,
  `Dispatchers.setMain(StandardTestDispatcher())` y `advanceUntilIdle()`.
- Qué usamos / qué cambiamos y por qué: aceptamos las 4 pruebas. Se agregó
  `backgroundScope.launch { viewModel.estado.collect {} }` porque `estado` usa
  `SharingStarted.WhileSubscribed` y sin alguien que lo observe nunca se calcula.
- Qué aprendimos: como el ViewModel depende de interfaces, se puede probar sin
  red, sin Room y sin Android. `viewModelScope` usa el dispatcher principal, por
  eso hay que reemplazarlo en las pruebas.

## Consulta 4
- Fecha: 08/11/2026
- Herramienta: Claude Code
- Prompt: «Agrega una búsqueda de smartphones por nombre en la lista, sin romper
  MVVM ni las pruebas.»
- Qué respondió (resumen): propuso guardar el texto en un `StateFlow` del
  ViewModel (`busqueda`), combinarlo con `repository.items` y filtrar con
  `contains(texto, ignoreCase = true)`; en la pantalla, un `OutlinedTextField`
  con ícono de lupa y botón para borrar.
- Qué usamos / qué cambiamos y por qué: aceptamos la idea. El filtro queda en el
  ViewModel (no en el composable) para que la pantalla solo muestre el estado.
  Si no hay coincidencias se reutiliza `EstadoVacioView` con un mensaje claro.
- Qué aprendimos: `combine` permite unir varios flujos (lista, carga, error y
  búsqueda) en un solo `UiState`. Volvimos a correr las pruebas para confirmar
  que la búsqueda no cambió el comportamiento anterior.

## Consulta 5
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «Quiero agregar una pantalla de registro en la rama mejora-ia. ¿La
  cuenta nueva aparecería en Firebase?»
- Qué respondió (resumen): que `createUserWithEmailAndPassword` crea la cuenta
  en Firebase Authentication, pero que el rol en Firestore no se crea solo.
  Advirtió que las reglas que teníamos (`allow write` para el propio usuario)
  permitían que alguien se pusiera `rol: "admin"` él mismo, y que la guía pide
  `allow write: if false`.
- Qué usamos / qué cambiamos y por qué: primero corregimos las reglas en `main`
  dejando la escritura bloqueada, como dice la guía. Después agregamos
  `registrar()` en `AuthRepository` y `FirebaseAuthRepository`, la pantalla
  `RegistroScreen` y el enlace desde el login. El registro solo crea la cuenta
  en Authentication y no escribe en Firestore: el usuario nuevo entra como
  USUARIO y, si el docente quiere hacerlo admin, crea su documento en la
  consola. Rechazamos guardar el rol desde la app.
- Qué aprendimos: validar el rol en la app solo cambia lo que se muestra; lo que
  protege de verdad son las reglas de Firestore. También que los errores de
  Firebase (correo repetido, contraseña débil) se pueden traducir en el
  repositorio para que el ViewModel no dependa de Firebase.

## Consulta 6
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «La interfaz se ve muy simple. Mejórala, pero que siga siendo sencilla
  de usar para el usuario.»
- Qué respondió (resumen): explicó que «sencilla» significa fácil de usar, no
  pobre, y que la rúbrica pide una «interfaz clara». Propuso usar siempre los
  colores propios de la app (el tema tenía `dynamicColor = true` y tomaba los
  colores del fondo de pantalla), tarjetas en el login y registro con opción de
  mostrar la contraseña, rating y stock en las tarjetas, deslizar para
  sincronizar, mensajes cortos (Snackbar) y una confirmación antes de limpiar
  la caché.
- Qué usamos / qué cambiamos y por qué: aceptamos todo en tres commits (tema,
  login y registro, y catálogo/detalle/Admin). Las piezas repetidas quedaron
  como componentes (`ComponentesAuth.kt` y `Etiquetas.kt`). El mensaje del
  panel Admin sale del ViewModel; las pantallas solo lo muestran. Rechazamos
  agregar la librería de íconos extendida solo para el ícono del «ojo»: se usó
  un botón de texto «Mostrar / Ocultar».
- Qué aprendimos: `PullToRefreshBox` da el gesto de deslizar con muy poco
  código; en una columna con scroll `Arrangement.Center` no centra, hay que
  envolverla en un `Box`; y una confirmación antes de borrar evita errores del
  usuario.

## Consulta 7
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «Al entrar a un celular deben salir sus características (procesador,
  RAM, etc.) y abajo los demás celulares.»
- Qué respondió (resumen): revisó la respuesta real de DummyJSON y avisó que
  la API **no trae** procesador, RAM ni almacenamiento; sí trae peso, medidas,
  garantía, envío, disponibilidad, devolución, descuento, SKU y pedido mínimo.
  Recomendó mostrar solo los datos reales y no escribir a mano procesador y RAM.
- Qué usamos / qué cambiamos y por qué: elegimos los datos reales de la API.
  Se agregaron los campos al DTO, a `ProductoEntity` y a `Producto`, y la base
  de datos pasó a la versión 2 (como Room solo guarda una copia de la API, se
  usa `fallbackToDestructiveMigration` y se vuelve a descargar). En el detalle
  se agregó la tarjeta «Características» y una fila «Otros smartphones» que
  sale de Room y abre el detalle de otro celular al tocarlo. Rechazamos
  inventar procesador y RAM porque no vendrían de la API.
- Qué aprendimos: antes de crear un DTO hay que abrir la URL de la API y ver
  qué campos trae de verdad (lo dice también la guía). Además, si cambia la
  tabla de Room hay que subir la versión de la base de datos.

## Consulta 8
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «Pon el fondo blanco, adapta los colores y reemplaza las imágenes por
  las de Movistar, Claro y Entel, que son mejores.»
- Qué respondió (resumen): cambió el fondo a blanco y ajustó los colores. Sobre
  las imágenes, explicó que esas tiendas no venden los 16 modelos de la API
  (son antiguos), que sus fotos tienen derechos de autor y nuestro repositorio
  es público, y que poner URLs a mano rompe la idea de que los datos salen de
  la API. Propuso usar el campo `images` de DummyJSON, que trae 3 fotos más
  grandes de cada celular.
- Qué usamos / qué cambiamos y por qué: usamos el fondo blanco con bordes
  suaves en las tarjetas y grises claros para separar secciones. Para las
  imágenes, guardamos `images` en Room (como texto separado por «|», porque
  Room no guarda listas sin un convertidor) y en el detalle pusimos un
  carrusel (`HorizontalPager`) con puntos indicadores. Rechazamos copiar
  imágenes de otras tiendas.
- Qué aprendimos: hay que revisar todo lo que trae la API antes de buscar
  datos afuera; y que usar imágenes de otras empresas en un repositorio
  público tiene problemas de derechos de autor.

## Consulta 9
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «No se entiende la descripción, ponla en español. ¿Por qué dice "En
  stock" si ya sale el stock? El descuento debe salir al lado del precio.»
- Qué respondió (resumen): explicó que DummyJSON solo manda los textos en
  inglés, así que propuso una tabla de traducciones en la capa de datos
  (`Traducciones.kt`) para la descripción, la garantía, el envío y la
  devolución. Quitó «Disponibilidad» y «Descuento» de las características por
  repetir información, y movió el descuento junto al precio. También notó que
  la app se veía oscura porque el celular estaba en modo oscuro.
- Qué usamos / qué cambiamos y por qué: las traducciones se aplican en el
  mapper (`toModelo`), así la pantalla no traduce nada y Room sigue guardando
  lo que llega de la API. El precio ahora muestra el precio con descuento, el
  precio original tachado y una etiqueta «-N%», en la lista y en el detalle.
  El tema quedó siempre en modo claro, con fondo blanco.
- Qué aprendimos: no repetir el mismo dato en dos lugares de la pantalla, y que
  traducir en el mapper mantiene a las pantallas sin lógica.

## Consulta 10
- Fecha: 11/11/2026
- Herramienta: Claude Code
- Prompt: «Agrega un filtro por marca. La garantía "de por vida" no tiene sentido,
  pon 1 año; quita el pedido mínimo y pon un peso realista, inspirándote en
  Movistar, Entel y Claro.»
- Qué respondió (resumen): agregó el filtro por marca en el ViewModel (las
  marcas salen de Room) con chips en la lista. Explicó que DummyJSON es una
  API de prueba y algunos valores no son reales (la garantía es al azar y el
  peso viene como «5» sin unidad), y propuso mostrarlos como en una tienda.
- Qué usamos / qué cambiamos y por qué: la garantía se muestra como «1 año»
  (garantía del fabricante, como en las tiendas) y el peso se toma de una tabla
  con el peso real de cada modelo en gramos (`DatosTienda.kt`, en la capa de
  datos). Quitamos «Pedido mínimo» porque una persona compra un solo celular.
  Room sigue guardando lo que manda la API; el cambio se hace en el mapper.
- Qué aprendimos: una API de prueba sirve para practicar, pero hay que revisar
  si sus datos tienen sentido para el usuario antes de mostrarlos.

---

## Resumen

| Aceptado | Rechazado o cambiado |
|---|---|
| Orden de trabajo según el README | Subir la versión de KSP |
| `android.disallowKotlinSourceSets=false` | Filtrar dentro del composable |
| Repositorio y notificador falsos para las pruebas | Guardar el rol desde la app al registrarse |
| Búsqueda con `StateFlow` + `combine` | — |
| Registro solo en Firebase Authentication | Íconos extendidos solo para el «ojo» de la contraseña |
| Tema propio, tarjetas, Snackbar y confirmación al limpiar | Inventar procesador y RAM que la API no trae |
| Características reales de la API y «Otros smartphones» | Copiar imágenes de Movistar, Claro y Entel |
| Fondo blanco y carrusel con las fotos grandes de la API | — |
