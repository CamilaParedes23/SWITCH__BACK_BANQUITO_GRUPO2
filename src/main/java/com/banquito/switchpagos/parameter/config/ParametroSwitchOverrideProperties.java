package com.banquito.switchpagos.parameter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "switch.parametros")
public class ParametroSwitchOverrideProperties {

    private String horaCorteProceso;
    private String horaInicioLotesEncolados;

    public String getHoraCorteProceso() {
        return horaCorteProceso;
    }

    public void setHoraCorteProceso(String horaCorteProceso) {
        this.horaCorteProceso = horaCorteProceso;
    }

    public String getHoraInicioLotesEncolados() {
        return horaInicioLotesEncolados;
    }

    public void setHoraInicioLotesEncolados(String horaInicioLotesEncolados) {
        this.horaInicioLotesEncolados = horaInicioLotesEncolados;
    }
}
