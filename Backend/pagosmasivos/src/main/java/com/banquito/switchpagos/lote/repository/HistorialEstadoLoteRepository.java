package com.banquito.switchpagos.lote.repository;

import com.banquito.switchpagos.lote.model.HistorialEstadoLote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialEstadoLoteRepository extends JpaRepository<HistorialEstadoLote, Long> {

    List<HistorialEstadoLote> findByLotePagoIdLoteOrderByFechaCambioDesc(Long idLote);
}
