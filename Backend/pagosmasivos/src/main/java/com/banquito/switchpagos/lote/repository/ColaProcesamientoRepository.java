package com.banquito.switchpagos.lote.repository;

import com.banquito.switchpagos.common.enums.EstadoColaProcesamientoEnum;
import com.banquito.switchpagos.lote.model.ColaProcesamiento;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaProcesamientoRepository extends JpaRepository<ColaProcesamiento, Long> {

    Optional<ColaProcesamiento> findByLotePagoIdLote(Long idLote);

    List<ColaProcesamiento> findByEstadoColaAndFechaProgramadaProcesoLessThanEqualOrderByPrioridadAscFechaProgramadaProcesoAsc(
            EstadoColaProcesamientoEnum estadoCola,
            OffsetDateTime fechaProgramadaProceso);
}
