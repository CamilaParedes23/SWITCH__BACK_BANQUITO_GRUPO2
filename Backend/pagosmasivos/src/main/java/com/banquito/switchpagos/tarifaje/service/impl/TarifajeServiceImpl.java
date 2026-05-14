package com.banquito.switchpagos.tarifaje.service.impl;

import com.banquito.switchpagos.common.enums.ConceptoDetalleLiquidacionEnum;
import com.banquito.switchpagos.common.enums.EstadoDebitoLiquidacionEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.common.enums.EstadoTarifaServicioEnum;
import com.banquito.switchpagos.common.enums.FormatoReporteEnum;
import com.banquito.switchpagos.common.enums.TipoReporteEnum;
import com.banquito.switchpagos.integracioncore.dto.internal.MovimientoCoreDTO;
import com.banquito.switchpagos.integracioncore.service.CoreBancarioService;
import com.banquito.switchpagos.lote.model.LotePago;
import com.banquito.switchpagos.lote.repository.HistorialEstadoLoteRepository;
import com.banquito.switchpagos.lote.repository.LotePagoRepository;
import com.banquito.switchpagos.lote.service.impl.HistorialEstadoLoteRepositoryHelper;
import com.banquito.switchpagos.parametro.model.ParametroSwitch;
import com.banquito.switchpagos.parametro.repository.ParametroSwitchRepository;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
import com.banquito.switchpagos.procesamiento.repository.LineaPagoRepository;
import com.banquito.switchpagos.reporte.model.ReporteCierre;
import com.banquito.switchpagos.reporte.repository.ReporteCierreRepository;
import com.banquito.switchpagos.tarifaje.dto.api.LiquidacionServicioDTO;
import com.banquito.switchpagos.tarifaje.dto.api.TarifaServicioDTO;
import com.banquito.switchpagos.tarifaje.model.DetalleLiquidacion;
import com.banquito.switchpagos.tarifaje.model.LiquidacionServicio;
import com.banquito.switchpagos.tarifaje.model.TarifaServicio;
import com.banquito.switchpagos.tarifaje.repository.DetalleLiquidacionRepository;
import com.banquito.switchpagos.tarifaje.repository.LiquidacionServicioRepository;
import com.banquito.switchpagos.tarifaje.repository.TarifaServicioRepository;
import com.banquito.switchpagos.tarifaje.service.TarifajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TarifajeServiceImpl implements TarifajeService {

    private static final String CODIGO_IVA = "IVA_PORCENTAJE";
    private static final BigDecimal IVA_POR_DEFECTO = new BigDecimal("0.15");
    private static final String CUENTA_INGRESOS = "INGRESOS_SERVICIOS_MASIVOS";
    private static final String CUENTA_IVA = "PASIVOS_IVA_RETENIDO";

    private final LotePagoRepository lotePagoRepository;
    private final HistorialEstadoLoteRepository historialEstadoLoteRepository;
    private final LineaPagoRepository lineaPagoRepository;
    private final TarifaServicioRepository tarifaServicioRepository;
    private final LiquidacionServicioRepository liquidacionServicioRepository;
    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final ParametroSwitchRepository parametroSwitchRepository;
    private final ReporteCierreRepository reporteCierreRepository;
    private final CoreBancarioService coreBancarioService;
    private final ObjectMapper objectMapper;

    public TarifajeServiceImpl(
            LotePagoRepository lotePagoRepository,
            HistorialEstadoLoteRepository historialEstadoLoteRepository,
            LineaPagoRepository lineaPagoRepository,
            TarifaServicioRepository tarifaServicioRepository,
            LiquidacionServicioRepository liquidacionServicioRepository,
            DetalleLiquidacionRepository detalleLiquidacionRepository,
            ParametroSwitchRepository parametroSwitchRepository,
            ReporteCierreRepository reporteCierreRepository,
            CoreBancarioService coreBancarioService,
            ObjectMapper objectMapper) {
        this.lotePagoRepository = lotePagoRepository;
        this.historialEstadoLoteRepository = historialEstadoLoteRepository;
        this.lineaPagoRepository = lineaPagoRepository;
        this.tarifaServicioRepository = tarifaServicioRepository;
        this.liquidacionServicioRepository = liquidacionServicioRepository;
        this.detalleLiquidacionRepository = detalleLiquidacionRepository;
        this.parametroSwitchRepository = parametroSwitchRepository;
        this.reporteCierreRepository = reporteCierreRepository;
        this.coreBancarioService = coreBancarioService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public LiquidacionServicioDTO liquidar(UUID uuidLote) {
        LotePago lotePago = this.lotePagoRepository.findByUuidLote(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        if (!(EstadoLoteEnum.PROCESADO_TOTAL.equals(lotePago.getEstado())
                || EstadoLoteEnum.PROCESADO_PARCIAL.equals(lotePago.getEstado()))) {
            throw new IllegalArgumentException("El lote solo puede liquidarse cuando se encuentra procesado");
        }

        this.liquidacionServicioRepository.findByLotePagoUuidLote(uuidLote).ifPresent(liquidacionServicio -> {
            throw new IllegalArgumentException("El lote ya fue liquidado");
        });

        Integer exitosas = Math.toIntExact(this.lineaPagoRepository.countByLotePagoIdLoteAndEstado(
                lotePago.getIdLote(), com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum.EXITOSA));
        Integer fallidas = Math.toIntExact(this.lineaPagoRepository.countByLotePagoIdLoteAndEstado(
                lotePago.getIdLote(), com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum.RECHAZADA));

        TarifaServicio tarifaServicio = this.tarifaServicioRepository.findTarifaAplicable(
                        lotePago.getTipoServicio().getCodigo(),
                        exitosas,
                        EstadoTarifaServicioEnum.ACTIVA,
                        LocalDate.now(ZoneOffset.UTC))
                .orElseThrow(() -> new IllegalArgumentException("No existe una tarifa vigente aplicable para el lote"));

        BigDecimal ivaPorcentaje = this.parametroSwitchRepository.findById(CODIGO_IVA)
                .map(ParametroSwitch::getValorTexto)
                .map(BigDecimal::new)
                .orElse(IVA_POR_DEFECTO);

        BigDecimal subtotalComision = tarifaServicio.getTarifaUnitaria().multiply(BigDecimal.valueOf(exitosas)).setScale(4, RoundingMode.HALF_UP);
        BigDecimal montoIva = subtotalComision.multiply(ivaPorcentaje).setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalDebitado = subtotalComision.add(montoIva).setScale(4, RoundingMode.HALF_UP);

        LiquidacionServicio liquidacionServicio = new LiquidacionServicio();
        liquidacionServicio.setLotePago(lotePago);
        liquidacionServicio.setTarifaAplicada(tarifaServicio);
        liquidacionServicio.setTransaccionesExitosas(exitosas);
        liquidacionServicio.setTransaccionesFallidas(fallidas);
        liquidacionServicio.setTarifaUnitariaAplicada(tarifaServicio.getTarifaUnitaria());
        liquidacionServicio.setIvaPorcentajeAplicado(ivaPorcentaje);
        liquidacionServicio.setSubtotalComision(subtotalComision);
        liquidacionServicio.setMontoIva(montoIva);
        liquidacionServicio.setTotalDebitado(totalDebitado);
        liquidacionServicio.setEstadoDebito(EstadoDebitoLiquidacionEnum.COMPLETADO);
        liquidacionServicio.setPermiteSobregiro(Boolean.TRUE);
        liquidacionServicio.setFechaLiquidacion(OffsetDateTime.now(ZoneOffset.UTC));
        liquidacionServicio.setFechaCreacion(OffsetDateTime.now(ZoneOffset.UTC));
        liquidacionServicio.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
        LiquidacionServicio liquidacionPersistida = this.liquidacionServicioRepository.save(liquidacionServicio);

        MovimientoCoreDTO debitoCuentaMatriz = this.coreBancarioService.debitar(lotePago.getCuentaMatrizCargo(), totalDebitado, Boolean.TRUE);
        MovimientoCoreDTO creditoIngresos = this.coreBancarioService.acreditar(CUENTA_INGRESOS, subtotalComision);
        MovimientoCoreDTO creditoIva = this.coreBancarioService.acreditar(CUENTA_IVA, montoIva);

        this.detalleLiquidacionRepository.saveAll(List.of(
                construirDetalle(liquidacionPersistida, ConceptoDetalleLiquidacionEnum.DEBITO_CUENTA_MATRIZ, totalDebitado,
                        debitoCuentaMatriz.uuidMovimiento(), lotePago.getCuentaMatrizCargo(), null),
                construirDetalle(liquidacionPersistida, ConceptoDetalleLiquidacionEnum.CREDITO_INGRESOS, subtotalComision,
                        creditoIngresos.uuidMovimiento(), null, CUENTA_INGRESOS),
                construirDetalle(liquidacionPersistida, ConceptoDetalleLiquidacionEnum.CREDITO_IVA, montoIva,
                        creditoIva.uuidMovimiento(), null, CUENTA_IVA)));

        generarReportes(lotePago, liquidacionPersistida, exitosas, fallidas);

        EstadoLoteEnum estadoAnterior = lotePago.getEstado();
        lotePago.setEstado(EstadoLoteEnum.CERRADO);
        lotePago.setFechaCierre(OffsetDateTime.now(ZoneOffset.UTC));
        this.lotePagoRepository.save(lotePago);
        HistorialEstadoLoteRepositoryHelper.registrar(
                this.historialEstadoLoteRepository,
                lotePago,
                estadoAnterior,
                EstadoLoteEnum.CERRADO,
                "Lote liquidado y cerrado");

        return new LiquidacionServicioDTO(uuidLote, subtotalComision, montoIva, totalDebitado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifaServicioDTO> obtenerTarifas(String codigoTipoServicio) {
        List<TarifaServicio> tarifas = (codigoTipoServicio == null || codigoTipoServicio.isBlank())
                ? this.tarifaServicioRepository.findTarifasVigentes(EstadoTarifaServicioEnum.ACTIVA, LocalDate.now(ZoneOffset.UTC))
                : this.tarifaServicioRepository.findTarifasVigentesPorTipoServicio(
                        codigoTipoServicio,
                        EstadoTarifaServicioEnum.ACTIVA,
                        LocalDate.now(ZoneOffset.UTC));

        return tarifas.stream()
                .map(this::mapearTarifa)
                .toList();
    }

    private DetalleLiquidacion construirDetalle(
            LiquidacionServicio liquidacionServicio,
            ConceptoDetalleLiquidacionEnum concepto,
            BigDecimal monto,
            UUID uuidTransaccionCore,
            String cuentaOrigenCore,
            String cuentaDestinoCore) {
        DetalleLiquidacion detalleLiquidacion = new DetalleLiquidacion();
        detalleLiquidacion.setLiquidacionServicio(liquidacionServicio);
        detalleLiquidacion.setConcepto(concepto);
        detalleLiquidacion.setMonto(monto);
        detalleLiquidacion.setUuidTransaccionCore(uuidTransaccionCore);
        detalleLiquidacion.setCuentaOrigenCore(cuentaOrigenCore);
        detalleLiquidacion.setCuentaDestinoCore(cuentaDestinoCore);
        detalleLiquidacion.setFechaCreacion(OffsetDateTime.now(ZoneOffset.UTC));
        return detalleLiquidacion;
    }

    private void generarReportes(LotePago lotePago, LiquidacionServicio liquidacionServicio, Integer exitosas, Integer fallidas) {
        ReporteCierre comprobante = new ReporteCierre();
        comprobante.setLotePago(lotePago);
        comprobante.setTipoReporte(TipoReporteEnum.COMPROBANTE_LIQUIDACION);
        comprobante.setContenidoJson(this.objectMapper.valueToTree(Map.of(
                "uuidLote", lotePago.getUuidLote(),
                "transaccionesExitosas", exitosas,
                "transaccionesFallidas", fallidas,
                "subtotalComision", liquidacionServicio.getSubtotalComision(),
                "montoIva", liquidacionServicio.getMontoIva(),
                "totalDebitado", liquidacionServicio.getTotalDebitado())));
        comprobante.setNombreArchivo("comprobante-" + lotePago.getUuidLote() + ".json");
        comprobante.setFormatoArchivo(FormatoReporteEnum.JSON);
        comprobante.setUrlArchivo("/api/v1/pagos-masivos/lotes/" + lotePago.getUuidLote() + "/comprobante");
        comprobante.setFechaGeneracion(OffsetDateTime.now(ZoneOffset.UTC));
        comprobante.setDescargadoEmpresa(Boolean.FALSE);
        comprobante.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));

        List<LineaPago> lineas = this.lineaPagoRepository.findByLotePagoIdLoteOrderBySecuencialAsc(lotePago.getIdLote());
        ReporteCierre novedades = new ReporteCierre();
        novedades.setLotePago(lotePago);
        novedades.setTipoReporte(TipoReporteEnum.REPORTE_NOVEDADES);
        novedades.setContenidoJson(this.objectMapper.valueToTree(lineas.stream()
                .map(lineaPago -> Map.of(
                        "secuencial", lineaPago.getSecuencial(),
                        "identificacionBeneficiario", lineaPago.getIdentificacionBeneficiario(),
                        "nombreBeneficiario", lineaPago.getNombreBeneficiario(),
                        "estado", lineaPago.getEstado(),
                        "codigoError", lineaPago.getCodigoError(),
                        "mensajeError", lineaPago.getMensajeError()))
                .toList()));
        novedades.setNombreArchivo("novedades-" + lotePago.getUuidLote() + ".json");
        novedades.setFormatoArchivo(FormatoReporteEnum.JSON);
        novedades.setUrlArchivo("/api/v1/pagos-masivos/lotes/" + lotePago.getUuidLote() + "/novedades");
        novedades.setFechaGeneracion(OffsetDateTime.now(ZoneOffset.UTC));
        novedades.setDescargadoEmpresa(Boolean.FALSE);
        novedades.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));

        this.reporteCierreRepository.saveAll(List.of(comprobante, novedades));
    }

    private TarifaServicioDTO mapearTarifa(TarifaServicio tarifaServicio) {
        return new TarifaServicioDTO(
                tarifaServicio.getTipoServicio().getCodigo(),
                tarifaServicio.getRangoDesde(),
                tarifaServicio.getRangoHasta(),
                tarifaServicio.getTarifaUnitaria(),
                tarifaServicio.getMoneda(),
                tarifaServicio.getVigenteDesde(),
                tarifaServicio.getVigenteHasta());
    }
}
