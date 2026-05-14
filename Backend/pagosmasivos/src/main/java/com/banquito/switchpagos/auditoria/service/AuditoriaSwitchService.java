package com.banquito.switchpagos.auditoria.service;

import com.banquito.switchpagos.auditoria.dto.internal.EventoAuditoriaDTO;

public interface AuditoriaSwitchService {

    void registrar(EventoAuditoriaDTO eventoAuditoriaDTO);
}
