package com.example.fororata.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ForoRata.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USUARIOS = "usuarios"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_CORREO = "correo"
        const val COL_CLAVE = "clave"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT UNIQUE NOT NULL,
                $COL_CLAVE TEXT NOT NULL
            );
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    fun insertarUsuario(usuario: Usuario): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, usuario.nombre)
            put(COL_CORREO, usuario.correo)
            put(COL_CLAVE, usuario.clave)
        }
        return db.insert(TABLE_USUARIOS, null, values)
    }

    fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COL_CORREO = ?",
            arrayOf(correo),
            null,
            null,
            null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORREO)),
                clave = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLAVE))
            )
        }

        cursor.close()
        return usuario
    }

    fun validarUsuario(correo: String, clave: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? AND $COL_CLAVE = ?",
            arrayOf(correo, clave)
        )
        val valido = cursor.count > 0
        cursor.close()
        return valido
    }
}
