package com.banquito.switchpagos.lote.service;

import com.banquito.switchpagos.lote.dto.api.CargaLoteRequestDTO;
import com.banquito.switchpagos.lote.dto.api.CargaLoteResponseDTO;
import com.banquito.switchpagos.lote.dto.api.AnulacionLoteDTO;
import com.banquito.switchpagos.lote.dto.api.EstadoLoteDTO;
import com.banquito.switchpagos.lote.dto.api.LoteListadoDTO;
import com.banquito.switchpagos.lote.dto.api.ValidacionLoteDTO;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import java.util.List;
import com.banquito.switchpagos.lote.dto.internal.LotePagoResumenDTO;
import java.util.Optional;
import java.util.UUID;

public interface LotePagoService {

    CargaLoteResponseDTO registrarLote(CargaLoteRequestDTO cargaLoteRequestDTO);

    Optional<EstadoLoteDTO> obtenerEstado(UUID uuidLote);

    Optional<LotePagoResumenDTO> obtenerResumen(UUID uuidLote);

    List<LoteListadoDTO> listarLotes(String rucEmpresa, EstadoLoteEnum estado);

    ValidacionLoteDTO validarLote(UUID uuidLote);

    AnulacionLoteDTO anularLote(UUID uuidLote);
}
