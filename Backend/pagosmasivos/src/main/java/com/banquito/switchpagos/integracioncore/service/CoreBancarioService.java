package com.banquito.switchpagos.integracioncore.service;

import com.banquito.switchpagos.integracioncore.dto.internal.MovimientoCoreDTO;
import com.banquito.switchpagos.integracioncore.dto.internal.RespuestaCuentaCoreDTO;
import java.math.BigDecimal;
import java.util.Optional;

public interface CoreBancarioService {

    Optional<RespuestaCuentaCoreDTO> obtenerCuenta(String numeroCuenta);

    MovimientoCoreDTO debitar(String numeroCuenta, BigDecimal monto, Boolean permitirSobregiro);

    MovimientoCoreDTO acreditar(String numeroCuenta, BigDecimal monto);
}
