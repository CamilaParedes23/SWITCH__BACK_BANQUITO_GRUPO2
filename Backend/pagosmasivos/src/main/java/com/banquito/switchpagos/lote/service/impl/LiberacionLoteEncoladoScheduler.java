package com.banquito.switchpagos.lote.service.impl;

import com.banquito.switchpagos.common.enums.EstadoColaProcesamientoEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.lote.model.ColaProcesamiento;
import com.banquito.switchpagos.lote.model.LotePago;
import com.banquito.switchpagos.lote.repository.ColaProcesamientoRepository;
import com.banquito.switchpagos.lote.repository.HistorialEstadoLoteRepository;
import com.banquito.switchpagos.lote.repository.LotePagoRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LiberacionLoteEncoladoScheduler {

    private final ColaProcesamientoRepository colaProcesamientoRepository;
    private final LotePagoRepository lotePagoRepository;
    private final HistorialEstadoLoteRepository historialEstadoLoteRepository;

    public LiberacionLoteEncoladoScheduler(
            ColaProcesamientoRepository colaProcesamientoRepository,
            LotePagoRepository lotePagoRepository,
            HistorialEstadoLoteRepository historialEstadoLoteRepository) {
        this.colaProcesamientoRepository = colaProcesamientoRepository;
        this.lotePagoRepository = lotePagoRepository;
        this.historialEstadoLoteRepository = historialEstadoLoteRepository;
    }

    @Scheduled(fixedDelayString = "${switch.scheduler.cola.delay-ms:60000}")
    @Transactional
    public void liberarLotesEncolados() {
        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
        List<ColaProcesamiento> pendientes = this.colaProcesamientoRepository
                .findByEstadoColaAndFechaProgramadaProcesoLessThanEqualOrderByPrioridadAscFechaProgramadaProcesoAsc(
                        EstadoColaProcesamientoEnum.PENDIENTE,
                        ahora);

        for (ColaProcesamiento colaProcesamiento : pendientes) {
            procesarLiberacion(colaProcesamiento, ahora);
        }
    }

    private void procesarLiberacion(ColaProcesamiento colaProcesamiento, OffsetDateTime ahora) {
        LotePago lotePago = colaProcesamiento.getLotePago();

        if (EstadoLoteEnum.ANULADO.equals(lotePago.getEstado()) || EstadoLoteEnum.CERRADO.equals(lotePago.getEstado())) {
            colaProcesamiento.setEstadoCola(EstadoColaProcesamientoEnum.CANCELADO);
            colaProcesamiento.setFechaActualizacion(ahora);
            colaProcesamiento.setUltimoError("El lote ya no es elegible para liberacion automatica");
            this.colaProcesamientoRepository.save(colaProcesamiento);
            return;
        }

        if (!EstadoLoteEnum.ENCOLADO.equals(lotePago.getEstado())) {
            colaProcesamiento.setEstadoCola(EstadoColaProcesamientoEnum.CANCELADO);
            colaProcesamiento.setFechaActualizacion(ahora);
            colaProcesamiento.setUltimoError("El lote ya no se encuentra encolado");
            this.colaProcesamientoRepository.save(colaProcesamiento);
            return;
        }

        lotePago.setEstado(EstadoLoteEnum.VALIDADO);
        lotePago.setFechaFinValidacion(ahora);
        lotePago.setFechaActualizacion(ahora);
        this.lotePagoRepository.save(lotePago);
        HistorialEstadoLoteRepositoryHelper.registrar(
                this.historialEstadoLoteRepository,
                lotePago,
                EstadoLoteEnum.ENCOLADO,
                EstadoLoteEnum.VALIDADO,
                "Liberacion automatica del lote encolado por ventana operativa");

        colaProcesamiento.setEstadoCola(EstadoColaProcesamientoEnum.COMPLETADO);
        colaProcesamiento.setTomadoEn(ahora);
        colaProcesamiento.setTomadoPor("SCHEDULER_LIBERACION_ENCOLADOS");
        colaProcesamiento.setFechaActualizacion(ahora);
        colaProcesamiento.setUltimoError(null);
        this.colaProcesamientoRepository.save(colaProcesamiento);
    }
}
