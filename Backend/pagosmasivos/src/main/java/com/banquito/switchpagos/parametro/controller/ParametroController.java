package com.banquito.switchpagos.parametro.controller;

import com.banquito.switchpagos.common.response.ApiResponse;
import com.banquito.switchpagos.parametro.dto.api.HorarioCorteDTO;
import com.banquito.switchpagos.parametro.service.ParametroSwitchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pagos-masivos/horarios-corte")
public class ParametroController {

    private final ParametroSwitchService parametroSwitchService;

    public ParametroController(ParametroSwitchService parametroSwitchService) {
        this.parametroSwitchService = parametroSwitchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HorarioCorteDTO>> obtenerHorarioCorte() {
        HorarioCorteDTO horarioCorteDTO = this.parametroSwitchService.obtenerHorarioCorte();
        return ResponseEntity.ok(ApiResponse.ok("Horarios obtenidos correctamente", horarioCorteDTO));
    }
}
