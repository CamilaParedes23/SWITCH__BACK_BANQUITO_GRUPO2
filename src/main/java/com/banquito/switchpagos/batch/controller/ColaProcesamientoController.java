package com.banquito.switchpagos.batch.controller;

import com.banquito.switchpagos.batch.dto.api.ProcesarPendientesColaResponse;
import com.banquito.switchpagos.batch.service.ColaProcesamientoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.banquito.switchpagos.batch.repository.ColaProcesamientoRepository;

@RestController
@RequestMapping("/api/v1/pagos-masivos/cola")
public class ColaProcesamientoController {

    private final ColaProcesamientoService colaProcesamientoService;
    private final ColaProcesamientoRepository colaProcesamientoRepository;

    public ColaProcesamientoController(ColaProcesamientoService colaProcesamientoService,
            ColaProcesamientoRepository colaProcesamientoRepository) {
        this.colaProcesamientoService = colaProcesamientoService;
        this.colaProcesamientoRepository = colaProcesamientoRepository;
    }

    @PostMapping("/procesar-pendientes")
    public ProcesarPendientesColaResponse procesarPendientes() {
        return colaProcesamientoService.procesarPendientesManual();
    }

    @GetMapping("/todos")
    public Object obtenerTodos() {
        return colaProcesamientoRepository.findAll();
    }
}
