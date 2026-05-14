package com.banquito.switchpagos.tarifaje.model;

import com.banquito.switchpagos.catalogo.model.TipoServicio;
import com.banquito.switchpagos.common.enums.EstadoTarifaServicioEnum;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(schema = "switch_banquito", name = "TARIFA_SERVICIO")
public class TarifaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TARIFA", nullable = false)
    private Integer idTarifa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIPO_SERVICIO", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "RANGO_DESDE", nullable = false)
    private Integer rangoDesde;

    @Column(name = "RANGO_HASTA")
    private Integer rangoHasta;

    @Column(name = "TARIFA_UNITARIA", nullable = false, precision = 10, scale = 4)
    private BigDecimal tarifaUnitaria;

    @Column(name = "MONEDA", nullable = false, length = 3)
    private String moneda;

    @Column(name = "VIGENTE_DESDE", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "VIGENTE_HASTA")
    private LocalDate vigenteHasta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoTarifaServicioEnum estado;

    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public TarifaServicio() {
    }

    public TarifaServicio(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public Integer getIdTarifa() { return idTarifa; }
    public void setIdTarifa(Integer idTarifa) { this.idTarifa = idTarifa; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }
    public Integer getRangoDesde() { return rangoDesde; }
    public void setRangoDesde(Integer rangoDesde) { this.rangoDesde = rangoDesde; }
    public Integer getRangoHasta() { return rangoHasta; }
    public void setRangoHasta(Integer rangoHasta) { this.rangoHasta = rangoHasta; }
    public BigDecimal getTarifaUnitaria() { return tarifaUnitaria; }
    public void setTarifaUnitaria(BigDecimal tarifaUnitaria) { this.tarifaUnitaria = tarifaUnitaria; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    public EstadoTarifaServicioEnum getEstado() { return estado; }
    public void setEstado(EstadoTarifaServicioEnum estado) { this.estado = estado; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TarifaServicio that)) {
            return false;
        }
        return Objects.equals(this.idTarifa, that.idTarifa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idTarifa);
    }

    @Override
    public String toString() {
        return "TarifaServicio{" +
                "idTarifa=" + idTarifa +
                ", rangoDesde=" + rangoDesde +
                ", rangoHasta=" + rangoHasta +
                '}';
    }
}
