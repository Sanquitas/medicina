package com.example.medicinas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medicinas.data.local.dao.PaqueteDao
import com.example.medicinas.data.local.dao.UsuarioDao
import com.example.medicinas.data.local.entities.PaqueteEntity
import com.example.medicinas.data.local.entities.UsuarioEntity
import com.example.medicinas.utils.DatosPrecarga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UsuarioEntity::class, PaqueteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun paqueteDao(): PaqueteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicinas_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val usuarioDao = database.usuarioDao()
                        val paqueteDao = database.paqueteDao()

                        // Insertamos los usuarios
                        usuarioDao.insertarUsuario(UsuarioEntity(username = "Operador", contrasena = "Operador", nombreCompleto = "Juan Operador", rol = "OPERADOR"))
                        usuarioDao.insertarUsuario(UsuarioEntity(username = "Supervisor", contrasena = "Supervisor", nombreCompleto = "Maria Supervisor", rol = "SUPERVISOR"))
                        usuarioDao.insertarUsuario(UsuarioEntity(username = "Administrador", contrasena = "Administrador", nombreCompleto = "Carlos Admin", rol = "ADMINISTRADOR"))

                        // Insertamos los paquetes mandando a llamar a nuestro archivo utilitario
                        val paquetes = DatosPrecarga.obtenerPaquetesReales()
                        paqueteDao.insertarPaquetes(paquetes)
                    }
                }
            }
        }
    }
}