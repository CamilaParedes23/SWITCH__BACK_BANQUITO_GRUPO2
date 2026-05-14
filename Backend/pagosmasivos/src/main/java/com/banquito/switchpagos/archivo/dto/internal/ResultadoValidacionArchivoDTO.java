package com.banquito.switchpagos.archivo.dto.internal;

import java.math.BigDecimal;

public record ResultadoValidacionArchivoDTO(
        Boolean valido,
        String mensaje,
        Integer totalRegistrosDetalle,
        BigDecimal montoTotalDetalle) {
}
