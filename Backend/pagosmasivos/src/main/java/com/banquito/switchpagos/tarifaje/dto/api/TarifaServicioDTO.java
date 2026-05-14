package com.banquito.switchpagos.tarifaje.dto.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TarifaServicioDTO(
        String codigoTipoServicio,
        Integer rangoDesde,
        Integer rangoHasta,
        BigDecimal tarifaUnitaria,
        String moneda,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta) {
}
