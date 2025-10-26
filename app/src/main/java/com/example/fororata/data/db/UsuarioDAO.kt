package com.example.fororata.data.db

import androidx.room.*

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo AND clave = :clave")
    suspend fun verificarCredenciales(correo: String, clave: String): Int
}
