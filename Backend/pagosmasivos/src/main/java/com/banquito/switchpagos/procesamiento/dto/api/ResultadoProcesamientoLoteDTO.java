package com.banquito.switchpagos.procesamiento.dto.api;

import java.util.UUID;

public record ResultadoProcesamientoLoteDTO(
        UUID uuidLote,
        String estadoLote,
        Integer transaccionesExitosas,
        Integer transaccionesFallidas) {
}
