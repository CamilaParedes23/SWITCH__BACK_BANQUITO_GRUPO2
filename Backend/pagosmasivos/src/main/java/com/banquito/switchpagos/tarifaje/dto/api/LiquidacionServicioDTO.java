package com.banquito.switchpagos.tarifaje.dto.api;

import java.math.BigDecimal;
import java.util.UUID;

public record LiquidacionServicioDTO(
        UUID uuidLote,
        BigDecimal subtotalComision,
        BigDecimal montoIva,
        BigDecimal totalDebitado) {
}
