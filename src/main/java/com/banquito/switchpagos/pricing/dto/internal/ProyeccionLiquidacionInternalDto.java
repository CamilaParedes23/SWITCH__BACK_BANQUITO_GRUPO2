package com.banquito.switchpagos.pricing.dto.internal;

import java.math.BigDecimal;

public record ProyeccionLiquidacionInternalDto(
        Integer transaccionesEstimadas,
        BigDecimal tarifaUnitariaAplicada,
        BigDecimal ivaPorcentajeAplicado,
        BigDecimal subtotalComision,
        BigDecimal montoIva,
        BigDecimal totalDebitado
) {
}
