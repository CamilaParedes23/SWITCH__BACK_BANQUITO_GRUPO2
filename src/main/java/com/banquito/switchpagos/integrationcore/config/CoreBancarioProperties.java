package com.banquito.switchpagos.integrationcore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "core")
public class CoreBancarioProperties {

    private String baseUrl;
    private Integration integration = new Integration();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integration getIntegration() {
        return integration;
    }

    public void setIntegration(Integration integration) {
        this.integration = integration;
    }

    public static class Integration {
        private String codigoSubtipoPagoMasivo;
        private String codigoCuentaIngresos;
        private String codigoCuentaIva;
        private String numeroCuentaIngresos;
        private String numeroCuentaIva;

        public String getCodigoSubtipoPagoMasivo() {
            return codigoSubtipoPagoMasivo;
        }

        public void setCodigoSubtipoPagoMasivo(String codigoSubtipoPagoMasivo) {
            this.codigoSubtipoPagoMasivo = codigoSubtipoPagoMasivo;
        }

        public String getCodigoCuentaIngresos() {
            return codigoCuentaIngresos;
        }

        public void setCodigoCuentaIngresos(String codigoCuentaIngresos) {
            this.codigoCuentaIngresos = codigoCuentaIngresos;
        }

        public String getCodigoCuentaIva() {
            return codigoCuentaIva;
        }

        public void setCodigoCuentaIva(String codigoCuentaIva) {
            this.codigoCuentaIva = codigoCuentaIva;
        }

        public String getNumeroCuentaIngresos() {
            return numeroCuentaIngresos;
        }

        public void setNumeroCuentaIngresos(String numeroCuentaIngresos) {
            this.numeroCuentaIngresos = numeroCuentaIngresos;
        }

        public String getNumeroCuentaIva() {
            return numeroCuentaIva;
        }

        public void setNumeroCuentaIva(String numeroCuentaIva) {
            this.numeroCuentaIva = numeroCuentaIva;
        }
    }
}
