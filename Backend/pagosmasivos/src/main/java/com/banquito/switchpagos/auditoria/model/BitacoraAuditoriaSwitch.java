package com.banquito.switchpagos.auditoria.model;

import com.banquito.switchpagos.common.enums.TipoActorAuditoriaEnum;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(schema = "switch_banquito", name = "BITACORA_AUDITORIA_SWITCH")
public class BitacoraAuditoriaSwitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDITORIA", nullable = false)
    private Long idAuditoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ACTOR", nullable = false, length = 20)
    private TipoActorAuditoriaEnum tipoActor;

    @Column(name = "ID_ACTOR", length = 50)
    private String idActor;

    @Column(name = "RUC_EMPRESA", length = 13)
    private String rucEmpresa;

    @Column(name = "ACCION", nullable = false, length = 100)
    private String accion;

    @Column(name = "ENTIDAD", nullable = false, length = 80)
    private String entidad;

    @Column(name = "ID_ENTIDAD", length = 80)
    private String idEntidad;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DATOS_ANTES")
    private JsonNode datosAntes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DATOS_DESPUES")
    private JsonNode datosDespues;

    @Column(name = "DIRECCION_IP")
    private String direccionIp;

    @Column(name = "AGENTE_USUARIO", length = 300)
    private String agenteUsuario;

    @Column(name = "FECHA_CREACION", nullable = false)
    private OffsetDateTime fechaCreacion;

    public BitacoraAuditoriaSwitch() {
    }

    public BitacoraAuditoriaSwitch(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Long idAuditoria) { this.idAuditoria = idAuditoria; }
    public TipoActorAuditoriaEnum getTipoActor() { return tipoActor; }
    public void setTipoActor(TipoActorAuditoriaEnum tipoActor) { this.tipoActor = tipoActor; }
    public String getIdActor() { return idActor; }
    public void setIdActor(String idActor) { this.idActor = idActor; }
    public String getRucEmpresa() { return rucEmpresa; }
    public void setRucEmpresa(String rucEmpresa) { this.rucEmpresa = rucEmpresa; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public String getIdEntidad() { return idEntidad; }
    public void setIdEntidad(String idEntidad) { this.idEntidad = idEntidad; }
    public JsonNode getDatosAntes() { return datosAntes; }
    public void setDatosAntes(JsonNode datosAntes) { this.datosAntes = datosAntes; }
    public JsonNode getDatosDespues() { return datosDespues; }
    public void setDatosDespues(JsonNode datosDespues) { this.datosDespues = datosDespues; }
    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }
    public String getAgenteUsuario() { return agenteUsuario; }
    public void setAgenteUsuario(String agenteUsuario) { this.agenteUsuario = agenteUsuario; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BitacoraAuditoriaSwitch that)) {
            return false;
        }
        return Objects.equals(this.idAuditoria, that.idAuditoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idAuditoria);
    }

    @Override
    public String toString() {
        return "BitacoraAuditoriaSwitch{" +
                "idAuditoria=" + idAuditoria +
                ", tipoActor=" + tipoActor +
                ", entidad='" + entidad + '\'' +
                '}';
    }
}
