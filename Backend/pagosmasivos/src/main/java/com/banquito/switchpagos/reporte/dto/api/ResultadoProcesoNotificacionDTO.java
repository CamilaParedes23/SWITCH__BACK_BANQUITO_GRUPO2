package com.banquito.switchpagos.reporte.dto.api;

public record ResultadoProcesoNotificacionDTO(
        Integer pendientesProcesadas,
        Integer enviadas,
        Integer conError) {
}
