package com.banquito.switchpagos.integracioncore.dto.internal;

import java.math.BigDecimal;

public record RespuestaCuentaCoreDTO(
        String numeroCuenta,
        String identificacionTitular,
        Boolean permiteDepositos,
        BigDecimal saldoDisponible) {
}
