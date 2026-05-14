package com.banquito.switchpagos.lote.controller;

import com.banquito.switchpagos.common.enums.CanalIngresoEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.common.enums.FormatoArchivoEnum;
import com.banquito.switchpagos.common.response.ApiResponse;
import com.banquito.switchpagos.lote.dto.api.AnulacionLoteDTO;
import com.banquito.switchpagos.lote.dto.api.CargaLoteRequestDTO;
import com.banquito.switchpagos.lote.dto.api.CargaLoteResponseDTO;
import com.banquito.switchpagos.lote.dto.api.EstadoLoteDTO;
import com.banquito.switchpagos.lote.dto.api.LoteListadoDTO;
import com.banquito.switchpagos.lote.dto.api.ValidacionLoteDTO;
import com.banquito.switchpagos.lote.service.LotePagoService;
import com.banquito.switchpagos.procesamiento.dto.api.LineaLoteDTO;
import com.banquito.switchpagos.procesamiento.dto.api.ResultadoProcesamientoLoteDTO;
import com.banquito.switchpagos.procesamiento.service.ProcesamientoPagoService;
import com.banquito.switchpagos.reporte.dto.api.ComprobanteLiquidacionDTO;
import com.banquito.switchpagos.reporte.dto.api.NovedadLoteDTO;
import com.banquito.switchpagos.reporte.service.ReporteLoteService;
import com.banquito.switchpagos.tarifaje.dto.api.LiquidacionServicioDTO;
import com.banquito.switchpagos.tarifaje.service.TarifajeService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pagos-masivos/lotes")
public class LotePagoController {

    private final LotePagoService lotePagoService;
    private final ProcesamientoPagoService procesamientoPagoService;
    private final TarifajeService tarifajeService;
    private final ReporteLoteService reporteLoteService;

    public LotePagoController(
            LotePagoService lotePagoService,
            ProcesamientoPagoService procesamientoPagoService,
            TarifajeService tarifajeService,
            ReporteLoteService reporteLoteService) {
        this.lotePagoService = lotePagoService;
        this.procesamientoPagoService = procesamientoPagoService;
        this.tarifajeService = tarifajeService;
        this.reporteLoteService = reporteLoteService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CargaLoteResponseDTO>> cargarLote(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("canalIngreso") CanalIngresoEnum canalIngreso,
            @RequestParam("formatoArchivo") FormatoArchivoEnum formatoArchivo) throws IOException {
        CargaLoteRequestDTO cargaLoteRequestDTO = new CargaLoteRequestDTO(
                archivo.getOriginalFilename(),
                canalIngreso,
                formatoArchivo,
                archivo.getBytes());

        CargaLoteResponseDTO cargaLoteResponseDTO = this.lotePagoService.registrarLote(cargaLoteRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Lote registrado correctamente", cargaLoteResponseDTO));
    }

    @GetMapping("/{uuidLote}/estado")
    public ResponseEntity<ApiResponse<EstadoLoteDTO>> obtenerEstado(@PathVariable UUID uuidLote) {
        EstadoLoteDTO estadoLoteDTO = this.lotePagoService.obtenerEstado(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe un lote con uuid " + uuidLote));

        return ResponseEntity.ok(ApiResponse.ok("Estado obtenido correctamente", estadoLoteDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoteListadoDTO>>> listarLotes(
            @RequestParam(required = false) String rucEmpresa,
            @RequestParam(required = false) EstadoLoteEnum estado) {
        List<LoteListadoDTO> lotes = this.lotePagoService.listarLotes(rucEmpresa, estado);
        return ResponseEntity.ok(ApiResponse.ok("Lotes obtenidos correctamente", lotes));
    }

    @PostMapping("/{uuidLote}/validar")
    public ResponseEntity<ApiResponse<ValidacionLoteDTO>> validarLote(@PathVariable UUID uuidLote) {
        ValidacionLoteDTO validacionLoteDTO = this.lotePagoService.validarLote(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Validacion ejecutada correctamente", validacionLoteDTO));
    }

    @GetMapping("/{uuidLote}/lineas")
    public ResponseEntity<ApiResponse<List<LineaLoteDTO>>> obtenerLineas(@PathVariable UUID uuidLote) {
        List<LineaLoteDTO> lineas = this.procesamientoPagoService.obtenerLineas(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Lineas obtenidas correctamente", lineas));
    }

    @PostMapping("/{uuidLote}/procesar")
    public ResponseEntity<ApiResponse<ResultadoProcesamientoLoteDTO>> procesarLote(@PathVariable UUID uuidLote) {
        ResultadoProcesamientoLoteDTO resultadoProcesamientoLoteDTO = this.procesamientoPagoService.procesar(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Procesamiento ejecutado correctamente", resultadoProcesamientoLoteDTO));
    }

    @PostMapping("/{uuidLote}/liquidar")
    public ResponseEntity<ApiResponse<LiquidacionServicioDTO>> liquidarLote(@PathVariable UUID uuidLote) {
        LiquidacionServicioDTO liquidacionServicioDTO = this.tarifajeService.liquidar(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Liquidacion ejecutada correctamente", liquidacionServicioDTO));
    }

    @GetMapping("/{uuidLote}/novedades")
    public ResponseEntity<ApiResponse<List<NovedadLoteDTO>>> obtenerNovedades(@PathVariable UUID uuidLote) {
        List<NovedadLoteDTO> novedades = this.reporteLoteService.obtenerNovedades(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Novedades obtenidas correctamente", novedades));
    }

    @GetMapping("/{uuidLote}/comprobante")
    public ResponseEntity<ApiResponse<ComprobanteLiquidacionDTO>> obtenerComprobante(@PathVariable UUID uuidLote) {
        ComprobanteLiquidacionDTO comprobanteLiquidacionDTO = this.reporteLoteService.obtenerComprobante(uuidLote)
                .orElseThrow(() -> new IllegalArgumentException("No existe comprobante para el lote " + uuidLote));
        return ResponseEntity.ok(ApiResponse.ok("Comprobante obtenido correctamente", comprobanteLiquidacionDTO));
    }

    @DeleteMapping("/{uuidLote}")
    public ResponseEntity<ApiResponse<AnulacionLoteDTO>> anularLote(@PathVariable UUID uuidLote) {
        AnulacionLoteDTO anulacionLoteDTO = this.lotePagoService.anularLote(uuidLote);
        return ResponseEntity.ok(ApiResponse.ok("Anulacion ejecutada correctamente", anulacionLoteDTO));
    }
}
