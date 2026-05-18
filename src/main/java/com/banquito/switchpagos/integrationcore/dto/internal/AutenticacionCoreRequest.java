package com.banquito.switchpagos.integrationcore.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AutenticacionCoreRequest(
        String usuario,
        @JsonProperty("contraseña")
        String contrasena
) {
}
