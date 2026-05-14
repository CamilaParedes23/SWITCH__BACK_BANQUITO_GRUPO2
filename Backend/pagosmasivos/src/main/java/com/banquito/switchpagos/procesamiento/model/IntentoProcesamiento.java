package com.banquito.switchpagos.procesamiento.model;

import com.banquito.switchpagos.common.enums.EstadoIntentoProcesamientoEnum;
import com.banquito.switchpagos.lote.model.ColaProcesamiento;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(schema = "switch_banquito", name = "INTENTO_PROCESAMIENTO")
public class IntentoProcesamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INTENTO", nullable = false)
    private Long idIntento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_COLA", nullable = false)
    private ColaProcesamiento colaProcesamiento;

    @Column(name = "NUMERO_INTENTO", nullable = false)
    private Integer numeroIntento;

    @Column(name = "FECHA_INICIO", nullable = false)
    private OffsetDateTime fechaInicio;

    @Column(name = "FECHA_FIN")
    private OffsetDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoIntentoProcesamientoEnum estado;

    @Column(name = "CODIGO_ERROR", length = 50)
    private String codigoError;

    @Column(name = "MENSAJE_ERROR", length = 500)
    private String mensajeError;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "SOLICITUD_CORE")
    private JsonNode solicitudCore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "RESPUESTA_CORE")
    private JsonNode respuestaCore;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public IntentoProcesamiento() {
    }

    public IntentoProcesamiento(Long idIntento) {
        this.idIntento = idIntento;
    }

    public Long getIdIntento() { return idIntento; }
    public void setIdIntento(Long idIntento) { this.idIntento = idIntento; }
    public ColaProcesamiento getColaProcesamiento() { return colaProcesamiento; }
    public void setColaProcesamiento(ColaProcesamiento colaProcesamiento) { this.colaProcesamiento = colaProcesamiento; }
    public Integer getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(Integer numeroIntento) { this.numeroIntento = numeroIntento; }
    public OffsetDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(OffsetDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public OffsetDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(OffsetDateTime fechaFin) { this.fechaFin = fechaFin; }
    public EstadoIntentoProcesamientoEnum getEstado() { return estado; }
    public void setEstado(EstadoIntentoProcesamientoEnum estado) { this.estado = estado; }
    public String getCodigoError() { return codigoError; }
    public void setCodigoError(String codigoError) { this.codigoError = codigoError; }
    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }
    public JsonNode getSolicitudCore() { return solicitudCore; }
    public void setSolicitudCore(JsonNode solicitudCore) { this.solicitudCore = solicitudCore; }
    public JsonNode getRespuestaCore() { return respuestaCore; }
    public void setRespuestaCore(JsonNode respuestaCore) { this.respuestaCore = respuestaCore; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IntentoProcesamiento that)) {
            return false;
        }
        return Objects.equals(this.idIntento, that.idIntento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idIntento);
    }

    @Override
    public String toString() {
        return "IntentoProcesamiento{" +
                "idIntento=" + idIntento +
                ", numeroIntento=" + numeroIntento +
                ", estado=" + estado +
                '}';
    }
}
