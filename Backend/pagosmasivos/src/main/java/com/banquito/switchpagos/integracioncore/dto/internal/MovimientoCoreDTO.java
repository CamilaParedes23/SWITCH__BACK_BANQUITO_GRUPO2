package com.banquito.switchpagos.integracioncore.dto.internal;

import java.util.UUID;

public record MovimientoCoreDTO(
        UUID uuidMovimiento,
        String mensaje) {
}
