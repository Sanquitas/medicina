package com.example.medicinas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medicinas.data.local.entities.PaqueteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaqueteDao {
    @Query("SELECT * FROM paquetes")
    fun obtenerTodosLosPaquetes(): Flow<List<PaqueteEntity>>

    @Query("SELECT * FROM paquetes WHERE tracking = :trackingId LIMIT 1")
    suspend fun obtenerPaquetePorTracking(trackingId: String): PaqueteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPaquetes(paquetes: List<PaqueteEntity>): List<Long>

    @Query("UPDATE paquetes SET estadoEntrega = 'Entregado' WHERE tracking = :trackingId")
    suspend fun marcarComoEntregado(trackingId: String): Int
}