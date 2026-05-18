package com.banquito.switchpagos.auth.dto.api;

import com.fasterxml.jackson.annotation.JsonAlias;

public record LoginSwitchRequest(
        String usuario,
        @JsonAlias("contraseña")
        String contrasena
) {
}
