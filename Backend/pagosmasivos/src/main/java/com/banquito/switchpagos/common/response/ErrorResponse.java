package com.banquito.switchpagos.common.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime fecha,
        Integer codigo,
        String error,
        String mensaje,
        String ruta) {
}
