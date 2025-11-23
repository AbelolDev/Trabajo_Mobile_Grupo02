package com.example.fororata.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, Publicacion::class, Comentario::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun comentarioDao(): ComentarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foro_rata_db"
                )
                    .fallbackToDestructiveMigration() // Para desarrollo, elimina esta línea en producción
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}