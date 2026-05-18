package com.banquito.switchpagos.sftp.service.impl;

import com.banquito.switchpagos.integrationcore.dto.internal.AutenticacionCoreResponse;
import com.banquito.switchpagos.sftp.dto.internal.SftpCargaMetadata;
import com.banquito.switchpagos.sftp.service.SftpMetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

@Service
public class SftpMetadataServiceImpl implements SftpMetadataService {

    private final ObjectMapper objectMapper;

    public SftpMetadataServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void guardarMetadata(Path archivo, AutenticacionCoreResponse autenticacion) throws IOException {
        if (autenticacion == null || archivo == null || !esArchivoLote(archivo)) {
            return;
        }
        SftpCargaMetadata metadata = new SftpCargaMetadata(
                autenticacion.usuario(),
                autenticacion.rucEmpresa(),
                autenticacion.credencialWebId(),
                autenticacion.clienteId(),
                autenticacion.rolSwitch(),
                OffsetDateTime.now()
        );
        Path metadataPath = metadataPath(archivo);
        Files.createDirectories(metadataPath.toAbsolutePath().normalize().getParent());
        objectMapper.writeValue(metadataPath.toFile(), metadata);
    }

    @Override
    public SftpCargaMetadata leerMetadata(Path archivo) throws IOException {
        Path metadata = metadataPath(archivo);
        if (!Files.exists(metadata)) {
            throw new IllegalArgumentException("El archivo SFTP no tiene metadata de autenticacion.");
        }
        return objectMapper.readValue(metadata.toFile(), SftpCargaMetadata.class);
    }

    @Override
    public Path metadataPath(Path archivo) {
        return archivo.resolveSibling(archivo.getFileName().toString() + ".meta.json");
    }

    private Boolean esArchivoLote(Path archivo) {
        String nombre = archivo.getFileName().toString().toLowerCase();
        return nombre.endsWith(".csv") || nombre.endsWith(".txt");
    }
}
