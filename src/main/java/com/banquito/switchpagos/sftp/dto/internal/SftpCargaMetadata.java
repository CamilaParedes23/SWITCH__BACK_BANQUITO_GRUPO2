package com.banquito.switchpagos.sftp.dto.internal;

import java.time.OffsetDateTime;

public record SftpCargaMetadata(
        String usuario,
        String rucEmpresa,
        String credencialWebId,
        String clienteId,
        String rolSwitch,
        OffsetDateTime fechaCarga
) {
}
