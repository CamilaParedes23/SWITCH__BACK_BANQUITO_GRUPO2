package com.banquito.switchpagos.catalogo.service.impl;

import com.banquito.switchpagos.catalogo.dto.internal.TipoServicioResumenDTO;
import com.banquito.switchpagos.catalogo.model.TipoServicio;
import com.banquito.switchpagos.catalogo.repository.TipoServicioRepository;
import com.banquito.switchpagos.catalogo.service.TipoServicioService;
import com.banquito.switchpagos.common.enums.EstadoTipoServicioEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TipoServicioServiceImpl implements TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;

    public TipoServicioServiceImpl(TipoServicioRepository tipoServicioRepository) {
        this.tipoServicioRepository = tipoServicioRepository;
    }

    @Override
    public List<TipoServicioResumenDTO> obtenerTiposServicioActivos() {
        return this.tipoServicioRepository.findAll().stream()
                .filter(tipoServicio -> EstadoTipoServicioEnum.ACTIVO.equals(tipoServicio.getEstado()))
                .map(this::mapearResumen)
                .toList();
    }

    @Override
    public Optional<TipoServicioResumenDTO> obtenerPorCodigo(String codigo) {
        return this.tipoServicioRepository.findById(codigo)
                .map(this::mapearResumen);
    }

    private TipoServicioResumenDTO mapearResumen(TipoServicio tipoServicio) {
        return new TipoServicioResumenDTO(
                tipoServicio.getCodigo(),
                tipoServicio.getNombre(),
                tipoServicio.getEstado());
    }
}
