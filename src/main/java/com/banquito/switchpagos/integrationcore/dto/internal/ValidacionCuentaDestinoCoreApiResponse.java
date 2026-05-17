package com.banquito.switchpagos.integrationcore.dto.internal;

public record ValidacionCuentaDestinoCoreApiResponse(
        String numeroCuenta,
        String identificacionBeneficiario,
        String nombreBeneficiario,
        Boolean existe,
        Boolean perteneceBeneficiario,
        String estado,
        Boolean permiteDeposito,
        Boolean bloqueada,
        Boolean valida,
        String codigo,
        String mensaje
) {
}
