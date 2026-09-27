package com.example.medicinas.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val contrasena: String,
    val nombreCompleto: String,
    val rol: String
)