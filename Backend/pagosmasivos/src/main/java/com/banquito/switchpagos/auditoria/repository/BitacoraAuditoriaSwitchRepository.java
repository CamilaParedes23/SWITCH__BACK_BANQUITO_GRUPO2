package com.banquito.switchpagos.auditoria.repository;

import com.banquito.switchpagos.auditoria.model.BitacoraAuditoriaSwitch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BitacoraAuditoriaSwitchRepository extends JpaRepository<BitacoraAuditoriaSwitch, Long> {

    List<BitacoraAuditoriaSwitch> findByRucEmpresaOrderByFechaCreacionDesc(String rucEmpresa);
}
