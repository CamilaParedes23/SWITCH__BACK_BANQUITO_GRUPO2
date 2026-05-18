package com.banquito.switchpagos.sftp.service;

import com.banquito.switchpagos.integrationcore.dto.internal.AutenticacionCoreResponse;
import com.banquito.switchpagos.sftp.dto.internal.SftpCargaMetadata;

import java.io.IOException;
import java.nio.file.Path;

public interface SftpMetadataService {

    void guardarMetadata(Path archivo, AutenticacionCoreResponse autenticacion) throws IOException;

    SftpCargaMetadata leerMetadata(Path archivo) throws IOException;

    Path metadataPath(Path archivo);
}
