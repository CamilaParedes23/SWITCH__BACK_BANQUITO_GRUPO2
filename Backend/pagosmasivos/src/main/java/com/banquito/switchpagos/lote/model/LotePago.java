package com.banquito.switchpagos.lote.model;

import com.banquito.switchpagos.catalogo.model.TipoServicio;
import com.banquito.switchpagos.common.enums.CanalIngresoEnum;
import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
import com.banquito.switchpagos.common.enums.FormatoArchivoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(schema = "switch_banquito", name = "LOTE_PAGO")
public class LotePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOTE", nullable = false)
    private Long idLote;

    @Column(name = "UUID_LOTE", nullable = false)
    private UUID uuidLote;

    @Column(name = "CLAVE_IDEMPOTENCIA", nullable = false)
    private UUID claveIdempotencia;

    @Column(name = "RUC_EMPRESA", nullable = false, length = 13)
    private String rucEmpresa;

    @Column(name = "ID_CREDENCIAL_WEB_CORE")
    private Integer idCredencialWebCore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIPO_SERVICIO", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "CUENTA_MATRIZ_CARGO", nullable = false, length = 20)
    private String cuentaMatrizCargo;

    @Column(name = "FECHA_HORA_GENERACION", nullable = false)
    private OffsetDateTime fechaHoraGeneracion;

    @Column(name = "TOTAL_REGISTROS_DECLARADO", nullable = false)
    private Integer totalRegistrosDeclarado;

    @Column(name = "MONTO_TOTAL_DECLARADO", nullable = false, precision = 19, scale = 4)
    private BigDecimal montoTotalDeclarado;

    @Column(name = "TOTAL_REGISTROS_PIE")
    private Integer totalRegistrosPie;

    @Column(name = "MONTO_TOTAL_PIE", precision = 19, scale = 4)
    private BigDecimal montoTotalPie;

    @Column(name = "TOTAL_REGISTROS_VALIDADOS")
    private Integer totalRegistrosValidados;

    @Column(name = "TOTAL_REGISTROS_RECHAZADOS")
    private Integer totalRegistrosRechazados;

    @Column(name = "MONTO_TOTAL_VALIDADO", precision = 19, scale = 4)
    private BigDecimal montoTotalValidado;

    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "HASH_ARCHIVO", nullable = false, length = 128)
    private String hashArchivo;

    @Column(name = "HASH_PIE_CONTROL", length = 128)
    private String hashPieControl;

    @Column(name = "TAMANO_BYTES")
    private Long tamanoBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "FORMATO_ARCHIVO", nullable = false, length = 10)
    private FormatoArchivoEnum formatoArchivo;

    @Column(name = "RUTA_ALMACENAMIENTO", length = 500)
    private String rutaAlmacenamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL_INGRESO", nullable = false, length = 15)
    private CanalIngresoEnum canalIngreso;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 25)
    private EstadoLoteEnum estado;

    @Column(name = "MOTIVO_RECHAZO_GLOBAL", length = 500)
    private String motivoRechazoGlobal;

    @Column(name = "FECHA_RECEPCION", nullable = false)
    private OffsetDateTime fechaRecepcion;

    @Column(name = "FECHA_INICIO_VALIDACION")
    private OffsetDateTime fechaInicioValidacion;

    @Column(name = "FECHA_FIN_VALIDACION")
    private OffsetDateTime fechaFinValidacion;

    @Column(name = "FECHA_INICIO_PROCESO")
    private OffsetDateTime fechaInicioProceso;

    @Column(name = "FECHA_FIN_PROCESO")
    private OffsetDateTime fechaFinProceso;

    @Column(name = "FECHA_CIERRE")
    private OffsetDateTime fechaCierre;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public LotePago() {
    }

    public LotePago(Long idLote) {
        this.idLote = idLote;
    }

    public Long getIdLote() { return idLote; }
    public void setIdLote(Long idLote) { this.idLote = idLote; }
    public UUID getUuidLote() { return uuidLote; }
    public void setUuidLote(UUID uuidLote) { this.uuidLote = uuidLote; }
    public UUID getClaveIdempotencia() { return claveIdempotencia; }
    public void setClaveIdempotencia(UUID claveIdempotencia) { this.claveIdempotencia = claveIdempotencia; }
    public String getRucEmpresa() { return rucEmpresa; }
    public void setRucEmpresa(String rucEmpresa) { this.rucEmpresa = rucEmpresa; }
    public Integer getIdCredencialWebCore() { return idCredencialWebCore; }
    public void setIdCredencialWebCore(Integer idCredencialWebCore) { this.idCredencialWebCore = idCredencialWebCore; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }
    public String getCuentaMatrizCargo() { return cuentaMatrizCargo; }
    public void setCuentaMatrizCargo(String cuentaMatrizCargo) { this.cuentaMatrizCargo = cuentaMatrizCargo; }
    public OffsetDateTime getFechaHoraGeneracion() { return fechaHoraGeneracion; }
    public void setFechaHoraGeneracion(OffsetDateTime fechaHoraGeneracion) { this.fechaHoraGeneracion = fechaHoraGeneracion; }
    public Integer getTotalRegistrosDeclarado() { return totalRegistrosDeclarado; }
    public void setTotalRegistrosDeclarado(Integer totalRegistrosDeclarado) { this.totalRegistrosDeclarado = totalRegistrosDeclarado; }
    public BigDecimal getMontoTotalDeclarado() { return montoTotalDeclarado; }
    public void setMontoTotalDeclarado(BigDecimal montoTotalDeclarado) { this.montoTotalDeclarado = montoTotalDeclarado; }
    public Integer getTotalRegistrosPie() { return totalRegistrosPie; }
    public void setTotalRegistrosPie(Integer totalRegistrosPie) { this.totalRegistrosPie = totalRegistrosPie; }
    public BigDecimal getMontoTotalPie() { return montoTotalPie; }
    public void setMontoTotalPie(BigDecimal montoTotalPie) { this.montoTotalPie = montoTotalPie; }
    public Integer getTotalRegistrosValidados() { return totalRegistrosValidados; }
    public void setTotalRegistrosValidados(Integer totalRegistrosValidados) { this.totalRegistrosValidados = totalRegistrosValidados; }
    public Integer getTotalRegistrosRechazados() { return totalRegistrosRechazados; }
    public void setTotalRegistrosRechazados(Integer totalRegistrosRechazados) { this.totalRegistrosRechazados = totalRegistrosRechazados; }
    public BigDecimal getMontoTotalValidado() { return montoTotalValidado; }
    public void setMontoTotalValidado(BigDecimal montoTotalValidado) { this.montoTotalValidado = montoTotalValidado; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getHashArchivo() { return hashArchivo; }
    public void setHashArchivo(String hashArchivo) { this.hashArchivo = hashArchivo; }
    public String getHashPieControl() { return hashPieControl; }
    public void setHashPieControl(String hashPieControl) { this.hashPieControl = hashPieControl; }
    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }
    public FormatoArchivoEnum getFormatoArchivo() { return formatoArchivo; }
    public void setFormatoArchivo(FormatoArchivoEnum formatoArchivo) { this.formatoArchivo = formatoArchivo; }
    public String getRutaAlmacenamiento() { return rutaAlmacenamiento; }
    public void setRutaAlmacenamiento(String rutaAlmacenamiento) { this.rutaAlmacenamiento = rutaAlmacenamiento; }
    public CanalIngresoEnum getCanalIngreso() { return canalIngreso; }
    public void setCanalIngreso(CanalIngresoEnum canalIngreso) { this.canalIngreso = canalIngreso; }
    public EstadoLoteEnum getEstado() { return estado; }
    public void setEstado(EstadoLoteEnum estado) { this.estado = estado; }
    public String getMotivoRechazoGlobal() { return motivoRechazoGlobal; }
    public void setMotivoRechazoGlobal(String motivoRechazoGlobal) { this.motivoRechazoGlobal = motivoRechazoGlobal; }
    public OffsetDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(OffsetDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
    public OffsetDateTime getFechaInicioValidacion() { return fechaInicioValidacion; }
    public void setFechaInicioValidacion(OffsetDateTime fechaInicioValidacion) { this.fechaInicioValidacion = fechaInicioValidacion; }
    public OffsetDateTime getFechaFinValidacion() { return fechaFinValidacion; }
    public void setFechaFinValidacion(OffsetDateTime fechaFinValidacion) { this.fechaFinValidacion = fechaFinValidacion; }
    public OffsetDateTime getFechaInicioProceso() { return fechaInicioProceso; }
    public void setFechaInicioProceso(OffsetDateTime fechaInicioProceso) { this.fechaInicioProceso = fechaInicioProceso; }
    public OffsetDateTime getFechaFinProceso() { return fechaFinProceso; }
    public void setFechaFinProceso(OffsetDateTime fechaFinProceso) { this.fechaFinProceso = fechaFinProceso; }
    public OffsetDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(OffsetDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LotePago that)) {
            return false;
        }
        return Objects.equals(this.idLote, that.idLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idLote);
    }

    @Override
    public String toString() {
        return "LotePago{" +
                "idLote=" + idLote +
                ", uuidLote=" + uuidLote +
                ", rucEmpresa='" + rucEmpresa + '\'' +
                ", estado=" + estado +
                '}';
    }
}
