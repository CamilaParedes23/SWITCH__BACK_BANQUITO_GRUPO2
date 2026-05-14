package com.banquito.switchpagos.parametro.repository;

import com.banquito.switchpagos.parametro.model.ParametroSwitch;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametroSwitchRepository extends JpaRepository<ParametroSwitch, String> {

    List<ParametroSwitch> findByCodigoIn(Collection<String> codigos);
}
