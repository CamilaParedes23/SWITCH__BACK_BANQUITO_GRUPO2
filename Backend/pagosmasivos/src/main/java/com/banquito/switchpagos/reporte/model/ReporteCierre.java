package com.banquito.switchpagos.reporte.model;

import com.banquito.switchpagos.common.enums.FormatoReporteEnum;
import com.banquito.switchpagos.common.enums.TipoReporteEnum;
import com.banquito.switchpagos.lote.model.LotePago;
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
@Table(schema = "switch_banquito", name = "REPORTE_CIERRE")
public class ReporteCierre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REPORTE", nullable = false)
    private Long idReporte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_LOTE", nullable = false)
    private LotePago lotePago;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_REPORTE", nullable = false, length = 35)
    private TipoReporteEnum tipoReporte;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "CONTENIDO_JSON", nullable = false)
    private JsonNode contenidoJson;

    @Column(name = "NOMBRE_ARCHIVO", length = 255)
    private String nombreArchivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "FORMATO_ARCHIVO", length = 10)
    private FormatoReporteEnum formatoArchivo;

    @Column(name = "URL_ARCHIVO", length = 500)
    private String urlArchivo;

    @Column(name = "HASH_REPORTE", length = 128)
    private String hashReporte;

    @Column(name = "FECHA_GENERACION", nullable = false)
    private OffsetDateTime fechaGeneracion;

    @Column(name = "DESCARGADO_EMPRESA", nullable = false)
    private Boolean descargadoEmpresa;

    @Column(name = "FECHA_DESCARGA")
    private OffsetDateTime fechaDescarga;

    @Column(name = "FECHA_ACTUALIZACION")
    private OffsetDateTime fechaActualizacion;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public ReporteCierre() {
    }

    public ReporteCierre(Long idReporte) {
        this.idReporte = idReporte;
    }

    public Long getIdReporte() { return idReporte; }
    public void setIdReporte(Long idReporte) { this.idReporte = idReporte; }
    public LotePago getLotePago() { return lotePago; }
    public void setLotePago(LotePago lotePago) { this.lotePago = lotePago; }
    public TipoReporteEnum getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(TipoReporteEnum tipoReporte) { this.tipoReporte = tipoReporte; }
    public JsonNode getContenidoJson() { return contenidoJson; }
    public void setContenidoJson(JsonNode contenidoJson) { this.contenidoJson = contenidoJson; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public FormatoReporteEnum getFormatoArchivo() { return formatoArchivo; }
    public void setFormatoArchivo(FormatoReporteEnum formatoArchivo) { this.formatoArchivo = formatoArchivo; }
    public String getUrlArchivo() { return urlArchivo; }
    public void setUrlArchivo(String urlArchivo) { this.urlArchivo = urlArchivo; }
    public String getHashReporte() { return hashReporte; }
    public void setHashReporte(String hashReporte) { this.hashReporte = hashReporte; }
    public OffsetDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(OffsetDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public Boolean getDescargadoEmpresa() { return descargadoEmpresa; }
    public void setDescargadoEmpresa(Boolean descargadoEmpresa) { this.descargadoEmpresa = descargadoEmpresa; }
    public OffsetDateTime getFechaDescarga() { return fechaDescarga; }
    public void setFechaDescarga(OffsetDateTime fechaDescarga) { this.fechaDescarga = fechaDescarga; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ReporteCierre that)) {
            return false;
        }
        return Objects.equals(this.idReporte, that.idReporte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idReporte);
    }

    @Override
    public String toString() {
        return "ReporteCierre{" +
                "idReporte=" + idReporte +
                ", tipoReporte=" + tipoReporte +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                '}';
    }
}
