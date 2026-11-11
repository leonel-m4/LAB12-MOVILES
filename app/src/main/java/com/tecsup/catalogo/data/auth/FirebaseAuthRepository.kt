package com.tecsup.catalogo.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.catalogo.domain.model.Rol
import com.tecsup.catalogo.domain.model.Sesion
import kotlinx.coroutines.tasks.await

/**
 * Implementación de [AuthRepository] con Firebase Auth y Firestore.
 *
 * El rol no se decide en la app: se lee del documento usuarios/{uid}.
 */
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

    /**
     * Crea la cuenta en Firebase Authentication y deja la sesión iniciada.
     *
     * No escribe en Firestore (las reglas no lo permiten): como el usuario
     * nuevo no tiene documento en usuarios/{uid}, entra con rol USUARIO.
     * Si el docente quiere hacerlo admin, crea ese documento en la consola.
     */
    override suspend fun registrar(
        correo: String,
        clave: String
    ): Result<Sesion> = try {
        val uid = auth.createUserWithEmailAndPassword(correo, clave).await().user!!.uid
        Result.success(leerSesion(uid, correo))
    } catch (e: FirebaseAuthUserCollisionException) {
        Result.failure(Exception("Ese correo ya está registrado."))
    } catch (e: FirebaseAuthWeakPasswordException) {
        Result.failure(Exception("La contraseña es muy débil."))
    } catch (e: Exception) {
        Result.failure(Exception("No se pudo crear la cuenta. Inténtalo otra vez."))
    }

    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }
}
