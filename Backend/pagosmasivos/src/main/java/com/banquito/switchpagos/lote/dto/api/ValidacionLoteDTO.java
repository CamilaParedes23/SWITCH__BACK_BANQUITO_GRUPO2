package com.banquito.switchpagos.lote.dto.api;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import java.util.UUID;

public record ValidacionLoteDTO(
        UUID uuidLote,
        EstadoLoteEnum estadoAnterior,
        EstadoLoteEnum estadoActual,
        String mensaje) {
}
