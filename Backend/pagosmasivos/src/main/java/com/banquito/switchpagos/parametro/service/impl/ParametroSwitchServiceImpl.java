package com.banquito.switchpagos.parametro.service.impl;

import com.banquito.switchpagos.parametro.dto.api.HorarioCorteDTO;
import com.banquito.switchpagos.parametro.dto.internal.ParametroSwitchDTO;
import com.banquito.switchpagos.parametro.model.ParametroSwitch;
import com.banquito.switchpagos.parametro.repository.ParametroSwitchRepository;
import com.banquito.switchpagos.parametro.service.ParametroSwitchService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ParametroSwitchServiceImpl implements ParametroSwitchService {

    private static final String CODIGO_HORA_CORTE = "HORA_CORTE_PROCESO";
    private static final String CODIGO_HORA_INICIO_ENCOLADOS = "HORA_INICIO_LOTES_ENCOLADOS";
    private static final String CODIGO_VENTANA_DUPLICIDAD = "VENTANA_DUPLICIDAD_DIAS";
    private static final String CODIGO_MAX_REINTENTOS = "MAX_REINTENTOS_LOTE";

    private final ParametroSwitchRepository parametroSwitchRepository;

    public ParametroSwitchServiceImpl(ParametroSwitchRepository parametroSwitchRepository) {
        this.parametroSwitchRepository = parametroSwitchRepository;
    }

    @Override
    public Optional<ParametroSwitchDTO> obtenerPorCodigo(String codigo) {
        return this.parametroSwitchRepository.findById(codigo)
                .map(this::mapearParametro);
    }

    @Override
    public HorarioCorteDTO obtenerHorarioCorte() {
        Map<String, ParametroSwitch> parametros = this.parametroSwitchRepository.findByCodigoIn(List.of(
                        CODIGO_HORA_CORTE,
                        CODIGO_HORA_INICIO_ENCOLADOS,
                        CODIGO_VENTANA_DUPLICIDAD,
                        CODIGO_MAX_REINTENTOS))
                .stream()
                .collect(Collectors.toMap(ParametroSwitch::getCodigo, parametroSwitch -> parametroSwitch));

        return new HorarioCorteDTO(
                obtenerValor(parametros, CODIGO_HORA_CORTE, "18:00"),
                obtenerValor(parametros, CODIGO_HORA_INICIO_ENCOLADOS, "00:01"),
                Integer.valueOf(obtenerValor(parametros, CODIGO_VENTANA_DUPLICIDAD, "30")),
                Integer.valueOf(obtenerValor(parametros, CODIGO_MAX_REINTENTOS, "3")));
    }

    private ParametroSwitchDTO mapearParametro(ParametroSwitch parametroSwitch) {
        return new ParametroSwitchDTO(
                parametroSwitch.getCodigo(),
                parametroSwitch.getNombre(),
                parametroSwitch.getValorTexto(),
                parametroSwitch.getTipoDato());
    }

    private String obtenerValor(Map<String, ParametroSwitch> parametros, String codigo, String valorPorDefecto) {
        return parametros.containsKey(codigo) ? parametros.get(codigo).getValorTexto() : valorPorDefecto;
    }
}
