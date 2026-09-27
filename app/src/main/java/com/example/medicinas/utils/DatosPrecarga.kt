package com.example.medicinas.utils

import com.example.medicinas.data.local.entities.PaqueteEntity

object DatosPrecarga {
    fun obtenerPaquetesReales(): List<PaqueteEntity> {
        return listOf(
            // === VACUNAS ===
            PaqueteEntity("TRK-VAC-0001", "VAC-001", "Vacuna", "Vacuna Antigripal", "VG26A01", 2, 8, "3/15/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0002", "VAC-002", "Vacuna", "Vacuna Hepatitis B", "VH26B02", 2, 8, "5/20/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0003", "VAC-003", "Vacuna", "Vacuna Hepatitis A", "VA26C03", 2, 8, "1/10/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0004", "VAC-004", "Vacuna", "Vacuna VPH", "VP26D04", 2, 8, "8/30/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-VAC-0005", "VAC-005", "Vacuna", "Vacuna Neumocócica", "VN26E05", 2, 8, "2/18/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0006", "VAC-006", "Vacuna", "Vacuna Triple Viral", "VT26F06", 2, 8, "9/20/2026", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-VAC-0007", "VAC-007", "Vacuna", "Vacuna Polio IPV", "VI26G07", 2, 8, "1/25/2028", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0008", "VAC-008", "Vacuna", "Vacuna Rotavirus", "VR26H08", 2, 8, "12/5/2026", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0009", "VAC-009", "Vacuna", "Vacuna Varicela", "VV26I09", 2, 8, "11/12/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-VAC-0010", "VAC-010", "Vacuna", "Vacuna Fiebre Amarilla", "VF26J10", 2, 8, "6/28/2027", requiereVideo = true, requiereFoto = true),

            // === ARM (Medicamentos de Alto Riesgo) ===
            PaqueteEntity("TRK-ARM-0001", "ARM-001", "ARM", "Insulina humana", "IH26A01", 2, 8, "4/15/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-ARM-0002", "ARM-002", "ARM", "Insulina glargina", "IG26B02", 2, 8, "7/20/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-ARM-0003", "ARM-003", "ARM", "Heparina", "HH26C03", 15, 25, "2/10/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-ARM-0004", "ARM-004", "ARM", "Enoxaparina", "EN26D04", 15, 25, "9/1/2027", requiereVideo = false, requiereFoto = false),
            PaqueteEntity("TRK-ARM-0005", "ARM-005", "ARM", "Cloruro de potasio concentrado", "KP26E05", 15, 25, "1/15/2028", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-ARM-0006", "ARM-006", "ARM", "Sulfato de magnesio", "SM26F06", 15, 25, "11/30/2027", requiereVideo = false, requiereFoto = false),
            PaqueteEntity("TRK-ARM-0007", "ARM-007", "ARM", "Adrenalina", "AD26G07", 15, 25, "3/12/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-ARM-0008", "ARM-008", "ARM", "Noradrenalina", "NO26H08", 2, 8, "5/18/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-ARM-0009", "ARM-009", "ARM", "Medicamento citotóxico A", "CT26I09", 2, 8, "10/25/2026", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-ARM-0010", "ARM-010", "ARM", "Medicamento citotóxico B", "CT26J10", 15, 25, "8/14/2027", requiereVideo = true, requiereFoto = true),

            // === BIOLÓGICOS ===
            PaqueteEntity("TRK-BIO-0001", "BIO-001", "Biológico", "Anticuerpo monoclonal A", "BM26A01", 2, 8, "2/28/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-BIO-0002", "BIO-002", "Biológico", "Anticuerpo monoclonal B", "BM26B02", 2, 8, "4/30/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-BIO-0003", "BIO-003", "Biológico", "Factor de coagulación A", "FC26C03", 2, 8, "6/15/2027", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-BIO-0004", "BIO-004", "Biológico", "Factor de coagulación B", "FC26D04", 2, 8, "10/20/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-BIO-0005", "BIO-005", "Biológico", "Proteína terapéutica A", "PT26E05", 2, 8, "1/31/2027", requiereVideo = false, requiereFoto = false),
            PaqueteEntity("TRK-BIO-0006", "BIO-006", "Biológico", "Proteína terapéutica B", "PT26F06", 2, 8, "8/10/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-BIO-0007", "BIO-007", "Biológico", "Inmunoglobulina A", "IG26G07", 2, 8, "2/12/2028", requiereVideo = true, requiereFoto = false),
            PaqueteEntity("TRK-BIO-0008", "BIO-008", "Biológico", "Inmunoglobulina B", "IG26H08", 2, 8, "12/1/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-BIO-0009", "BIO-009", "Biológico", "Terapia enzimática A", "TE26I09", 2, 8, "9/22/2027", requiereVideo = true, requiereFoto = true),
            PaqueteEntity("TRK-BIO-0010", "BIO-010", "Biológico", "Terapia enzimática B", "TE26J10", 2, 8, "9/18/2026", requiereVideo = true, requiereFoto = true),

            // === FÁRMACOS ===
            PaqueteEntity("TRK-FAR-0001", "FAR-001", "Fármaco", "Paracetamol", "PF26A01", 15, 25, "1/10/2028", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0002", "FAR-002", "Fármaco", "Amoxicilina", "AM26B02", 15, 25, "11/15/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0003", "FAR-003", "Fármaco", "Metformina", "MF26C03", 15, 25, "3/20/2028", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0004", "FAR-004", "Fármaco", "Losartán", "LS26D04", 15, 25, "10/12/2027", requiereVideo = false, requiereFoto = false),
            PaqueteEntity("TRK-FAR-0005", "FAR-005", "Fármaco", "Omeprazol", "OM26E05", 15, 25, "9/8/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0006", "FAR-006", "Fármaco", "Cefalexina", "CF26F06", 15, 25, "12/17/2027", requiereVideo = false, requiereFoto = false),
            PaqueteEntity("TRK-FAR-0007", "FAR-007", "Fármaco", "Ibuprofeno", "IB26G07", 15, 25, "2/28/2028", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0008", "FAR-008", "Fármaco", "Atorvastatina", "AT26H08", 15, 25, "4/5/2028", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0009", "FAR-009", "Fármaco", "Levotiroxina", "LV26I09", 15, 25, "7/22/2027", requiereVideo = false, requiereFoto = true),
            PaqueteEntity("TRK-FAR-0010", "FAR-010", "Fármaco", "Furosemida", "FU26J10", 15, 25, "5/18/2028", requiereVideo = false, requiereFoto = true)
        )
    }
}