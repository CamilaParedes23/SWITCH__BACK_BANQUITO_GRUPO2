package com.banquito.switchpagos.archivo.service;

import com.banquito.switchpagos.archivo.dto.internal.ArchivoPagoDTO;
import com.banquito.switchpagos.archivo.dto.internal.ResultadoValidacionArchivoDTO;

public interface ValidadorArchivoPagoService {

    ResultadoValidacionArchivoDTO validar(ArchivoPagoDTO archivoPagoDTO);
}
