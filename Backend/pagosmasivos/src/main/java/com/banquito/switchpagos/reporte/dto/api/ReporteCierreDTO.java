package com.banquito.switchpagos.reporte.dto.api;

import com.banquito.switchpagos.common.enums.FormatoReporteEnum;
import com.banquito.switchpagos.common.enums.TipoReporteEnum;

public record ReporteCierreDTO(
        TipoReporteEnum tipoReporte,
        String nombreArchivo,
        FormatoReporteEnum formatoArchivo,
        String urlArchivo) {
}
