package com.banquito.switchpagos.reporte.service;

import com.banquito.switchpagos.procesamiento.model.LineaPago;
import com.banquito.switchpagos.reporte.dto.api.NotificacionBeneficiarioDTO;
import com.banquito.switchpagos.reporte.dto.api.ResultadoProcesoNotificacionDTO;
import java.util.List;
import java.util.UUID;

public interface NotificacionBeneficiarioService {

    void registrarPagoExitoso(LineaPago lineaPago, String nombreEmpresaEmisora);

    ResultadoProcesoNotificacionDTO procesarPendientes();

    List<NotificacionBeneficiarioDTO> obtenerPorLote(UUID uuidLote);
}
