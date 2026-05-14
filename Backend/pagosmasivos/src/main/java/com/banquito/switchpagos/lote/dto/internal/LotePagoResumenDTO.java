package com.banquito.switchpagos.lote.dto.internal;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import java.math.BigDecimal;
import java.util.UUID;

public record LotePagoResumenDTO(
        UUID uuidLote,
        String rucEmpresa,
        EstadoLoteEnum estado,
        Integer totalRegistrosDeclarado,
        BigDecimal montoTotalDeclarado) {
}
