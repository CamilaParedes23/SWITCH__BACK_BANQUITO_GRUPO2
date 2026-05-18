package com.banquito.switchpagos.sftp.support;

import com.banquito.switchpagos.integrationcore.dto.internal.AutenticacionCoreResponse;
import org.apache.sshd.common.AttributeRepository;

public final class SftpSessionAttributes {

    public static final AttributeRepository.AttributeKey<AutenticacionCoreResponse> AUTENTICACION_CORE =
            new AttributeRepository.AttributeKey<>();

    private SftpSessionAttributes() {
    }
}
