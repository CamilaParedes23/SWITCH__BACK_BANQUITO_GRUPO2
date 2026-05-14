package com.banquito.switchpagos.lote.model;

import com.banquito.switchpagos.common.enums.EstadoLoteEnum;
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
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(schema = "switch_banquito", name = "HISTORIAL_ESTADO_LOTE")
public class HistorialEstadoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORIAL", nullable = false)
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LOTE", nullable = false)
    private LotePago lotePago;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ANTERIOR", length = 25)
    private EstadoLoteEnum estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_NUEVO", nullable = false, length = 25)
    private EstadoLoteEnum estadoNuevo;

    @Column(name = "MOTIVO", length = 500)
    private String motivo;

    @Column(name = "CAMBIADO_POR", length = 100)
    private String cambiadoPor;

    @Column(name = "FECHA_CAMBIO", nullable = false)
    private OffsetDateTime fechaCambio;

    public HistorialEstadoLote() {
    }

    public HistorialEstadoLote(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Long getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Long idHistorial) { this.idHistorial = idHistorial; }
    public LotePago getLotePago() { return lotePago; }
    public void setLotePago(LotePago lotePago) { this.lotePago = lotePago; }
    public EstadoLoteEnum getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoLoteEnum estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public EstadoLoteEnum getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoLoteEnum estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getCambiadoPor() { return cambiadoPor; }
    public void setCambiadoPor(String cambiadoPor) { this.cambiadoPor = cambiadoPor; }
    public OffsetDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(OffsetDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HistorialEstadoLote that)) {
            return false;
        }
        return Objects.equals(this.idHistorial, that.idHistorial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idHistorial);
    }

    @Override
    public String toString() {
        return "HistorialEstadoLote{" +
                "idHistorial=" + idHistorial +
                ", estadoAnterior=" + estadoAnterior +
                ", estadoNuevo=" + estadoNuevo +
                '}';
    }
}
