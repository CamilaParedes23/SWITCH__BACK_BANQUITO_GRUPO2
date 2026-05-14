package com.banquito.switchpagos.reporte.repository;

import com.banquito.switchpagos.common.enums.EstadoEnvioNotificacionEnum;
import com.banquito.switchpagos.reporte.model.NotificacionBeneficiario;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionBeneficiarioRepository extends JpaRepository<NotificacionBeneficiario, Long> {

    List<NotificacionBeneficiario> findByEstadoEnvioAndProximoReintentoEnLessThanEqualOrderByIdNotificacionAsc(
            EstadoEnvioNotificacionEnum estadoEnvio,
            OffsetDateTime proximoReintentoEn);

    List<NotificacionBeneficiario> findByEstadoEnvioOrderByIdNotificacionAsc(EstadoEnvioNotificacionEnum estadoEnvio);

    List<NotificacionBeneficiario> findByLineaPagoLotePagoUuidLoteOrderByIdNotificacionAsc(UUID uuidLote);
}
