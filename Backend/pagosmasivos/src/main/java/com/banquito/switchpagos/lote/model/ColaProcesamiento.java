package com.banquito.switchpagos.lote.model;

import com.banquito.switchpagos.common.enums.EstadoColaProcesamientoEnum;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(schema = "switch_banquito", name = "COLA_PROCESAMIENTO")
public class ColaProcesamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COLA", nullable = false)
    private Long idCola;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LOTE", nullable = false)
    private LotePago lotePago;

    @Column(name = "FECHA_HABIL_PROGRAMADA", nullable = false)
    private LocalDate fechaHabilProgramada;

    @Column(name = "FECHA_ENCOLADO", nullable = false)
    private OffsetDateTime fechaEncolado;

    @Column(name = "FECHA_PROGRAMADA_PROCESO", nullable = false)
    private OffsetDateTime fechaProgramadaProceso;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_COLA", nullable = false, length = 20)
    private EstadoColaProcesamientoEnum estadoCola;

    @Column(name = "PRIORIDAD", nullable = false)
    private Integer prioridad;

    @Column(name = "INTENTOS", nullable = false)
    private Integer intentos;

    @Column(name = "MAX_INTENTOS", nullable = false)
    private Integer maxIntentos;

    @Column(name = "TOMADO_POR", length = 100)
    private String tomadoPor;

    @Column(name = "TOMADO_EN")
    private OffsetDateTime tomadoEn;

    @Column(name = "PROXIMO_REINTENTO_EN")
    private OffsetDateTime proximoReintentoEn;

    @Column(name = "ULTIMO_ERROR", length = 500)
    private String ultimoError;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public ColaProcesamiento() {
    }

    public ColaProcesamiento(Long idCola) {
        this.idCola = idCola;
    }

    public Long getIdCola() { return idCola; }
    public void setIdCola(Long idCola) { this.idCola = idCola; }
    public LotePago getLotePago() { return lotePago; }
    public void setLotePago(LotePago lotePago) { this.lotePago = lotePago; }
    public LocalDate getFechaHabilProgramada() { return fechaHabilProgramada; }
    public void setFechaHabilProgramada(LocalDate fechaHabilProgramada) { this.fechaHabilProgramada = fechaHabilProgramada; }
    public OffsetDateTime getFechaEncolado() { return fechaEncolado; }
    public void setFechaEncolado(OffsetDateTime fechaEncolado) { this.fechaEncolado = fechaEncolado; }
    public OffsetDateTime getFechaProgramadaProceso() { return fechaProgramadaProceso; }
    public void setFechaProgramadaProceso(OffsetDateTime fechaProgramadaProceso) { this.fechaProgramadaProceso = fechaProgramadaProceso; }
    public EstadoColaProcesamientoEnum getEstadoCola() { return estadoCola; }
    public void setEstadoCola(EstadoColaProcesamientoEnum estadoCola) { this.estadoCola = estadoCola; }
    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }
    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }
    public Integer getMaxIntentos() { return maxIntentos; }
    public void setMaxIntentos(Integer maxIntentos) { this.maxIntentos = maxIntentos; }
    public String getTomadoPor() { return tomadoPor; }
    public void setTomadoPor(String tomadoPor) { this.tomadoPor = tomadoPor; }
    public OffsetDateTime getTomadoEn() { return tomadoEn; }
    public void setTomadoEn(OffsetDateTime tomadoEn) { this.tomadoEn = tomadoEn; }
    public OffsetDateTime getProximoReintentoEn() { return proximoReintentoEn; }
    public void setProximoReintentoEn(OffsetDateTime proximoReintentoEn) { this.proximoReintentoEn = proximoReintentoEn; }
    public String getUltimoError() { return ultimoError; }
    public void setUltimoError(String ultimoError) { this.ultimoError = ultimoError; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ColaProcesamiento that)) {
            return false;
        }
        return Objects.equals(this.idCola, that.idCola);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCola);
    }

    @Override
    public String toString() {
        return "ColaProcesamiento{" +
                "idCola=" + idCola +
                ", estadoCola=" + estadoCola +
                ", prioridad=" + prioridad +
                '}';
    }
}
