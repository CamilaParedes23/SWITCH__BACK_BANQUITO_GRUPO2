package com.banquito.switchpagos.catalogo.dto.internal;

import com.banquito.switchpagos.common.enums.EstadoTipoServicioEnum;

public record TipoServicioResumenDTO(
        String codigo,
        String nombre,
        EstadoTipoServicioEnum estado) {
}
