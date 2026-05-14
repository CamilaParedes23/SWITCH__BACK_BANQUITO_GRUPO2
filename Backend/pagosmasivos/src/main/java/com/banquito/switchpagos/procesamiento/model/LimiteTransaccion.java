package com.banquito.switchpagos.procesamiento.model;

import com.banquito.switchpagos.catalogo.model.TipoServicio;
import com.banquito.switchpagos.common.enums.EstadoLimiteTransaccionEnum;
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
@Table(schema = "switch_banquito", name = "LIMITE_TRANSACCION")
public class LimiteTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LIMITE", nullable = false)
    private Integer idLimite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIPO_SERVICIO", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "MONTO_MINIMO", nullable = false, precision = 19, scale = 4)
    private BigDecimal montoMinimo;

    @Column(name = "MONTO_MAXIMO", nullable = false, precision = 19, scale = 4)
    private BigDecimal montoMaximo;

    @Column(name = "MONEDA", nullable = false, length = 3)
    private String moneda;

    @Column(name = "VIGENTE_DESDE", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "VIGENTE_HASTA")
    private LocalDate vigenteHasta;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 15)
    private EstadoLimiteTransaccionEnum estado;

    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public LimiteTransaccion() {
    }

    public LimiteTransaccion(Integer idLimite) {
        this.idLimite = idLimite;
    }

    public Integer getIdLimite() { return idLimite; }
    public void setIdLimite(Integer idLimite) { this.idLimite = idLimite; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }
    public BigDecimal getMontoMinimo() { return montoMinimo; }
    public void setMontoMinimo(BigDecimal montoMinimo) { this.montoMinimo = montoMinimo; }
    public BigDecimal getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(BigDecimal montoMaximo) { this.montoMaximo = montoMaximo; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    public EstadoLimiteTransaccionEnum getEstado() { return estado; }
    public void setEstado(EstadoLimiteTransaccionEnum estado) { this.estado = estado; }
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
        if (!(object instanceof LimiteTransaccion that)) {
            return false;
        }
        return Objects.equals(this.idLimite, that.idLimite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idLimite);
    }

    @Override
    public String toString() {
        return "LimiteTransaccion{" +
                "idLimite=" + idLimite +
                ", montoMinimo=" + montoMinimo +
                ", montoMaximo=" + montoMaximo +
                '}';
    }
}
