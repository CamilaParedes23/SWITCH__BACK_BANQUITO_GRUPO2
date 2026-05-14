package com.banquito.switchpagos.reporte.controller;

import com.banquito.switchpagos.common.response.ApiResponse;
import com.banquito.switchpagos.reporte.dto.api.NotificacionBeneficiarioDTO;
import com.banquito.switchpagos.reporte.dto.api.ResultadoProcesoNotificacionDTO;
import com.banquito.switchpagos.reporte.service.NotificacionBeneficiarioService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pagos-masivos")
public class NotificacionBeneficiarioController {

    private final NotificacionBeneficiarioService notificacionBeneficiarioService;

    public NotificacionBeneficiarioController(NotificacionBeneficiarioService notificacionBeneficiarioService) {
        this.notificacionBeneficiarioService = notificacionBeneficiarioService;
    }

    @GetMapping("/lotes/{uuidLote}/notificaciones")
    public ResponseEntity<ApiResponse<List<NotificacionBeneficiarioDTO>>> obtenerNotificaciones(@PathVariable UUID uuidLote) {
        List<NotificacionBeneficiarioDTO> notificaciones = this.notificacionBeneficiarioService.obtenerPorLote(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones obtenidas correctamente", notificaciones));
    }

    @PostMapping("/notificaciones/procesar")
    public ResponseEntity<ApiResponse<ResultadoProcesoNotificacionDTO>> procesarPendientes() {
        ResultadoProcesoNotificacionDTO resultadoProcesoNotificacionDTO = this.notificacionBeneficiarioService.procesarPendientes();
        return ResponseEntity.ok(ApiResponse.ok("Procesamiento de notificaciones ejecutado correctamente", resultadoProcesoNotificacionDTO));
    }
}
