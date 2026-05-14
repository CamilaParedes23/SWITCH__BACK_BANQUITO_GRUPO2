package com.banquito.switchpagos.reporte.dto.api;

import com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum;

public record NovedadLoteDTO(
        Integer secuencial,
        String identificacionBeneficiario,
        String nombreBeneficiario,
        EstadoLineaPagoEnum estado,
        String codigoError,
        String mensajeError) {
}
