package com.example.medicinas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicinas.data.local.entities.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE username = :user AND contrasena = :pass LIMIT 1")
    suspend fun login(user: String, pass: String): UsuarioEntity?

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long
}