package com.banquito.switchpagos.reporte.dto.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ComprobanteLiquidacionDTO(
        UUID uuidLote,
        Integer transaccionesExitosas,
        Integer transaccionesFallidas,
        BigDecimal subtotalComision,
        BigDecimal montoIva,
        BigDecimal totalDebitado) {
}
