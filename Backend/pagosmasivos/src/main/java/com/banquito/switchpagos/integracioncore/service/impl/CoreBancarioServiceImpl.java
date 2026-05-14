package com.banquito.switchpagos.integracioncore.service.impl;

import com.banquito.switchpagos.integracioncore.dto.internal.MovimientoCoreDTO;
import com.banquito.switchpagos.integracioncore.dto.internal.RespuestaCuentaCoreDTO;
import com.banquito.switchpagos.integracioncore.service.CoreBancarioService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CoreBancarioServiceImpl implements CoreBancarioService {

    @Override
    public Optional<RespuestaCuentaCoreDTO> obtenerCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.isBlank() || numeroCuenta.startsWith("999")) {
            return Optional.empty();
        }

        String identificacionTitular = numeroCuenta.length() >= 10
                ? numeroCuenta.substring(0, 10)
                : numeroCuenta;

        return Optional.of(new RespuestaCuentaCoreDTO(
                numeroCuenta,
                identificacionTitular,
                Boolean.TRUE,
                new BigDecimal("1000000.00")));
    }

    @Override
    public MovimientoCoreDTO debitar(String numeroCuenta, BigDecimal monto, Boolean permitirSobregiro) {
        return new MovimientoCoreDTO(UUID.randomUUID(), "Debito simulado correctamente");
    }

    @Override
    public MovimientoCoreDTO acreditar(String numeroCuenta, BigDecimal monto) {
        return new MovimientoCoreDTO(UUID.randomUUID(), "Credito simulado correctamente");
    }
}
