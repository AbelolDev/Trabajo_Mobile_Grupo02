package com.example.fororata.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(publicacion: Publicacion): Long

    @Update
    suspend fun actualizar(publicacion: Publicacion)

    @Delete
    suspend fun eliminar(publicacion: Publicacion)

    @Query("SELECT * FROM publicaciones ORDER BY fechaCreacion DESC")
    fun obtenerTodasFlow(): Flow<List<Publicacion>>

    @Query("SELECT * FROM publicaciones ORDER BY fechaCreacion DESC")
    suspend fun obtenerTodas(): List<Publicacion>

    @Query("SELECT * FROM publicaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Publicacion?

    @Query("SELECT * FROM publicaciones WHERE autorId = :autorId ORDER BY fechaCreacion DESC")
    fun obtenerPorAutorFlow(autorId: Int): Flow<List<Publicacion>>

    @Query("DELETE FROM publicaciones WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}