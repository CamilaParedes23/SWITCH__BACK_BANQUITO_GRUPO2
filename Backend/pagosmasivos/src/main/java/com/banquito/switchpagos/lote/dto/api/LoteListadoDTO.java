package com.banquito.switchpagos.lote.dto.api;

import com.banquito.switchpagos.common.enums.CanalIngresoEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LoteListadoDTO(
        UUID uuidLote,
        String rucEmpresa,
        String nombreArchivo,
        EstadoLoteEnum estado,
        CanalIngresoEnum canalIngreso,
        OffsetDateTime fechaRecepcion) {
}
