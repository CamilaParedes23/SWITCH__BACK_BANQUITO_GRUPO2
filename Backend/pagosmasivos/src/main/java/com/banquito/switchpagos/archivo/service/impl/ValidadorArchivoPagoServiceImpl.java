package com.banquito.switchpagos.archivo.service.impl;

import com.banquito.switchpagos.archivo.dto.internal.ArchivoPagoDTO;
import com.banquito.switchpagos.archivo.dto.internal.ResultadoValidacionArchivoDTO;
import com.banquito.switchpagos.archivo.service.ValidadorArchivoPagoService;
import java.math.BigDecimal;
import java.util.HashSet;
import org.springframework.stereotype.Service;

@Service
public class ValidadorArchivoPagoServiceImpl implements ValidadorArchivoPagoService {

    @Override
    public ResultadoValidacionArchivoDTO validar(ArchivoPagoDTO archivoPagoDTO) {
        Integer totalRegistrosDetalle = archivoPagoDTO.detalles().size();
        BigDecimal montoTotalDetalle = archivoPagoDTO.detalles().stream()
                .map(detalleArchivoPagoDTO -> detalleArchivoPagoDTO.monto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (archivoPagoDTO.detalles().isEmpty()) {
            return new ResultadoValidacionArchivoDTO(Boolean.FALSE, "El archivo debe contener al menos un registro DET", totalRegistrosDetalle, montoTotalDetalle);
        }

        boolean secuencialesUnicos = archivoPagoDTO.detalles().stream()
                .map(detalleArchivoPagoDTO -> detalleArchivoPagoDTO.secuencial())
                .allMatch(new HashSet<>()::add);
        if (!secuencialesUnicos) {
            return new ResultadoValidacionArchivoDTO(Boolean.FALSE, "Existen secuenciales duplicados en el detalle", totalRegistrosDetalle, montoTotalDetalle);
        }

        boolean cantidadValida = totalRegistrosDetalle.equals(archivoPagoDTO.totalRegistrosCabecera())
                && totalRegistrosDetalle.equals(archivoPagoDTO.totalRegistrosPie());
        if (!cantidadValida) {
            return new ResultadoValidacionArchivoDTO(Boolean.FALSE, "Las cantidades declaradas en cabecera, detalle y pie no coinciden", totalRegistrosDetalle, montoTotalDetalle);
        }

        boolean montoValido = montoTotalDetalle.compareTo(archivoPagoDTO.montoTotalCabecera()) == 0
                && montoTotalDetalle.compareTo(archivoPagoDTO.montoTotalPie()) == 0;
        if (!montoValido) {
            return new ResultadoValidacionArchivoDTO(Boolean.FALSE, "Las sumatorias monetarias de cabecera, detalle y pie no coinciden", totalRegistrosDetalle, montoTotalDetalle);
        }

        return new ResultadoValidacionArchivoDTO(Boolean.TRUE, "Archivo valido", totalRegistrosDetalle, montoTotalDetalle);
    }
}
