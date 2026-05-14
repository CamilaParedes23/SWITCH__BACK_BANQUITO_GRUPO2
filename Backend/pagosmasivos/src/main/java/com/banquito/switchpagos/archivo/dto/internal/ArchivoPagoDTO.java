package com.banquito.switchpagos.archivo.dto.internal;

import com.banquito.switchpagos.common.enums.FormatoArchivoEnum;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ArchivoPagoDTO(
        String nombreArchivo,
        FormatoArchivoEnum formatoArchivo,
        String contenidoPlano,
        String hashArchivo,
        String rucEmpresa,
        String codigoTipoServicio,
        OffsetDateTime fechaHoraGeneracion,
        String cuentaMatrizCargo,
        Integer totalRegistrosCabecera,
        BigDecimal montoTotalCabecera,
        Integer totalRegistrosPie,
        BigDecimal montoTotalPie,
        String hashPieControl,
        List<DetalleArchivoPagoDTO> detalles) {
}
