package com.banquito.switchpagos.procesamiento.service;

import com.banquito.switchpagos.procesamiento.dto.api.ResultadoProcesamientoLoteDTO;
import com.banquito.switchpagos.procesamiento.dto.api.LineaLoteDTO;
import java.util.List;
import java.util.UUID;

public interface ProcesamientoPagoService {

    ResultadoProcesamientoLoteDTO procesar(UUID uuidLote);

    List<LineaLoteDTO> obtenerLineas(UUID uuidLote);
}
