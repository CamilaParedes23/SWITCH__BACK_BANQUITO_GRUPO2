package com.banquito.switchpagos.reporte.repository;

import com.banquito.switchpagos.common.enums.TipoReporteEnum;
import com.banquito.switchpagos.reporte.model.ReporteCierre;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteCierreRepository extends JpaRepository<ReporteCierre, Long> {

    List<ReporteCierre> findByLotePagoUuidLoteOrderByFechaGeneracionDesc(UUID uuidLote);

    Optional<ReporteCierre> findByLotePagoUuidLoteAndTipoReporte(UUID uuidLote, TipoReporteEnum tipoReporte);
}
