package com.banquito.switchpagos.reporte.service.impl;

import com.banquito.switchpagos.common.enums.EstadoEnvioNotificacionEnum;
import com.banquito.switchpagos.common.enums.TipoNotificacionEnum;
import com.banquito.switchpagos.parametro.model.ParametroSwitch;
import com.banquito.switchpagos.parametro.repository.ParametroSwitchRepository;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
import com.banquito.switchpagos.reporte.dto.api.NotificacionBeneficiarioDTO;
import com.banquito.switchpagos.reporte.dto.api.ResultadoProcesoNotificacionDTO;
import com.banquito.switchpagos.reporte.model.NotificacionBeneficiario;
import com.banquito.switchpagos.reporte.repository.NotificacionBeneficiarioRepository;
import com.banquito.switchpagos.reporte.service.NotificacionBeneficiarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionBeneficiarioServiceImpl implements NotificacionBeneficiarioService {

    private static final String CODIGO_MAX_REINTENTOS = "MAX_REINTENTOS_LOTE";
    private static final Integer MAX_REINTENTOS_POR_DEFECTO = 3;
    private static final Pattern PATRON_CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final NotificacionBeneficiarioRepository notificacionBeneficiarioRepository;
    private final ParametroSwitchRepository parametroSwitchRepository;
    private final ObjectMapper objectMapper;

    public NotificacionBeneficiarioServiceImpl(
            NotificacionBeneficiarioRepository notificacionBeneficiarioRepository,
            ParametroSwitchRepository parametroSwitchRepository,
            ObjectMapper objectMapper) {
        this.notificacionBeneficiarioRepository = notificacionBeneficiarioRepository;
        this.parametroSwitchRepository = parametroSwitchRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registrarPagoExitoso(LineaPago lineaPago, String nombreEmpresaEmisora) {
        if (lineaPago.getCorreoNotificacion() == null || lineaPago.getCorreoNotificacion().isBlank()) {
            return;
        }

        NotificacionBeneficiario notificacionBeneficiario = new NotificacionBeneficiario();
        notificacionBeneficiario.setLineaPago(lineaPago);
        notificacionBeneficiario.setCorreoDestino(lineaPago.getCorreoNotificacion());
        notificacionBeneficiario.setTipoNotificacion(TipoNotificacionEnum.PAGO_EXITOSO);
        notificacionBeneficiario.setAsunto("Pago acreditado correctamente");
        notificacionBeneficiario.setContenido(this.objectMapper.valueToTree(Map.of(
                "montoAcreditado", lineaPago.getMonto(),
                "concepto", lineaPago.getConceptoReferencia(),
                "empresaEmisora", nombreEmpresaEmisora,
                "beneficiario", lineaPago.getNombreBeneficiario())));
        notificacionBeneficiario.setEstadoEnvio(EstadoEnvioNotificacionEnum.PENDIENTE);
        notificacionBeneficiario.setReintentos(0);
        notificacionBeneficiario.setFechaActualizacion(OffsetDateTime.now(ZoneOffset.UTC));
        this.notificacionBeneficiarioRepository.save(notificacionBeneficiario);
    }

    @Override
    @Transactional
    public ResultadoProcesoNotificacionDTO procesarPendientes() {
        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
        List<NotificacionBeneficiario> pendientes = this.notificacionBeneficiarioRepository
                .findByEstadoEnvioAndProximoReintentoEnLessThanEqualOrderByIdNotificacionAsc(EstadoEnvioNotificacionEnum.ERROR, ahora);
        pendientes.addAll(this.notificacionBeneficiarioRepository.findByEstadoEnvioOrderByIdNotificacionAsc(EstadoEnvioNotificacionEnum.PENDIENTE));

        int enviadas = 0;
        int conError = 0;
        for (NotificacionBeneficiario notificacionBeneficiario : pendientes.stream().distinct().toList()) {
            if (procesar(notificacionBeneficiario, ahora)) {
                enviadas++;
            } else {
                conError++;
            }
        }
        return new ResultadoProcesoNotificacionDTO(pendientes.stream().distinct().toList().size(), enviadas, conError);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionBeneficiarioDTO> obtenerPorLote(UUID uuidLote) {
        return this.notificacionBeneficiarioRepository.findByLineaPagoLotePagoUuidLoteOrderByIdNotificacionAsc(uuidLote).stream()
                .map(this::mapearDto)
                .toList();
    }

    private boolean procesar(NotificacionBeneficiario notificacionBeneficiario, OffsetDateTime ahora) {
        Integer maxReintentos = this.parametroSwitchRepository.findById(CODIGO_MAX_REINTENTOS)
                .map(ParametroSwitch::getValorTexto)
                .map(Integer::valueOf)
                .orElse(MAX_REINTENTOS_POR_DEFECTO);

        String correoDestino = notificacionBeneficiario.getCorreoDestino();
        boolean correoValido = correoDestino != null && PATRON_CORREO.matcher(correoDestino).matches();
        boolean simularFallo = correoDestino != null && correoDestino.toLowerCase().contains("fail@");

        if (correoValido && !simularFallo) {
            notificacionBeneficiario.setEstadoEnvio(EstadoEnvioNotificacionEnum.ENVIADA);
            notificacionBeneficiario.setFechaEnvio(ahora);
            notificacionBeneficiario.setErrorEnvio(null);
            notificacionBeneficiario.setProximoReintentoEn(null);
            notificacionBeneficiario.setFechaActualizacion(ahora);
            this.notificacionBeneficiarioRepository.save(notificacionBeneficiario);
            return true;
        }

        Integer nuevoIntento = notificacionBeneficiario.getReintentos() + 1;
        notificacionBeneficiario.setReintentos(nuevoIntento);
        notificacionBeneficiario.setErrorEnvio(correoValido ? "Fallo simulado de notificacion" : "Correo destino invalido");
        notificacionBeneficiario.setFechaActualizacion(ahora);
        if (nuevoIntento >= maxReintentos) {
            notificacionBeneficiario.setEstadoEnvio(EstadoEnvioNotificacionEnum.CANCELADA);
            notificacionBeneficiario.setProximoReintentoEn(null);
        } else {
            notificacionBeneficiario.setEstadoEnvio(EstadoEnvioNotificacionEnum.ERROR);
            notificacionBeneficiario.setProximoReintentoEn(ahora.plusMinutes(5));
        }
        this.notificacionBeneficiarioRepository.save(notificacionBeneficiario);
        return false;
    }

    private NotificacionBeneficiarioDTO mapearDto(NotificacionBeneficiario notificacionBeneficiario) {
        return new NotificacionBeneficiarioDTO(
                notificacionBeneficiario.getIdNotificacion(),
                notificacionBeneficiario.getLineaPago().getSecuencial(),
                notificacionBeneficiario.getCorreoDestino(),
                notificacionBeneficiario.getTipoNotificacion(),
                notificacionBeneficiario.getEstadoEnvio(),
                notificacionBeneficiario.getReintentos(),
                notificacionBeneficiario.getFechaEnvio(),
                notificacionBeneficiario.getErrorEnvio());
    }
}
