package com.banquito.switchpagos.pricing.service;

import com.banquito.switchpagos.pricing.dto.internal.ProyeccionLiquidacionInternalDto;

public interface ProyeccionLiquidacionService {

    ProyeccionLiquidacionInternalDto calcularProyeccion(String tipoServicio, Integer transaccionesEstimadas);
}
