package com.banquito.switchpagos.lote.repository;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.lote.model.LotePago;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LotePagoRepository extends JpaRepository<LotePago, Long> {

    Optional<LotePago> findByUuidLote(UUID uuidLote);

    List<LotePago> findByRucEmpresaOrderByFechaRecepcionDesc(String rucEmpresa);

    List<LotePago> findByRucEmpresaAndEstadoOrderByFechaRecepcionDesc(String rucEmpresa, EstadoLoteEnum estado);

    List<LotePago> findByEstadoOrderByFechaRecepcionAsc(EstadoLoteEnum estado);

    @Query("""
            select case when count(lote) > 0 then true else false end
            from LotePago lote
            where lote.rucEmpresa = :rucEmpresa
              and lote.nombreArchivo = :nombreArchivo
              and lote.hashArchivo = :hashArchivo
              and lote.fechaRecepcion >= :fechaInicio
              and lote.estado in :estados
            """)
    Boolean existsDuplicadoReciente(
            @Param("rucEmpresa") String rucEmpresa,
            @Param("nombreArchivo") String nombreArchivo,
            @Param("hashArchivo") String hashArchivo,
            @Param("fechaInicio") OffsetDateTime fechaInicio,
            @Param("estados") Collection<EstadoLoteEnum> estados);
}
