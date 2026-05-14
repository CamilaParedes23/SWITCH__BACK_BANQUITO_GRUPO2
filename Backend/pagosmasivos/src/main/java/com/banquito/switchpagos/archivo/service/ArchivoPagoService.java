package com.banquito.switchpagos.archivo.service;

import com.banquito.switchpagos.archivo.dto.internal.ArchivoPagoDTO;

public interface ArchivoPagoService {

    ArchivoPagoDTO analizarContenido(byte[] archivo, String nombreArchivo);
}
