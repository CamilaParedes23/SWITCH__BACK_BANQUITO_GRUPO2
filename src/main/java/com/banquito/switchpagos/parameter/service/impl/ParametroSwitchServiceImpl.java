package com.banquito.switchpagos.parameter.service.impl;

import com.banquito.switchpagos.parameter.config.ParametroSwitchOverrideProperties;
import com.banquito.switchpagos.parameter.constants.CodigoParametroSwitch;
import com.banquito.switchpagos.shared.exception.RecursoNoEncontradoException;
import com.banquito.switchpagos.shared.exception.SolicitudInvalidaException;
import com.banquito.switchpagos.parameter.model.ParametroSwitch;
import com.banquito.switchpagos.parameter.repository.ParametroSwitchRepository;
import com.banquito.switchpagos.parameter.service.ParametroSwitchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Service
@Transactional(readOnly = true)
public class ParametroSwitchServiceImpl implements ParametroSwitchService {

    private final ParametroSwitchRepository parametroSwitchRepository;
    private final ParametroSwitchOverrideProperties overrideProperties;

    public ParametroSwitchServiceImpl(ParametroSwitchRepository parametroSwitchRepository,
                                      ParametroSwitchOverrideProperties overrideProperties) {
        this.parametroSwitchRepository = parametroSwitchRepository;
        this.overrideProperties = overrideProperties;
    }

    @Override
    public String obtenerValorTexto(String codigo) {
        return obtenerParametro(codigo).getValorTexto();
    }

    @Override
    public BigDecimal obtenerDecimal(String codigo) {
        String valorTexto = obtenerValorTexto(codigo);
        try {
            return new BigDecimal(valorTexto);
        } catch (NumberFormatException exception) {
            throw new SolicitudInvalidaException(
                    "PARAMETRO_NUMERICO_INVALIDO",
                    "El parametro " + codigo + " no contiene un valor numerico valido.",
                    exception
            );
        }
    }

    @Override
    public Integer obtenerInteger(String codigo) {
        String valorTexto = obtenerValorTexto(codigo);
        try {
            return Integer.valueOf(valorTexto);
        } catch (NumberFormatException exception) {
            throw new SolicitudInvalidaException(
                    "PARAMETRO_ENTERO_INVALIDO",
                    "El parametro " + codigo + " no contiene un valor entero valido.",
                    exception
            );
        }
    }

    @Override
    public LocalTime obtenerHora(String codigo) {
        String valorTexto = obtenerOverrideHora(codigo);
        if (valorTexto == null || valorTexto.isBlank()) {
            valorTexto = obtenerValorTexto(codigo);
        }
        try {
            return LocalTime.parse(valorTexto);
        } catch (DateTimeParseException exception) {
            throw new SolicitudInvalidaException(
                    "PARAMETRO_HORA_INVALIDO",
                    "El parametro " + codigo + " no contiene una hora valida.",
                    exception
            );
        }
    }

    @Override
    public Boolean obtenerBoolean(String codigo) {
        String valorTexto = obtenerValorTexto(codigo);
        if ("true".equalsIgnoreCase(valorTexto) || "1".equals(valorTexto) || "SI".equalsIgnoreCase(valorTexto)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(valorTexto) || "0".equals(valorTexto) || "NO".equalsIgnoreCase(valorTexto)) {
            return Boolean.FALSE;
        }
        throw new SolicitudInvalidaException(
                "PARAMETRO_BOOLEANO_INVALIDO",
                "El parametro " + codigo + " no contiene un valor booleano valido."
        );
    }

    @Override
    public ParametroSwitch obtenerParametro(String codigo) {
        validarCodigo(codigo);
        return parametroSwitchRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "PARAMETRO_SWITCH_NO_ENCONTRADO",
                        "No existe un parametro del Switch con el codigo " + codigo + "."
                ));
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new SolicitudInvalidaException(
                    "PARAMETRO_CODIGO_REQUERIDO",
                    "El codigo del parametro es obligatorio."
            );
        }
    }

    private String obtenerOverrideHora(String codigo) {
        if (CodigoParametroSwitch.HORA_CORTE_PROCESO.equals(codigo)) {
            return overrideProperties.getHoraCorteProceso();
        }
        if (CodigoParametroSwitch.HORA_INICIO_LOTES_ENCOLADOS.equals(codigo)) {
            return overrideProperties.getHoraInicioLotesEncolados();
        }
        return null;
    }
}
