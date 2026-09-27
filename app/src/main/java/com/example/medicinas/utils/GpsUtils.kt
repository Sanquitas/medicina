package com.example.medicinas.utils

import android.location.Location

object GpsUtils {
    /**
     * Calcula la distancia en metros entre dos coordenadas geográficas.
     */
    fun calcularDistanciaMetros(
        latActual: Double, lonActual: Double,
        latDestino: Double, lonDestino: Double
    ): Float {
        val resultados = FloatArray(1)
        Location.distanceBetween(latActual, lonActual, latDestino, lonDestino, resultados)
        return resultados[0]
    }
}