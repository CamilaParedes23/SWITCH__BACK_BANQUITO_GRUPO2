package com.banquito.switchpagos.parametro.dto.api;

public record HorarioCorteDTO(
        String horaCorteProceso,
        String horaInicioLotesEncolados,
        Integer ventanaDuplicidadDias,
        Integer maxReintentosLote) {
}
