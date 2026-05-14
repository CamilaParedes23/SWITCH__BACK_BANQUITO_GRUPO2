package com.banquito.switchpagos.reporte.model;

import com.banquito.switchpagos.common.enums.EstadoEnvioNotificacionEnum;
import com.banquito.switchpagos.common.enums.TipoNotificacionEnum;
import com.banquito.switchpagos.procesamiento.model.LineaPago;
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
@Table(schema = "switch_banquito", name = "NOTIFICACION_BENEFICIARIO")
public class NotificacionBeneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICACION", nullable = false)
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LINEA", nullable = false)
    private LineaPago lineaPago;

    @Column(name = "CORREO_DESTINO", nullable = false, length = 200)
    private String correoDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_NOTIFICACION", nullable = false, length = 25)
    private TipoNotificacionEnum tipoNotificacion;

    @Column(name = "ASUNTO", length = 200)
    private String asunto;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "CONTENIDO")
    private JsonNode contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ENVIO", nullable = false, length = 15)
    private EstadoEnvioNotificacionEnum estadoEnvio;

    @Column(name = "FECHA_ENVIO")
    private OffsetDateTime fechaEnvio;

    @Column(name = "ERROR_ENVIO", length = 300)
    private String errorEnvio;

    @Column(name = "REINTENTOS", nullable = false)
    private Integer reintentos;

    @Column(name = "PROXIMO_REINTENTO_EN")
    private OffsetDateTime proximoReintentoEn;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public NotificacionBeneficiario() {
    }

    public NotificacionBeneficiario(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Long getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Long idNotificacion) { this.idNotificacion = idNotificacion; }
    public LineaPago getLineaPago() { return lineaPago; }
    public void setLineaPago(LineaPago lineaPago) { this.lineaPago = lineaPago; }
    public String getCorreoDestino() { return correoDestino; }
    public void setCorreoDestino(String correoDestino) { this.correoDestino = correoDestino; }
    public TipoNotificacionEnum getTipoNotificacion() { return tipoNotificacion; }
    public void setTipoNotificacion(TipoNotificacionEnum tipoNotificacion) { this.tipoNotificacion = tipoNotificacion; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public JsonNode getContenido() { return contenido; }
    public void setContenido(JsonNode contenido) { this.contenido = contenido; }
    public EstadoEnvioNotificacionEnum getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(EstadoEnvioNotificacionEnum estadoEnvio) { this.estadoEnvio = estadoEnvio; }
    public OffsetDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(OffsetDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public String getErrorEnvio() { return errorEnvio; }
    public void setErrorEnvio(String errorEnvio) { this.errorEnvio = errorEnvio; }
    public Integer getReintentos() { return reintentos; }
    public void setReintentos(Integer reintentos) { this.reintentos = reintentos; }
    public OffsetDateTime getProximoReintentoEn() { return proximoReintentoEn; }
    public void setProximoReintentoEn(OffsetDateTime proximoReintentoEn) { this.proximoReintentoEn = proximoReintentoEn; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NotificacionBeneficiario that)) {
            return false;
        }
        return Objects.equals(this.idNotificacion, that.idNotificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idNotificacion);
    }

    @Override
    public String toString() {
        return "NotificacionBeneficiario{" +
                "idNotificacion=" + idNotificacion +
                ", correoDestino='" + correoDestino + '\'' +
                ", estadoEnvio=" + estadoEnvio +
                '}';
    }
}
