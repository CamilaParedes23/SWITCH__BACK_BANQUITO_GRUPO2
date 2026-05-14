package com.banquito.switchpagos.tarifaje.repository;

import com.banquito.switchpagos.common.enums.EstadoTarifaServicioEnum;
import com.banquito.switchpagos.tarifaje.model.TarifaServicio;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TarifaServicioRepository extends JpaRepository<TarifaServicio, Integer> {

    @Query("""
            select tarifa
            from TarifaServicio tarifa
            where tarifa.tipoServicio.codigo = :codigoTipoServicio
              and tarifa.estado = :estado
              and tarifa.rangoDesde <= :cantidadExitosa
              and (tarifa.rangoHasta is null or tarifa.rangoHasta >= :cantidadExitosa)
              and tarifa.vigenteDesde <= :fechaProceso
              and (tarifa.vigenteHasta is null or tarifa.vigenteHasta >= :fechaProceso)
            order by tarifa.rangoDesde desc
            """)
    Optional<TarifaServicio> findTarifaAplicable(
            @Param("codigoTipoServicio") String codigoTipoServicio,
            @Param("cantidadExitosa") Integer cantidadExitosa,
            @Param("estado") EstadoTarifaServicioEnum estado,
            @Param("fechaProceso") LocalDate fechaProceso);

    @Query("""
            select tarifa
            from TarifaServicio tarifa
            where tarifa.estado = :estado
              and tarifa.vigenteDesde <= :fechaProceso
              and (tarifa.vigenteHasta is null or tarifa.vigenteHasta >= :fechaProceso)
            order by tarifa.tipoServicio.codigo asc, tarifa.rangoDesde asc
            """)
    List<TarifaServicio> findTarifasVigentes(
            @Param("estado") EstadoTarifaServicioEnum estado,
            @Param("fechaProceso") LocalDate fechaProceso);

    @Query("""
            select tarifa
            from TarifaServicio tarifa
            where tarifa.tipoServicio.codigo = :codigoTipoServicio
              and tarifa.estado = :estado
              and tarifa.vigenteDesde <= :fechaProceso
              and (tarifa.vigenteHasta is null or tarifa.vigenteHasta >= :fechaProceso)
            order by tarifa.rangoDesde asc
            """)
    List<TarifaServicio> findTarifasVigentesPorTipoServicio(
            @Param("codigoTipoServicio") String codigoTipoServicio,
            @Param("estado") EstadoTarifaServicioEnum estado,
            @Param("fechaProceso") LocalDate fechaProceso);
}
