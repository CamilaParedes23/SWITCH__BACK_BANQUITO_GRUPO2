package com.banquito.switchpagos.parametro.model;

import com.banquito.switchpagos.common.enums.TipoDatoParametroEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(schema = "switch_banquito", name = "PARAMETRO_SWITCH")
public class ParametroSwitch {

    @Id
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "VALOR_TEXTO", nullable = false, length = 255)
    private String valorTexto;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DATO", nullable = false, length = 15)
    private TipoDatoParametroEnum tipoDato;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @Column(name = "ACTUALIZADO_POR", length = 100)
    private String actualizadoPor;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public ParametroSwitch() {
    }

    public ParametroSwitch(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public TipoDatoParametroEnum getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDatoParametroEnum tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ParametroSwitch that)) {
            return false;
        }
        return Objects.equals(this.codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codigo);
    }

    @Override
    public String toString() {
        return "ParametroSwitch{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipoDato=" + tipoDato +
                '}';
    }
}
