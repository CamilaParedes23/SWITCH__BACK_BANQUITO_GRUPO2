package com.banquito.switchpagos.tarifaje.repository;

import com.banquito.switchpagos.tarifaje.model.LiquidacionServicio;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquidacionServicioRepository extends JpaRepository<LiquidacionServicio, Long> {

    Optional<LiquidacionServicio> findByLotePagoUuidLote(UUID uuidLote);
}
