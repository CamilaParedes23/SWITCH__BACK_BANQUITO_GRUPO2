package com.banquito.switchpagos.auditoria.dto.internal;

import com.banquito.switchpagos.common.enums.TipoActorAuditoriaEnum;

public record EventoAuditoriaDTO(
        TipoActorAuditoriaEnum tipoActor,
        String accion,
        String entidad,
        String idEntidad) {
}
