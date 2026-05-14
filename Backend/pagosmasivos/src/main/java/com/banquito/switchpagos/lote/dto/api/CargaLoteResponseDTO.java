package com.banquito.switchpagos.lote.dto.api;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import java.util.UUID;

public record CargaLoteResponseDTO(
        UUID uuidLote,
        EstadoLoteEnum estado,
        String mensaje) {
}
