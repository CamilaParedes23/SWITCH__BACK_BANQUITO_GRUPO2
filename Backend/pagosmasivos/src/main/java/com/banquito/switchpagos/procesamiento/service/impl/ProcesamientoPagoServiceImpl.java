package com.banquito.switchpagos.procesamiento.service.impl;

import com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum;
import com.banquito.switchpagos.common.enums.EstadoLimiteTransaccionEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.integracioncore.dto.internal.MovimientoCoreDTO;
import com.banquito.switchpagos.integracioncore.dto.internal.RespuestaCuentaCoreDTO;
import com.banquito.switchpagos.integracioncore.service.CoreBancarioService;
import com.banquito.switchpagos.lote.model.LotePago;
import com.banquito.switchpagos.lote.repository.HistorialEstadoLoteRepository;
import com.banquito.switchpagos.lote.repository.LotePagoRepository;
import com.banquito.switchpagos.lote.service.impl.HistorialEstadoLoteRepositoryHelper;
import com.banquito.switchpagos.procesamiento.dto.api.LineaLoteDTO;
import com.banquito.switchpagos.procesamiento.dto.api.ResultadoProcesamientoLoteDTO;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
import com.banquito.switchpagos.procesamiento.model.LimiteTransaccion;
import com.banquito.switchpagos.procesamiento.repository.LineaPagoRepository;
import com.banquito.switchpagos.procesamiento.repository.LimiteTransaccionRepository;
import com.banquito.switchpagos.procesamiento.service.ProcesamientoPagoService;
import com.banquito.switchpagos.reporte.service.NotificacionBeneficiarioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcesamientoPagoServiceImpl implements ProcesamientoPagoService {

    private final LotePagoRepository lotePagoRepository;
    private final LineaPagoRepository lineaPagoRepository;
    private final LimiteTransaccionRepository limiteTransaccionRepository;
    private final HistorialEstadoLoteRepository historialEstadoLoteRepository;
    private final CoreBancarioService coreBancarioService;
    private final NotificacionBeneficiarioService notificacionBeneficiarioService;

    public ProcesamientoPagoServiceImpl(
            LotePagoRepository lotePagoRepository,
            LineaPagoRepository lineaPagoRepository,
            LimiteTransaccionRepository limiteTransaccionRepository,
            HistorialEstadoLoteRepository historialEstadoLoteRepository,
            CoreBancarioService coreBancarioService,
            NotificacionBeneficiarioService notificacionBeneficiarioService) {
        this.lotePagoRepository = lotePagoRepository;
        this.lineaPagoRepository = lineaPagoRepository;
        this.limiteTransaccionRepository = limiteTransaccionRepository;
        this.historialEstadoLoteRepository = historialEstadoLoteRepository;
        this.coreBancarioService = coreBancarioService;
        this.notificacionBeneficiarioService = notificacionBeneficiarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LineaLoteDTO> obtenerLineas(UUID uuidLote) {
        LotePago lotePago = this.lotePagoRepository.findByUuidLote(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        return this.lineaPagoRepository.findByLotePagoIdLoteOrderBySecuencialAsc(lotePago.getIdLote()).stream()
                .map(this::mapearLinea)
                .toList();
    }

    @Override
    @Transactional
    public ResultadoProcesamientoLoteDTO procesar(UUID uuidLote) {
        LotePago lotePago = this.lotePagoRepository.findByUuidLote(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        if (!EstadoLoteEnum.VALIDADO.equals(lotePago.getEstado())) {
            throw new IllegalArgumentException("El lote no se encuentra en un estado valido para procesamiento");
        }

        EstadoLoteEnum estadoAnterior = lotePago.getEstado();
        lotePago.setEstado(EstadoLoteEnum.PROCESANDO);
        lotePago.setFechaInicioProceso(OffsetDateTime.now(ZoneOffset.UTC));
        this.lotePagoRepository.save(lotePago);
        registrarHistorial(lotePago, estadoAnterior, EstadoLoteEnum.PROCESANDO, "Inicio de procesamiento del lote");

        List<LineaPago> lineas = this.lineaPagoRepository.findByLotePagoIdLoteOrderBySecuencialAsc(lotePago.getIdLote());
        LimiteTransaccion limiteTransaccion = this.limiteTransaccionRepository.findVigentePorTipoServicio(
                        lotePago.getTipoServicio().getCodigo(),
                        EstadoLimiteTransaccionEnum.ACTIVO,
                        LocalDate.now(ZoneOffset.UTC))
                .orElseThrow(() -> new IllegalArgumentException("No existe un limite transaccional vigente para el tipo de servicio"));

        RespuestaCuentaCoreDTO cuentaOrigen = this.coreBancarioService.obtenerCuenta(lotePago.getCuentaMatrizCargo())
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuenta matriz en el core"));

        BigDecimal saldoDisponible = cuentaOrigen.saldoDisponible();
        Integer exitosas = 0;
        Integer fallidas = 0;
        BigDecimal montoExitoso = BigDecimal.ZERO;

        for (LineaPago lineaPago : lineas) {
            ResultadoLinea resultadoLinea = procesarLinea(lineaPago, lotePago, limiteTransaccion, saldoDisponible);
            saldoDisponible = resultadoLinea.saldoDisponible();
            if (Boolean.TRUE.equals(resultadoLinea.exitosa())) {
                exitosas++;
                montoExitoso = montoExitoso.add(lineaPago.getMonto());
            } else {
                fallidas++;
            }
        }

        EstadoLoteEnum estadoFinal = fallidas > 0 ? EstadoLoteEnum.PROCESADO_PARCIAL : EstadoLoteEnum.PROCESADO_TOTAL;
        lotePago.setEstado(estadoFinal);
        lotePago.setTotalRegistrosValidados(exitosas);
        lotePago.setTotalRegistrosRechazados(fallidas);
        lotePago.setMontoTotalValidado(montoExitoso);
        lotePago.setFechaFinProceso(OffsetDateTime.now(ZoneOffset.UTC));
        this.lotePagoRepository.save(lotePago);
        registrarHistorial(lotePago, EstadoLoteEnum.PROCESANDO, estadoFinal, "Fin de procesamiento del lote");

        return new ResultadoProcesamientoLoteDTO(uuidLote, estadoFinal.name(), exitosas, fallidas);
    }

    private ResultadoLinea procesarLinea(
            LineaPago lineaPago,
            LotePago lotePago,
            LimiteTransaccion limiteTransaccion,
            BigDecimal saldoDisponible) {
        OffsetDateTime fechaProceso = OffsetDateTime.now(ZoneOffset.UTC);
        lineaPago.setFechaValidacion(fechaProceso);

        if (lineaPago.getMonto().compareTo(limiteTransaccion.getMontoMinimo()) < 0
                || lineaPago.getMonto().compareTo(limiteTransaccion.getMontoMaximo()) > 0) {
            marcarRechazo(lineaPago, "LIMITE_MONTO", "El monto esta fuera de los limites permitidos", fechaProceso);
            return new ResultadoLinea(Boolean.FALSE, saldoDisponible);
        }

        if (saldoDisponible.compareTo(lineaPago.getMonto()) < 0) {
            marcarRechazo(lineaPago, "SALDO_INSUFICIENTE", "No existe saldo suficiente en la cuenta matriz", fechaProceso);
            return new ResultadoLinea(Boolean.FALSE, saldoDisponible);
        }

        RespuestaCuentaCoreDTO cuentaDestino = this.coreBancarioService.obtenerCuenta(lineaPago.getCuentaDestino())
                .orElse(null);
        if (cuentaDestino == null) {
            marcarRechazo(lineaPago, "CUENTA_DESTINO", "La cuenta destino no existe en el core", fechaProceso);
            return new ResultadoLinea(Boolean.FALSE, saldoDisponible);
        }
        if (!Boolean.TRUE.equals(cuentaDestino.permiteDepositos())) {
            marcarRechazo(lineaPago, "CUENTA_NO_PERMITE_DEPOSITO", "La cuenta destino no permite depositos", fechaProceso);
            return new ResultadoLinea(Boolean.FALSE, saldoDisponible);
        }
        if (!cuentaDestino.identificacionTitular().equals(lineaPago.getIdentificacionBeneficiario())) {
            marcarRechazo(lineaPago, "TITULARIDAD_DESTINO", "La cuenta destino no corresponde al beneficiario", fechaProceso);
            return new ResultadoLinea(Boolean.FALSE, saldoDisponible);
        }

        MovimientoCoreDTO debito = this.coreBancarioService.debitar(lotePago.getCuentaMatrizCargo(), lineaPago.getMonto(), Boolean.FALSE);
        MovimientoCoreDTO credito = this.coreBancarioService.acreditar(lineaPago.getCuentaDestino(), lineaPago.getMonto());

        lineaPago.setEstado(EstadoLineaPagoEnum.EXITOSA);
        lineaPago.setUuidDebitoCore(debito.uuidMovimiento());
        lineaPago.setUuidCreditoCore(credito.uuidMovimiento());
        lineaPago.setFechaEnvioCore(fechaProceso);
        lineaPago.setFechaRespuestaCore(fechaProceso);
        lineaPago.setFechaProceso(fechaProceso);
        lineaPago.setFechaActualizacion(fechaProceso);
        lineaPago.setCodigoError(null);
        lineaPago.setMensajeError(null);
        this.lineaPagoRepository.save(lineaPago);
        this.notificacionBeneficiarioService.registrarPagoExitoso(lineaPago, lotePago.getRucEmpresa());

        return new ResultadoLinea(Boolean.TRUE, saldoDisponible.subtract(lineaPago.getMonto()));
    }

    private void marcarRechazo(LineaPago lineaPago, String codigoError, String mensajeError, OffsetDateTime fechaProceso) {
        lineaPago.setEstado(EstadoLineaPagoEnum.RECHAZADA);
        lineaPago.setCodigoError(codigoError);
        lineaPago.setMensajeError(mensajeError);
        lineaPago.setFechaProceso(fechaProceso);
        lineaPago.setFechaActualizacion(fechaProceso);
        this.lineaPagoRepository.save(lineaPago);
    }

    private void registrarHistorial(LotePago lotePago, EstadoLoteEnum estadoAnterior, EstadoLoteEnum estadoNuevo, String motivo) {
        HistorialEstadoLoteRepositoryHelper.registrar(
                this.historialEstadoLoteRepository,
                lotePago,
                estadoAnterior,
                estadoNuevo,
                motivo);
    }

    private LineaLoteDTO mapearLinea(LineaPago lineaPago) {
        return new LineaLoteDTO(
                lineaPago.getSecuencial(),
                lineaPago.getIdentificacionBeneficiario(),
                lineaPago.getNombreBeneficiario(),
                lineaPago.getCuentaDestino(),
                lineaPago.getMonto(),
                lineaPago.getEstado(),
                lineaPago.getCodigoError(),
                lineaPago.getMensajeError(),
                lineaPago.getUuidOperacionSwitch());
    }

    private record ResultadoLinea(Boolean exitosa, BigDecimal saldoDisponible) {
    }
}
