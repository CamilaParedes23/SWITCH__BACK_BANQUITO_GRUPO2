package com.banquito.switchpagos.tarifaje.controller;

import com.banquito.switchpagos.common.response.ApiResponse;
import com.banquito.switchpagos.tarifaje.dto.api.TarifaServicioDTO;
import com.banquito.switchpagos.tarifaje.service.TarifajeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pagos-masivos/tarifas")
public class TarifajeController {

    private final TarifajeService tarifajeService;

    public TarifajeController(TarifajeService tarifajeService) {
        this.tarifajeService = tarifajeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TarifaServicioDTO>>> obtenerTarifas(
            @RequestParam(required = false) String codigoTipoServicio) {
        List<TarifaServicioDTO> tarifas = this.tarifajeService.obtenerTarifas(codigoTipoServicio);
        return ResponseEntity.ok(ApiResponse.ok("Tarifas obtenidas correctamente", tarifas));
    }
}
