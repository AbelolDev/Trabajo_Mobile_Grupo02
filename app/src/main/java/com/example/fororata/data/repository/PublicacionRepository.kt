package com.example.fororata.data.repository

import android.content.Context
import com.example.fororata.data.db.AppDatabase
import com.example.fororata.data.db.Comentario
import com.example.fororata.data.db.Publicacion
import com.example.fororata.model.PublicacionConEstadisticas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PublicacionRepository(context: Context) {

    private val publicacionDao = AppDatabase.getDatabase(context).publicacionDao()
    private val comentarioDao = AppDatabase.getDatabase(context).comentarioDao()
    private val usuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    // Crear publicación
    suspend fun crearPublicacion(publicacion: Publicacion): Long {
        return publicacionDao.insertar(publicacion)
    }

    // Obtener todas las publicaciones con estadísticas
    suspend fun obtenerPublicacionesConEstadisticas(): List<PublicacionConEstadisticas> {
        val publicaciones = publicacionDao.obtenerTodas()
        return publicaciones.map { pub ->
            val promedio = comentarioDao.obtenerPromedioEstrellas(pub.id) ?: 0.0
            val cantidad = comentarioDao.contarComentarios(pub.id)
            val autor = usuarioDao.obtenerUsuarioPorCorreo("") // Temporal, necesitarás ajustar esto
            PublicacionConEstadisticas(
                publicacion = pub,
                promedioEstrellas = promedio,
                cantidadComentarios = cantidad,
                nombreAutor = "Usuario ${pub.autorId}"
            )
        }
    }

    // Obtener todas las publicaciones (Flow para observar cambios)
    fun obtenerTodasPublicaciones(): Flow<List<Publicacion>> {
        return publicacionDao.obtenerTodasFlow()
    }

    // Obtener publicación por ID
    suspend fun obtenerPublicacionPorId(id: Int): Publicacion? {
        return publicacionDao.obtenerPorId(id)
    }

    // Obtener estadísticas de una publicación
    suspend fun obtenerEstadisticasPublicacion(publicacionId: Int): PublicacionConEstadisticas? {
        val pub = publicacionDao.obtenerPorId(publicacionId) ?: return null
        val promedio = comentarioDao.obtenerPromedioEstrellas(publicacionId) ?: 0.0
        val cantidad = comentarioDao.contarComentarios(publicacionId)
        return PublicacionConEstadisticas(
            publicacion = pub,
            promedioEstrellas = promedio,
            cantidadComentarios = cantidad,
            nombreAutor = "Usuario ${pub.autorId}"
        )
    }

    // Actualizar publicación
    suspend fun actualizarPublicacion(publicacion: Publicacion): Boolean {
        return try {
            publicacionDao.actualizar(publicacion)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar publicación
    suspend fun eliminarPublicacion(publicacion: Publicacion): Boolean {
        return try {
            comentarioDao.eliminarPorPublicacion(publicacion.id)
            publicacionDao.eliminar(publicacion)
            true
        } catch (e: Exception) {
            false
        }
    }

    // === COMENTARIOS ===

    // Agregar comentario con calificación
    suspend fun agregarComentario(comentario: Comentario): Long {
        return comentarioDao.insertar(comentario)
    }

    // Obtener comentarios de una publicación
    fun obtenerComentariosFlow(publicacionId: Int): Flow<List<Comentario>> {
        return comentarioDao.obtenerPorPublicacionFlow(publicacionId)
    }

    suspend fun obtenerComentarios(publicacionId: Int): List<Comentario> {
        return comentarioDao.obtenerPorPublicacion(publicacionId)
    }

    // Eliminar comentario
    suspend fun eliminarComentario(comentario: Comentario): Boolean {
        return try {
            comentarioDao.eliminar(comentario)
            true
        } catch (e: Exception) {
            false
        }
    }
}