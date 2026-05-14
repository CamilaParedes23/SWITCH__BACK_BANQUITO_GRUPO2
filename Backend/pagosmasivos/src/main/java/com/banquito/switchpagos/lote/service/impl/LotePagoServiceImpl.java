package com.banquito.switchpagos.lote.service.impl;

import com.banquito.switchpagos.archivo.dto.internal.ArchivoPagoDTO;
import com.banquito.switchpagos.archivo.dto.internal.DetalleArchivoPagoDTO;
import com.banquito.switchpagos.archivo.dto.internal.ResultadoValidacionArchivoDTO;
import com.banquito.switchpagos.archivo.service.ArchivoPagoService;
import com.banquito.switchpagos.archivo.service.ValidadorArchivoPagoService;
import com.banquito.switchpagos.catalogo.model.TipoServicio;
import com.banquito.switchpagos.catalogo.repository.TipoServicioRepository;
import com.banquito.switchpagos.common.enums.EstadoColaProcesamientoEnum;
import com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.common.exception.ValidacionArchivoException;
import com.banquito.switchpagos.lote.dto.api.AnulacionLoteDTO;
import com.banquito.switchpagos.lote.dto.api.CargaLoteRequestDTO;
import com.banquito.switchpagos.lote.dto.api.CargaLoteResponseDTO;
import com.banquito.switchpagos.lote.dto.api.EstadoLoteDTO;
import com.banquito.switchpagos.lote.dto.api.LoteListadoDTO;
import com.banquito.switchpagos.lote.dto.api.ValidacionLoteDTO;
import com.banquito.switchpagos.lote.dto.internal.LotePagoResumenDTO;
import com.banquito.switchpagos.lote.model.ColaProcesamiento;
import com.banquito.switchpagos.lote.model.HistorialEstadoLote;
import com.banquito.switchpagos.lote.model.LotePago;
import com.banquito.switchpagos.lote.repository.ColaProcesamientoRepository;
import com.banquito.switchpagos.lote.repository.HistorialEstadoLoteRepository;
import com.banquito.switchpagos.lote.repository.LotePagoRepository;
import com.banquito.switchpagos.parametro.repository.ParametroSwitchRepository;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
import com.banquito.switchpagos.procesamiento.repository.LineaPagoRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LotePagoServiceImpl implements com.banquito.switchpagos.lote.service.LotePagoService {

    private static final String CODIGO_HORA_CORTE = "HORA_CORTE_PROCESO";
    private static final String CODIGO_VENTANA_DUPLICIDAD = "VENTANA_DUPLICIDAD_DIAS";
    private static final LocalTime HORA_CORTE_POR_DEFECTO = LocalTime.of(18, 0);
    private static final Integer VENTANA_DUPLICIDAD_POR_DEFECTO = 30;

    private final LotePagoRepository lotePagoRepository;
    private final HistorialEstadoLoteRepository historialEstadoLoteRepository;
    private final ColaProcesamientoRepository colaProcesamientoRepository;
    private final LineaPagoRepository lineaPagoRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final ParametroSwitchRepository parametroSwitchRepository;
    private final ArchivoPagoService archivoPagoService;
    private final ValidadorArchivoPagoService validadorArchivoPagoService;

    public LotePagoServiceImpl(
            LotePagoRepository lotePagoRepository,
            HistorialEstadoLoteRepository historialEstadoLoteRepository,
            ColaProcesamientoRepository colaProcesamientoRepository,
            LineaPagoRepository lineaPagoRepository,
            TipoServicioRepository tipoServicioRepository,
            ParametroSwitchRepository parametroSwitchRepository,
            ArchivoPagoService archivoPagoService,
            ValidadorArchivoPagoService validadorArchivoPagoService) {
        this.lotePagoRepository = lotePagoRepository;
        this.historialEstadoLoteRepository = historialEstadoLoteRepository;
        this.colaProcesamientoRepository = colaProcesamientoRepository;
        this.lineaPagoRepository = lineaPagoRepository;
        this.tipoServicioRepository = tipoServicioRepository;
        this.parametroSwitchRepository = parametroSwitchRepository;
        this.archivoPagoService = archivoPagoService;
        this.validadorArchivoPagoService = validadorArchivoPagoService;
    }

    @Override
    @Transactional
    public CargaLoteResponseDTO registrarLote(CargaLoteRequestDTO cargaLoteRequestDTO) {
        ArchivoPagoDTO archivoPagoDTO = this.archivoPagoService.analizarContenido(
                cargaLoteRequestDTO.contenidoArchivo(),
                cargaLoteRequestDTO.nombreArchivo());

        ResultadoValidacionArchivoDTO resultadoValidacionArchivoDTO = this.validadorArchivoPagoService.validar(archivoPagoDTO);
        TipoServicio tipoServicio = this.tipoServicioRepository.findById(archivoPagoDTO.codigoTipoServicio())
                .orElseThrow(() -> new ValidacionArchivoException("No existe el tipo de servicio " + archivoPagoDTO.codigoTipoServicio()));

        OffsetDateTime fechaRecepcion = OffsetDateTime.now(ZoneOffset.UTC);
        validarDuplicidad(archivoPagoDTO, fechaRecepcion);

        EstadoLoteEnum estadoLote = determinarEstadoInicial(fechaRecepcion, resultadoValidacionArchivoDTO.valido());
        LotePago lotePago = construirLote(cargaLoteRequestDTO, archivoPagoDTO, tipoServicio, resultadoValidacionArchivoDTO, fechaRecepcion, estadoLote);
        LotePago lotePersistido = this.lotePagoRepository.save(lotePago);

        guardarHistorial(lotePersistido, null, estadoLote, resultadoValidacionArchivoDTO.mensaje());
        guardarLineas(lotePersistido, archivoPagoDTO.detalles());

        if (EstadoLoteEnum.ENCOLADO.equals(estadoLote)) {
            guardarCola(lotePersistido, fechaRecepcion);
        }

        return new CargaLoteResponseDTO(lotePersistido.getUuidLote(), estadoLote, resultadoValidacionArchivoDTO.mensaje());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EstadoLoteDTO> obtenerEstado(UUID uuidLote) {
        return this.lotePagoRepository.findByUuidLote(uuidLote)
                .map(lotePago -> new EstadoLoteDTO(lotePago.getUuidLote(), lotePago.getEstado(), lotePago.getMotivoRechazoGlobal()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LotePagoResumenDTO> obtenerResumen(UUID uuidLote) {
        return this.lotePagoRepository.findByUuidLote(uuidLote)
                .map(lotePago -> new LotePagoResumenDTO(
                        lotePago.getUuidLote(),
                        lotePago.getRucEmpresa(),
                        lotePago.getEstado(),
                        lotePago.getTotalRegistrosDeclarado(),
                        lotePago.getMontoTotalDeclarado()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteListadoDTO> listarLotes(String rucEmpresa, EstadoLoteEnum estado) {
        List<LotePago> lotes;
        if (rucEmpresa != null && !rucEmpresa.isBlank() && estado != null) {
            lotes = this.lotePagoRepository.findByRucEmpresaAndEstadoOrderByFechaRecepcionDesc(rucEmpresa, estado);
        } else if (rucEmpresa != null && !rucEmpresa.isBlank()) {
            lotes = this.lotePagoRepository.findByRucEmpresaOrderByFechaRecepcionDesc(rucEmpresa);
        } else if (estado != null) {
            lotes = this.lotePagoRepository.findByEstadoOrderByFechaRecepcionAsc(estado);
        } else {
            lotes = this.lotePagoRepository.findAll();
        }

        return lotes.stream()
                .map(this::mapearListado)
                .toList();
    }

    @Override
    @Transactional
    public ValidacionLoteDTO validarLote(UUID uuidLote) {
        LotePago lotePago = this.lotePagoRepository.findByUuidLote(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        EstadoLoteEnum estadoAnterior = lotePago.getEstado();
        if (EstadoLoteEnum.VALIDADO.equals(estadoAnterior)) {
            return new ValidacionLoteDTO(uuidLote, estadoAnterior, estadoAnterior, "El lote ya se encuentra validado");
        }
        if (EstadoLoteEnum.ENCOLADO.equals(estadoAnterior)) {
            throw new IllegalArgumentException("El lote encolado solo puede pasar a VALIDADO cuando llegue su ventana operativa");
        }
        if (!EstadoLoteEnum.RECIBIDO.equals(estadoAnterior)) {
            throw new IllegalArgumentException("El lote no puede validarse en el estado actual " + estadoAnterior);
        }

        EstadoLoteEnum nuevoEstado = EstadoLoteEnum.VALIDADO;

        lotePago.setEstado(nuevoEstado);
        lotePago.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
        lotePago.setFechaFinValidacion(OffsetDateTime.now(ZoneOffset.UTC));
        this.lotePagoRepository.save(lotePago);
        guardarHistorial(lotePago, estadoAnterior, nuevoEstado, "Validacion manual del lote");

        return new ValidacionLoteDTO(uuidLote, estadoAnterior, nuevoEstado, "Lote validado correctamente");
    }

    @Override
    @Transactional
    public AnulacionLoteDTO anularLote(UUID uuidLote) {
        LotePago lotePago = this.lotePagoRepository.findByUuidLote(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        EstadoLoteEnum estadoAnterior = lotePago.getEstado();
        if (!(EstadoLoteEnum.RECIBIDO.equals(estadoAnterior)
                || EstadoLoteEnum.VALIDADO.equals(estadoAnterior)
                || EstadoLoteEnum.ENCOLADO.equals(estadoAnterior))) {
            throw new IllegalArgumentException("El lote no puede anularse en el estado actual " + estadoAnterior);
        }

        lotePago.setEstado(EstadoLoteEnum.ANULADO);
        lotePago.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
        this.lotePagoRepository.save(lotePago);
        guardarHistorial(lotePago, estadoAnterior, EstadoLoteEnum.ANULADO, "Lote anulado manualmente");

        this.colaProcesamientoRepository.findByLotePagoIdLote(lotePago.getIdLote())
                .ifPresent(colaProcesamiento -> {
                    colaProcesamiento.setEstadoCola(EstadoColaProcesamientoEnum.CANCELADO);
                    colaProcesamiento.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
                    this.colaProcesamientoRepository.save(colaProcesamiento);
                });

        return new AnulacionLoteDTO(uuidLote, estadoAnterior, EstadoLoteEnum.ANULADO, "Lote anulado correctamente");
    }

    private void validarDuplicidad(ArchivoPagoDTO archivoPagoDTO, OffsetDateTime fechaRecepcion) {
        Integer ventanaDuplicidad = this.parametroSwitchRepository.findById(CODIGO_VENTANA_DUPLICIDAD)
                .map(parametroSwitch -> Integer.valueOf(parametroSwitch.getValorTexto()))
                .orElse(VENTANA_DUPLICIDAD_POR_DEFECTO);

        boolean duplicado = Boolean.TRUE.equals(this.lotePagoRepository.existsDuplicadoReciente(
                archivoPagoDTO.rucEmpresa(),
                archivoPagoDTO.nombreArchivo(),
                archivoPagoDTO.hashArchivo(),
                fechaRecepcion.minusDays(ventanaDuplicidad),
                List.of(EstadoLoteEnum.RECIBIDO, EstadoLoteEnum.VALIDADO, EstadoLoteEnum.PROCESANDO, EstadoLoteEnum.PROCESADO_PARCIAL, EstadoLoteEnum.PROCESADO_TOTAL, EstadoLoteEnum.CERRADO)));

        if (duplicado) {
            throw new ValidacionArchivoException("Ya existe un lote procesado o recibido con el mismo nombre y hash dentro de la ventana configurada");
        }
    }

    private EstadoLoteEnum determinarEstadoInicial(OffsetDateTime fechaRecepcion, Boolean valido) {
        if (!Boolean.TRUE.equals(valido)) {
            return EstadoLoteEnum.RECHAZADO;
        }

        LocalTime horaCorte = this.parametroSwitchRepository.findById(CODIGO_HORA_CORTE)
                .map(parametroSwitch -> LocalTime.parse(parametroSwitch.getValorTexto()))
                .orElse(HORA_CORTE_POR_DEFECTO);

        DayOfWeek diaSemana = fechaRecepcion.getDayOfWeek();
        boolean finDeSemana = DayOfWeek.SATURDAY.equals(diaSemana) || DayOfWeek.SUNDAY.equals(diaSemana);
        boolean fueraDeHorario = !fechaRecepcion.toLocalTime().isBefore(horaCorte);

        return (finDeSemana || fueraDeHorario) ? EstadoLoteEnum.ENCOLADO : EstadoLoteEnum.RECIBIDO;
    }

    private LotePago construirLote(
            CargaLoteRequestDTO cargaLoteRequestDTO,
            ArchivoPagoDTO archivoPagoDTO,
            TipoServicio tipoServicio,
            ResultadoValidacionArchivoDTO resultadoValidacionArchivoDTO,
            OffsetDateTime fechaRecepcion,
            EstadoLoteEnum estadoLote) {
        LotePago lotePago = new LotePago();
        lotePago.setUuidLote(UUID.randomUUID());
        lotePago.setClaveIdempotencia(UUID.randomUUID());
        lotePago.setRucEmpresa(archivoPagoDTO.rucEmpresa());
        lotePago.setTipoServicio(tipoServicio);
        lotePago.setCuentaMatrizCargo(archivoPagoDTO.cuentaMatrizCargo());
        lotePago.setFechaHoraGeneracion(archivoPagoDTO.fechaHoraGeneracion());
        lotePago.setTotalRegistrosDeclarado(archivoPagoDTO.totalRegistrosCabecera());
        lotePago.setMontoTotalDeclarado(archivoPagoDTO.montoTotalCabecera());
        lotePago.setTotalRegistrosPie(archivoPagoDTO.totalRegistrosPie());
        lotePago.setMontoTotalPie(archivoPagoDTO.montoTotalPie());
        lotePago.setTotalRegistrosValidados(resultadoValidacionArchivoDTO.totalRegistrosDetalle());
        lotePago.setTotalRegistrosRechazados(0);
        lotePago.setMontoTotalValidado(resultadoValidacionArchivoDTO.montoTotalDetalle());
        lotePago.setNombreArchivo(cargaLoteRequestDTO.nombreArchivo());
        lotePago.setHashArchivo(archivoPagoDTO.hashArchivo());
        lotePago.setHashPieControl(archivoPagoDTO.hashPieControl());
        lotePago.setTamanoBytes(Long.valueOf(cargaLoteRequestDTO.contenidoArchivo().length));
        lotePago.setFormatoArchivo(cargaLoteRequestDTO.formatoArchivo());
        lotePago.setCanalIngreso(cargaLoteRequestDTO.canalIngreso());
        lotePago.setEstado(estadoLote);
        lotePago.setMotivoRechazoGlobal(Boolean.TRUE.equals(resultadoValidacionArchivoDTO.valido()) ? null : resultadoValidacionArchivoDTO.mensaje());
        lotePago.setFechaRecepcion(fechaRecepcion);
        lotePago.setFechaInicioValidacion(fechaRecepcion);
        lotePago.setFechaFinValidacion(fechaRecepcion);
        return lotePago;
    }

    private void guardarHistorial(LotePago lotePago, EstadoLoteEnum estadoAnterior, EstadoLoteEnum estadoNuevo, String motivo) {
        HistorialEstadoLote historialEstadoLote = new HistorialEstadoLote();
        historialEstadoLote.setLotePago(lotePago);
        historialEstadoLote.setEstadoAnterior(estadoAnterior);
        historialEstadoLote.setEstadoNuevo(estadoNuevo);
        historialEstadoLote.setMotivo(motivo);
        historialEstadoLote.setFechaCambio(OffsetDateTime.now(ZoneOffset.UTC));
        this.historialEstadoLoteRepository.save(historialEstadoLote);
    }

    private void guardarLineas(LotePago lotePago, List<DetalleArchivoPagoDTO> detalles) {
        List<LineaPago> lineas = detalles.stream()
                .map(detalleArchivoPagoDTO -> construirLinea(lotePago, detalleArchivoPagoDTO))
                .toList();
        this.lineaPagoRepository.saveAll(lineas);
    }

    private LineaPago construirLinea(LotePago lotePago, DetalleArchivoPagoDTO detalleArchivoPagoDTO) {
        LineaPago lineaPago = new LineaPago();
        lineaPago.setLotePago(lotePago);
        lineaPago.setSecuencial(detalleArchivoPagoDTO.secuencial());
        lineaPago.setIdentificacionBeneficiario(detalleArchivoPagoDTO.identificacionBeneficiario());
        lineaPago.setNombreBeneficiario(detalleArchivoPagoDTO.nombreBeneficiario());
        lineaPago.setCuentaDestino(detalleArchivoPagoDTO.cuentaDestino());
        lineaPago.setMonto(detalleArchivoPagoDTO.monto());
        lineaPago.setConceptoReferencia(detalleArchivoPagoDTO.conceptoReferencia());
        lineaPago.setCorreoNotificacion(detalleArchivoPagoDTO.correoNotificacion());
        lineaPago.setEstado(EstadoLineaPagoEnum.PENDIENTE);
        lineaPago.setUuidOperacionSwitch(UUID.randomUUID());
        return lineaPago;
    }

    private void guardarCola(LotePago lotePago, OffsetDateTime fechaRecepcion) {
        ColaProcesamiento colaProcesamiento = new ColaProcesamiento();
        colaProcesamiento.setLotePago(lotePago);
        colaProcesamiento.setFechaEncolado(fechaRecepcion);
        colaProcesamiento.setFechaHabilProgramada(calcularSiguienteDiaHabil(fechaRecepcion.toLocalDate()));
        colaProcesamiento.setFechaProgramadaProceso(calcularFechaProgramadaProceso(fechaRecepcion.toLocalDate()));
        colaProcesamiento.setEstadoCola(EstadoColaProcesamientoEnum.PENDIENTE);
        colaProcesamiento.setPrioridad(5);
        colaProcesamiento.setIntentos(0);
        colaProcesamiento.setMaxIntentos(3);
        this.colaProcesamientoRepository.save(colaProcesamiento);
    }

    private LoteListadoDTO mapearListado(LotePago lotePago) {
        return new LoteListadoDTO(
                lotePago.getUuidLote(),
                lotePago.getRucEmpresa(),
                lotePago.getNombreArchivo(),
                lotePago.getEstado(),
                lotePago.getCanalIngreso(),
                lotePago.getFechaRecepcion());
    }

    private LocalDate calcularSiguienteDiaHabil(LocalDate fechaBase) {
        LocalDate fecha = fechaBase.plusDays(1);
        while (DayOfWeek.SATURDAY.equals(fecha.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(fecha.getDayOfWeek())) {
            fecha = fecha.plusDays(1);
        }
        return fecha;
    }

    private OffsetDateTime calcularFechaProgramadaProceso(LocalDate fechaBase) {
        return calcularSiguienteDiaHabil(fechaBase).atTime(0, 1).atOffset(ZoneOffset.UTC);
    }
}
