package com.banquito.switchpagos.batch.repository;

import com.banquito.switchpagos.batch.enums.EstadoColaProcesamiento;
import com.banquito.switchpagos.batch.model.ColaProcesamiento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface ColaProcesamientoRepository extends JpaRepository<ColaProcesamiento, Long> {

    @EntityGraph(attributePaths = {"lotePago", "lotePago.tipoServicio"})
    List<ColaProcesamiento> findByEstadoColaInAndFechaProgramadaProcesoLessThanEqualOrderByPrioridadAscFechaProgramadaProcesoAsc(
            Collection<EstadoColaProcesamiento> estados,
            OffsetDateTime fechaReferencia,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"lotePago", "lotePago.tipoServicio"})
    List<ColaProcesamiento> findByEstadoColaInOrderByPrioridadAscFechaProgramadaProcesoAsc(
            Collection<EstadoColaProcesamiento> estados,
            Pageable pageable
    );
}
