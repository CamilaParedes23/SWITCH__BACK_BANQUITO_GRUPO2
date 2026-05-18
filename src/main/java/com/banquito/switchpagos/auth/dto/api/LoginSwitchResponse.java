package com.banquito.switchpagos.auth.dto.api;

public record LoginSwitchResponse(
        Boolean autenticado,
        String tipoUsuario,
        String credencialWebId,
        String clienteId,
        String rucEmpresa,
        String usuario,
        String nombre,
        String rolSwitch,
        String estado,
        Boolean activoPagosMasivos
) {
}
