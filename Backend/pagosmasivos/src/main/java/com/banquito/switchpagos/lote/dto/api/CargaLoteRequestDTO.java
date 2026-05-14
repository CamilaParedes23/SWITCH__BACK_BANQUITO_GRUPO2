package com.banquito.switchpagos.lote.dto.api;

import com.banquito.switchpagos.common.enums.CanalIngresoEnum;
import com.banquito.switchpagos.common.enums.FormatoArchivoEnum;

public record CargaLoteRequestDTO(
        String nombreArchivo,
        CanalIngresoEnum canalIngreso,
        FormatoArchivoEnum formatoArchivo,
        byte[] contenidoArchivo) {
}
