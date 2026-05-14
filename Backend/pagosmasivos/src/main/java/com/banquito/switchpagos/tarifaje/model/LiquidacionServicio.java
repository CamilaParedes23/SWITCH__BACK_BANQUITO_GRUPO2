package com.banquito.switchpagos.tarifaje.model;

import com.banquito.switchpagos.common.enums.EstadoDebitoLiquidacionEnum;
import com.banquito.switchpagos.lote.model.LotePago;
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

@Entity
@Table(schema = "switch_banquito", name = "LIQUIDACION_SERVICIO")
public class LiquidacionServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LIQUIDACION", nullable = false)
    private Long idLiquidacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LOTE", nullable = false)
    private LotePago lotePago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TARIFA_APLICADA", nullable = false)
    private TarifaServicio tarifaAplicada;

    @Column(name = "TRANSACCIONES_EXITOSAS", nullable = false)
    private Integer transaccionesExitosas;

    @Column(name = "TRANSACCIONES_FALLIDAS", nullable = false)
    private Integer transaccionesFallidas;

    @Column(name = "TARIFA_UNITARIA_APLICADA", nullable = false, precision = 10, scale = 4)
    private BigDecimal tarifaUnitariaAplicada;

    @Column(name = "IVA_PORCENTAJE_APLICADO", nullable = false, precision = 5, scale = 4)
    private BigDecimal ivaPorcentajeAplicado;

    @Column(name = "SUBTOTAL_COMISION", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalComision;

    @Column(name = "MONTO_IVA", nullable = false, precision = 19, scale = 4)
    private BigDecimal montoIva;

    @Column(name = "TOTAL_DEBITADO", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDebitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_DEBITO", nullable = false, length = 20)
    private EstadoDebitoLiquidacionEnum estadoDebito;

    @Column(name = "PERMITE_SOBREGIRO", nullable = false)
    private Boolean permiteSobregiro;

    @Column(name = "FECHA_LIQUIDACION")
    private OffsetDateTime fechaLiquidacion;

    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public LiquidacionServicio() {
    }

    public LiquidacionServicio(Long idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
    }

    public Long getIdLiquidacion() { return idLiquidacion; }
    public void setIdLiquidacion(Long idLiquidacion) { this.idLiquidacion = idLiquidacion; }
    public LotePago getLotePago() { return lotePago; }
    public void setLotePago(LotePago lotePago) { this.lotePago = lotePago; }
    public TarifaServicio getTarifaAplicada() { return tarifaAplicada; }
    public void setTarifaAplicada(TarifaServicio tarifaAplicada) { this.tarifaAplicada = tarifaAplicada; }
    public Integer getTransaccionesExitosas() { return transaccionesExitosas; }
    public void setTransaccionesExitosas(Integer transaccionesExitosas) { this.transaccionesExitosas = transaccionesExitosas; }
    public Integer getTransaccionesFallidas() { return transaccionesFallidas; }
    public void setTransaccionesFallidas(Integer transaccionesFallidas) { this.transaccionesFallidas = transaccionesFallidas; }
    public BigDecimal getTarifaUnitariaAplicada() { return tarifaUnitariaAplicada; }
    public void setTarifaUnitariaAplicada(BigDecimal tarifaUnitariaAplicada) { this.tarifaUnitariaAplicada = tarifaUnitariaAplicada; }
    public BigDecimal getIvaPorcentajeAplicado() { return ivaPorcentajeAplicado; }
    public void setIvaPorcentajeAplicado(BigDecimal ivaPorcentajeAplicado) { this.ivaPorcentajeAplicado = ivaPorcentajeAplicado; }
    public BigDecimal getSubtotalComision() { return subtotalComision; }
    public void setSubtotalComision(BigDecimal subtotalComision) { this.subtotalComision = subtotalComision; }
    public BigDecimal getMontoIva() { return montoIva; }
    public void setMontoIva(BigDecimal montoIva) { this.montoIva = montoIva; }
    public BigDecimal getTotalDebitado() { return totalDebitado; }
    public void setTotalDebitado(BigDecimal totalDebitado) { this.totalDebitado = totalDebitado; }
    public EstadoDebitoLiquidacionEnum getEstadoDebito() { return estadoDebito; }
    public void setEstadoDebito(EstadoDebitoLiquidacionEnum estadoDebito) { this.estadoDebito = estadoDebito; }
    public Boolean getPermiteSobregiro() { return permiteSobregiro; }
    public void setPermiteSobregiro(Boolean permiteSobregiro) { this.permiteSobregiro = permiteSobregiro; }
    public OffsetDateTime getFechaLiquidacion() { return fechaLiquidacion; }
    public void setFechaLiquidacion(OffsetDateTime fechaLiquidacion) { this.fechaLiquidacion = fechaLiquidacion; }
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
        if (!(object instanceof LiquidacionServicio that)) {
            return false;
        }
        return Objects.equals(this.idLiquidacion, that.idLiquidacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idLiquidacion);
    }

    @Override
    public String toString() {
        return "LiquidacionServicio{" +
                "idLiquidacion=" + idLiquidacion +
                ", estadoDebito=" + estadoDebito +
                ", totalDebitado=" + totalDebitado +
                '}';
    }
}
