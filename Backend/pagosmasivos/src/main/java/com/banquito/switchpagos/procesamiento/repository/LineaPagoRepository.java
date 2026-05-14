package com.banquito.switchpagos.procesamiento.repository;

import com.banquito.switchpagos.common.enums.EstadoLineaPagoEnum;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineaPagoRepository extends JpaRepository<LineaPago, Long> {

    List<LineaPago> findByLotePagoIdLoteOrderBySecuencialAsc(Long idLote);

    List<LineaPago> findByLotePagoIdLoteAndEstadoOrderBySecuencialAsc(Long idLote, EstadoLineaPagoEnum estado);

    Optional<LineaPago> findByLotePagoIdLoteAndSecuencial(Long idLote, Integer secuencial);

    Long countByLotePagoIdLoteAndEstado(Long idLote, EstadoLineaPagoEnum estado);
}
