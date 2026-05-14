package com.banquito.switchpagos.lote.service.impl;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.lote.model.HistorialEstadoLote;
import com.banquito.switchpagos.lote.model.LotePago;
import com.banquito.switchpagos.lote.repository.HistorialEstadoLoteRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class HistorialEstadoLoteRepositoryHelper {

    private HistorialEstadoLoteRepositoryHelper() {
    }

    public static void registrar(
            HistorialEstadoLoteRepository historialEstadoLoteRepository,
            LotePago lotePago,
            EstadoLoteEnum estadoAnterior,
            EstadoLoteEnum estadoNuevo,
            String motivo) {
        HistorialEstadoLote historialEstadoLote = new HistorialEstadoLote();
        historialEstadoLote.setLotePago(lotePago);
        historialEstadoLote.setEstadoAnterior(estadoAnterior);
        historialEstadoLote.setEstadoNuevo(estadoNuevo);
        historialEstadoLote.setMotivo(motivo);
        historialEstadoLote.setFechaCambio(OffsetDateTime.now(ZoneOffset.UTC));
        historialEstadoLoteRepository.save(historialEstadoLote);
    }
}
