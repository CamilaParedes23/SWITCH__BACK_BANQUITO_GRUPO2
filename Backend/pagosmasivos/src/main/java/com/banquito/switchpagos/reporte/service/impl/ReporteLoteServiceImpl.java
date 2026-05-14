package com.banquito.switchpagos.reporte.service.impl;

import com.banquito.switchpagos.common.enums.TipoReporteEnum;
import com.banquito.switchpagos.procesamiento.repository.LineaPagoRepository;
import com.banquito.switchpagos.reporte.dto.api.ComprobanteLiquidacionDTO;
import com.banquito.switchpagos.reporte.dto.api.NovedadLoteDTO;
import com.banquito.switchpagos.reporte.dto.api.ReporteCierreDTO;
import com.banquito.switchpagos.reporte.model.ReporteCierre;
import com.banquito.switchpagos.reporte.repository.ReporteCierreRepository;
import com.banquito.switchpagos.reporte.service.ReporteLoteService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReporteLoteServiceImpl implements ReporteLoteService {

    private final ReporteCierreRepository reporteCierreRepository;
    private final LineaPagoRepository lineaPagoRepository;

    public ReporteLoteServiceImpl(
            ReporteCierreRepository reporteCierreRepository,
            LineaPagoRepository lineaPagoRepository) {
        this.reporteCierreRepository = reporteCierreRepository;
        this.lineaPagoRepository = lineaPagoRepository;
    }

    @Override
    public List<ReporteCierreDTO> obtenerReportes(UUID uuidLote) {
        return this.reporteCierreRepository.findByLotePagoUuidLoteOrderByFechaGeneracionDesc(uuidLote).stream()
                .map(this::mapearReporte)
                .toList();
    }

    @Override
    public List<NovedadLoteDTO> obtenerNovedades(UUID uuidLote) {
        ReporteCierre reporteCierre = this.reporteCierreRepository
                .findByLotePagoUuidLoteAndTipoReporte(uuidLote, TipoReporteEnum.REPORTE_NOVEDADES)
                .orElseThrow(() -> new IllegalArgumentException("No existe reporte de novedades para el lote " + uuidLote));

        return this.lineaPagoRepository.findByLotePagoIdLoteOrderBySecuencialAsc(reporteCierre.getLotePago().getIdLote()).stream()
                .map(lineaPago -> new NovedadLoteDTO(
                        lineaPago.getSecuencial(),
                        lineaPago.getIdentificacionBeneficiario(),
                        lineaPago.getNombreBeneficiario(),
                        lineaPago.getEstado(),
                        lineaPago.getCodigoError(),
                        lineaPago.getMensajeError()))
                .toList();
    }

    @Override
    public Optional<ComprobanteLiquidacionDTO> obtenerComprobante(UUID uuidLote) {
        return this.reporteCierreRepository.findByLotePagoUuidLoteAndTipoReporte(uuidLote, TipoReporteEnum.COMPROBANTE_LIQUIDACION)
                .map(reporteCierre -> new ComprobanteLiquidacionDTO(
                        uuidLote,
                        reporteCierre.getContenidoJson().path("transaccionesExitosas").asInt(),
                        reporteCierre.getContenidoJson().path("transaccionesFallidas").asInt(),
                        reporteCierre.getContenidoJson().path("subtotalComision").decimalValue(),
                        reporteCierre.getContenidoJson().path("montoIva").decimalValue(),
                        reporteCierre.getContenidoJson().path("totalDebitado").decimalValue()));
    }

    private ReporteCierreDTO mapearReporte(ReporteCierre reporteCierre) {
        return new ReporteCierreDTO(
                reporteCierre.getTipoReporte(),
                reporteCierre.getNombreArchivo(),
                reporteCierre.getFormatoArchivo(),
                reporteCierre.getUrlArchivo());
    }
}
