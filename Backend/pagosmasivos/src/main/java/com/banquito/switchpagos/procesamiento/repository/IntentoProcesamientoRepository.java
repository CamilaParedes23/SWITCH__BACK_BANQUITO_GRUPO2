package com.banquito.switchpagos.procesamiento.repository;

import com.banquito.switchpagos.procesamiento.model.IntentoProcesamiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntentoProcesamientoRepository extends JpaRepository<IntentoProcesamiento, Long> {

    List<IntentoProcesamiento> findByColaProcesamientoIdColaOrderByNumeroIntentoDesc(Long idCola);
}
