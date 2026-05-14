package com.banquito.switchpagos.procesamiento.dto.api;

import com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum;
import java.math.BigDecimal;
import java.util.UUID;

public record LineaLoteDTO(
        Integer secuencial,
        String identificacionBeneficiario,
        String nombreBeneficiario,
        String cuentaDestino,
        BigDecimal monto,
        EstadoLineaPagoEnum estado,
        String codigoError,
        String mensajeError,
        UUID uuidOperacionSwitch) {
}
