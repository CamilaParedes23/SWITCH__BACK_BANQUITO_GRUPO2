package com.banquito.switchpagos.tarifaje.service;

import com.banquito.switchpagos.tarifaje.dto.api.TarifaServicioDTO;
import java.util.List;
import com.banquito.switchpagos.tarifaje.dto.api.LiquidacionServicioDTO;
import java.util.UUID;

public interface TarifajeService {

    LiquidacionServicioDTO liquidar(UUID uuidLote);

    List<TarifaServicioDTO> obtenerTarifas(String codigoTipoServicio);
}
