package com.banquito.switchpagos.parametro.service;

import com.banquito.switchpagos.parametro.dto.api.HorarioCorteDTO;
import com.banquito.switchpagos.parametro.dto.internal.ParametroSwitchDTO;
import java.util.Optional;

public interface ParametroSwitchService {

    Optional<ParametroSwitchDTO> obtenerPorCodigo(String codigo);

    HorarioCorteDTO obtenerHorarioCorte();
}
