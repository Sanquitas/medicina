package com.example.medicinas.utils

import com.example.medicinas.viewmodel.EstadoPaquete
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CaducidadUtils {

    fun evaluar(fechaStr: String): EstadoPaquete {
        return try {
            val sdf = SimpleDateFormat("M/d/yyyy", Locale.US)
            val expirationDate = sdf.parse(fechaStr) ?: return EstadoPaquete.NORMAL

            // Calculamos "today" reseteando las horas para precisión exacta de días
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            // Calculamos "today + 30 días"
            val todayPlus30 = Calendar.getInstance().apply {
                time = today
                add(Calendar.DAY_OF_YEAR, 30)
            }.time

            when {
                expirationDate.before(today) || expirationDate == today -> EstadoPaquete.CADUCADO
                expirationDate.before(todayPlus30) || expirationDate == todayPlus30 -> EstadoPaquete.PROXIMO_A_CADUCAR
                else -> EstadoPaquete.NORMAL
            }
        } catch (e: Exception) {
            EstadoPaquete.NORMAL
        }
    }
}