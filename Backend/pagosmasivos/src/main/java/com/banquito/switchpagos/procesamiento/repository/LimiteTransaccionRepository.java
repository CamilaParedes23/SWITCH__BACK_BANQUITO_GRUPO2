package com.banquito.switchpagos.procesamiento.repository;

import com.banquito.switchpagos.common.enums.EstadoLimiteTransaccionEnum;
import com.banquito.switchpagos.procesamiento.model.LimiteTransaccion;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LimiteTransaccionRepository extends JpaRepository<LimiteTransaccion, Integer> {

    @Query("""
            select limite
            from LimiteTransaccion limite
            where limite.tipoServicio.codigo = :codigoTipoServicio
              and limite.estado = :estado
              and limite.vigenteDesde <= :fechaProceso
              and (limite.vigenteHasta is null or limite.vigenteHasta >= :fechaProceso)
            order by limite.vigenteDesde desc
            """)
    Optional<LimiteTransaccion> findVigentePorTipoServicio(
            @Param("codigoTipoServicio") String codigoTipoServicio,
            @Param("estado") EstadoLimiteTransaccionEnum estado,
            @Param("fechaProceso") LocalDate fechaProceso);
}
