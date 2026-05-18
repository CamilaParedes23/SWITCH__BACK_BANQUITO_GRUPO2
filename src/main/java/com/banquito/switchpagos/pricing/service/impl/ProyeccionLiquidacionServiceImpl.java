package com.banquito.switchpagos.pricing.service.impl;

import com.banquito.switchpagos.parameter.constants.CodigoParametroSwitch;
import com.banquito.switchpagos.parameter.service.ParametroSwitchService;
import com.banquito.switchpagos.pricing.dto.internal.ProyeccionLiquidacionInternalDto;
import com.banquito.switchpagos.pricing.enums.EstadoTarifaServicio;
import com.banquito.switchpagos.pricing.model.TarifaServicio;
import com.banquito.switchpagos.pricing.repository.TarifaServicioRepository;
import com.banquito.switchpagos.pricing.service.ProyeccionLiquidacionService;
import com.banquito.switchpagos.shared.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ProyeccionLiquidacionServiceImpl implements ProyeccionLiquidacionService {

    private static final Integer ESCALA_MONTO = 2;

    private final TarifaServicioRepository tarifaServicioRepository;
    private final ParametroSwitchService parametroSwitchService;

    public ProyeccionLiquidacionServiceImpl(TarifaServicioRepository tarifaServicioRepository,
                                            ParametroSwitchService parametroSwitchService) {
        this.tarifaServicioRepository = tarifaServicioRepository;
        this.parametroSwitchService = parametroSwitchService;
    }

    @Override
    public ProyeccionLiquidacionInternalDto calcularProyeccion(String tipoServicio, Integer transaccionesEstimadas) {
        if (transaccionesEstimadas == null || transaccionesEstimadas < 1) {
            return new ProyeccionLiquidacionInternalDto(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        TarifaServicio tarifaServicio = tarifaServicioRepository.buscarTarifaAplicable(
                        tipoServicio,
                        transaccionesEstimadas,
                        EstadoTarifaServicio.ACTIVA,
                        LocalDate.now()
                )
                .orElseThrow(() -> new ReglaNegocioException(
                        "TARIFA_NO_CONFIGURADA",
                        "No existe una tarifa vigente aplicable para el volumen declarado del lote."
                ));

        BigDecimal ivaPorcentaje = parametroSwitchService.obtenerDecimal(CodigoParametroSwitch.IVA_PORCENTAJE);
        BigDecimal tarifaUnitaria = tarifaServicio.getTarifaUnitaria();
        BigDecimal subtotalComision = tarifaUnitaria
                .multiply(BigDecimal.valueOf(transaccionesEstimadas))
                .setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
        BigDecimal montoIva = subtotalComision.multiply(ivaPorcentaje).setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
        BigDecimal totalDebitado = subtotalComision.add(montoIva).setScale(ESCALA_MONTO, RoundingMode.HALF_UP);

        return new ProyeccionLiquidacionInternalDto(
                transaccionesEstimadas,
                tarifaUnitaria,
                ivaPorcentaje,
                subtotalComision,
                montoIva,
                totalDebitado
        );
    }
}
