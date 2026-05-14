package com.banquito.switchpagos.reporte.service;

import com.banquito.switchpagos.reporte.dto.api.ComprobanteLiquidacionDTO;
import com.banquito.switchpagos.reporte.dto.api.NovedadLoteDTO;
import com.banquito.switchpagos.reporte.dto.api.ReporteCierreDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReporteLoteService {

    List<ReporteCierreDTO> obtenerReportes(UUID uuidLote);

    List<NovedadLoteDTO> obtenerNovedades(UUID uuidLote);

    Optional<ComprobanteLiquidacionDTO> obtenerComprobante(UUID uuidLote);
}
