package com.banquito.switchpagos.parametro.dto.internal;

import com.banquito.switchpagos.common.enums.TipoDatoParametroEnum;

public record ParametroSwitchDTO(
        String codigo,
        String nombre,
        String valorTexto,
        TipoDatoParametroEnum tipoDato) {
}
