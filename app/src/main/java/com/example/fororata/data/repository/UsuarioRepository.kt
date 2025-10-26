package com.example.fororata.data.repository

import android.content.Context
import com.example.fororata.data.db.AppDatabase
import com.example.fororata.data.db.Usuario

class UsuarioRepository(context: Context) {

    private val usuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    suspend fun registrarUsuario(usuario: Usuario): Boolean {
        val id = usuarioDao.insertarUsuario(usuario)
        return id > 0
    }

    suspend fun iniciarSesion(correo: String, clave: String): Boolean {
        return usuarioDao.verificarCredenciales(correo, clave) > 0
    }

    suspend fun obtenerUsuario(correo: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorCorreo(correo)
    }
}
