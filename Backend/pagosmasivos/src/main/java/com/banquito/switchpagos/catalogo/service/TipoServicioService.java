package com.banquito.switchpagos.catalogo.service;

import com.banquito.switchpagos.catalogo.dto.internal.TipoServicioResumenDTO;
import java.util.List;
import java.util.Optional;

public interface TipoServicioService {

    List<TipoServicioResumenDTO> obtenerTiposServicioActivos();

    Optional<TipoServicioResumenDTO> obtenerPorCodigo(String codigo);
}
