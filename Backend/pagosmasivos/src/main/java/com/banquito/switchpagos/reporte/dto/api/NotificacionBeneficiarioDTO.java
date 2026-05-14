package com.banquito.switchpagos.reporte.dto.api;

import com.banquito.switchpagos.common.enums.EstadoEnvioNotificacionEnum;
import com.banquito.switchpagos.common.enums.TipoNotificacionEnum;
import java.time.OffsetDateTime;

public record NotificacionBeneficiarioDTO(
        Long idNotificacion,
        Integer secuencialLinea,
        String correoDestino,
        TipoNotificacionEnum tipoNotificacion,
        EstadoEnvioNotificacionEnum estadoEnvio,
        Integer reintentos,
        OffsetDateTime fechaEnvio,
        String errorEnvio) {
}
