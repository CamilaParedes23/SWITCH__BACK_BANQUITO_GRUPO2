package com.banquito.switchpagos.batch.scheduler;

import com.banquito.switchpagos.batch.service.ColaProcesamientoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "switch.cola.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ColaProcesamientoScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColaProcesamientoScheduler.class);

    private final ColaProcesamientoService colaProcesamientoService;

    public ColaProcesamientoScheduler(ColaProcesamientoService colaProcesamientoService) {
        this.colaProcesamientoService = colaProcesamientoService;
    }

    @Scheduled(fixedDelayString = "${switch.cola.scheduler.fixed-delay-ms:60000}")
    public void procesarPendientesVencidos() {
        LOGGER.debug("Ejecutando ciclo automatico de cola de pagos masivos.");
        colaProcesamientoService.procesarPendientesVencidos();
    }
}
