package com.banquito.switchpagos.tarifaje.repository;

import com.banquito.switchpagos.tarifaje.model.DetalleLiquidacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleLiquidacionRepository extends JpaRepository<DetalleLiquidacion, Long> {

    List<DetalleLiquidacion> findByLiquidacionServicioIdLiquidacionOrderByFechaCreacionAsc(Long idLiquidacion);
}
