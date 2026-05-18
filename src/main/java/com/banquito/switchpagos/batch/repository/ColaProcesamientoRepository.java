package com.banquito.switchpagos.batch.repository;

import com.banquito.switchpagos.batch.enums.EstadoColaProcesamiento;
import com.banquito.switchpagos.batch.model.ColaProcesamiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface ColaProcesamientoRepository extends JpaRepository<ColaProcesamiento, Long> {

    @EntityGraph(attributePaths = "lotePago")
    List<ColaProcesamiento> findByEstadoColaInAndFechaProgramadaProcesoLessThanEqualOrderByPrioridadAscFechaProgramadaProcesoAsc(
            Collection<EstadoColaProcesamiento> estados,
            OffsetDateTime fechaReferencia,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "lotePago")
    List<ColaProcesamiento> findByEstadoColaInOrderByPrioridadAscFechaProgramadaProcesoAsc(
            Collection<EstadoColaProcesamiento> estados,
            Pageable pageable
    );
}
