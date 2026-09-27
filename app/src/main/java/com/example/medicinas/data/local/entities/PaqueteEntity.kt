package com.example.medicinas.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paquetes")
data class PaqueteEntity(
    @PrimaryKey val tracking: String,
    val idProducto: String,
    val tipo: String,
    val producto: String,
    val lote: String,
    val tempMin: Int,
    val tempMax: Int,
    val caducidad: String,
    val requiereVideo: Boolean,
    val requiereFoto: Boolean,
    val estadoEntrega: String = "PENDIENTE"
)