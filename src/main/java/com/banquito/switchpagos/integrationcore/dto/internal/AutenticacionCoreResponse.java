package com.banquito.switchpagos.integrationcore.dto.internal;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AutenticacionCoreResponse(
        Boolean autenticado,
        String tipoUsuario,
        String credencialWebId,
        String clienteId,
        String rucEmpresa,
        String usuario,
        String nombre,
        String rolSwitch,
        String estado,
        @JsonAlias({"activoPagos Masivos", "activoPagosMasivos"})
        Boolean activoPagosMasivos
) {
}
