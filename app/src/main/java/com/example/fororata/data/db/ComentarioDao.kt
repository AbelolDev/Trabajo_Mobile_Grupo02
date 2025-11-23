package com.example.fororata.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComentarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(comentario: Comentario): Long

    @Update
    suspend fun actualizar(comentario: Comentario)

    @Delete
    suspend fun eliminar(comentario: Comentario)

    @Query("SELECT * FROM comentarios WHERE publicacionId = :publicacionId ORDER BY fechaCreacion DESC")
    fun obtenerPorPublicacionFlow(publicacionId: Int): Flow<List<Comentario>>

    @Query("SELECT * FROM comentarios WHERE publicacionId = :publicacionId ORDER BY fechaCreacion DESC")
    suspend fun obtenerPorPublicacion(publicacionId: Int): List<Comentario>

    @Query("SELECT AVG(estrellas) FROM comentarios WHERE publicacionId = :publicacionId")
    suspend fun obtenerPromedioEstrellas(publicacionId: Int): Double?

    @Query("SELECT COUNT(*) FROM comentarios WHERE publicacionId = :publicacionId")
    suspend fun contarComentarios(publicacionId: Int): Int

    @Query("DELETE FROM comentarios WHERE publicacionId = :publicacionId")
    suspend fun eliminarPorPublicacion(publicacionId: Int)
}