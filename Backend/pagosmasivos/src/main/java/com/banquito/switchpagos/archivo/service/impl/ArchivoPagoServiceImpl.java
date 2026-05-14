package com.banquito.switchpagos.archivo.service.impl;

import com.banquito.switchpagos.archivo.dto.internal.ArchivoPagoDTO;
import com.banquito.switchpagos.archivo.dto.internal.DetalleArchivoPagoDTO;
import com.banquito.switchpagos.archivo.service.ArchivoPagoService;
import com.banquito.switchpagos.common.enums.FormatoArchivoEnum;
import com.banquito.switchpagos.common.exception.ValidacionArchivoException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ArchivoPagoServiceImpl implements ArchivoPagoService {

    private static final DateTimeFormatter FECHA_HORA_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final String DELIMITADOR_OFICIAL = ";";
    private static final Pattern PATRON_CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Integer COLUMNAS_CABECERA = 7;
    private static final Integer COLUMNAS_DETALLE = 8;
    private static final Integer COLUMNAS_PIE = 4;

    @Override
    public ArchivoPagoDTO analizarContenido(byte[] archivo, String nombreArchivo) {
        if (archivo == null || archivo.length == 0) {
            throw new ValidacionArchivoException("El archivo esta vacio");
        }
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new ValidacionArchivoException("El nombre del archivo es obligatorio");
        }

        FormatoArchivoEnum formatoArchivo = inferirFormato(nombreArchivo);
        String contenidoPlano = normalizarContenido(new String(archivo, StandardCharsets.UTF_8));
        List<String> lineas = contenidoPlano.lines()
                .map(String::trim)
                .filter(linea -> !linea.isBlank())
                .toList();

        if (lineas.size() < 3) {
            throw new ValidacionArchivoException("El archivo debe contener una cabecera, al menos un detalle y un pie");
        }

        validarDelimitadorOficial(lineas);

        String[] cabecera = dividir(lineas.get(0));
        validarTipoRegistro(cabecera, "CAB", 1);
        validarCantidadColumnas(cabecera, COLUMNAS_CABECERA, 1, "CAB");

        String[] pie = dividir(lineas.get(lineas.size() - 1));
        validarTipoRegistro(pie, "PIE", lineas.size());
        validarCantidadColumnas(pie, COLUMNAS_PIE, lineas.size(), "PIE");

        List<DetalleArchivoPagoDTO> detalles = new ArrayList<>();
        Set<Integer> secuenciales = new HashSet<>();

        for (int indice = 1; indice < lineas.size() - 1; indice++) {
            String[] detalle = dividir(lineas.get(indice));
            validarTipoRegistro(detalle, "DET", indice + 1);
            validarCantidadColumnas(detalle, COLUMNAS_DETALLE, indice + 1, "DET");
            DetalleArchivoPagoDTO detalleArchivoPagoDTO = parsearDetalle(detalle, indice + 1);
            if (!secuenciales.add(detalleArchivoPagoDTO.secuencial())) {
                throw new ValidacionArchivoException("El secuencial " + detalleArchivoPagoDTO.secuencial() + " esta duplicado en el archivo");
            }
            detalles.add(detalleArchivoPagoDTO);
        }

        return new ArchivoPagoDTO(
                nombreArchivo,
                formatoArchivo,
                contenidoPlano,
                generarHash(contenidoPlano),
                obtenerValor(cabecera, 1, "RUC de cabecera", 1),
                obtenerValor(cabecera, 2, "tipo de servicio de cabecera", 1),
                parsearFechaHora(obtenerValor(cabecera, 3, "fecha y hora de generacion", 1)),
                obtenerValor(cabecera, 4, "cuenta matriz de cargo", 1),
                parsearEntero(obtenerValor(cabecera, 5, "total de registros de cabecera", 1), "total de registros de cabecera"),
                parsearMonto(obtenerValor(cabecera, 6, "monto total de cabecera", 1), "monto total de cabecera"),
                parsearEntero(obtenerValor(pie, 2, "total de registros de pie", lineas.size()), "total de registros de pie"),
                parsearMonto(obtenerValor(pie, 3, "monto total de pie", lineas.size()), "monto total de pie"),
                obtenerValor(pie, 1, "hash de pie", lineas.size()),
                List.copyOf(detalles));
    }

    private FormatoArchivoEnum inferirFormato(String nombreArchivo) {
        String nombreMinusculas = nombreArchivo.toLowerCase();
        if (nombreMinusculas.endsWith(".csv")) {
            return FormatoArchivoEnum.CSV;
        }
        if (nombreMinusculas.endsWith(".txt")) {
            return FormatoArchivoEnum.TXT;
        }
        throw new ValidacionArchivoException("Formato de archivo no soportado: " + nombreArchivo);
    }

    private String normalizarContenido(String contenidoPlano) {
        return contenidoPlano.replace("\uFEFF", "").replace("\r\n", "\n").replace('\r', '\n');
    }

    private void validarDelimitadorOficial(List<String> lineas) {
        for (int indice = 0; indice < lineas.size(); indice++) {
            String linea = lineas.get(indice);
            if (!linea.contains(DELIMITADOR_OFICIAL)) {
                throw new ValidacionArchivoException("La linea " + (indice + 1) + " no usa el delimitador oficial ';'");
            }
            if (linea.contains("|") || linea.contains(",")) {
                throw new ValidacionArchivoException("La linea " + (indice + 1) + " contiene un delimitador no permitido");
            }
        }
    }

    private String[] dividir(String linea) {
        return linea.split(DELIMITADOR_OFICIAL, -1);
    }

    private void validarTipoRegistro(String[] valores, String tipoEsperado, Integer numeroLinea) {
        if (valores.length == 0 || valores[0].isBlank()) {
            throw new ValidacionArchivoException("La linea " + numeroLinea + " no contiene tipo de registro");
        }
        if (!tipoEsperado.equals(valores[0].trim())) {
            throw new ValidacionArchivoException("La linea " + numeroLinea + " debe ser un registro " + tipoEsperado);
        }
    }

    private void validarCantidadColumnas(String[] valores, Integer cantidadEsperada, Integer numeroLinea, String tipoRegistro) {
        if (valores.length != cantidadEsperada) {
            throw new ValidacionArchivoException("La linea " + numeroLinea + " del registro " + tipoRegistro + " debe tener exactamente " + cantidadEsperada + " columnas");
        }
    }

    private DetalleArchivoPagoDTO parsearDetalle(String[] detalle, Integer numeroLinea) {
        Integer secuencial = parsearEntero(obtenerValor(detalle, 1, "secuencial de detalle", numeroLinea), "secuencial de detalle");
        BigDecimal monto = parsearMonto(obtenerValor(detalle, 5, "monto de detalle", numeroLinea), "monto de detalle");
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionArchivoException("El monto de la linea " + numeroLinea + " debe ser mayor a cero");
        }

        String correoNotificacion = obtenerValorOpcional(detalle, 7);
        if (correoNotificacion != null && !PATRON_CORREO.matcher(correoNotificacion).matches()) {
            throw new ValidacionArchivoException("El correo de notificacion de la linea " + numeroLinea + " no tiene un formato valido");
        }

        return new DetalleArchivoPagoDTO(
                secuencial,
                obtenerValor(detalle, 2, "identificacion de beneficiario", numeroLinea),
                obtenerValor(detalle, 3, "nombre de beneficiario", numeroLinea),
                obtenerValor(detalle, 4, "cuenta destino", numeroLinea),
                monto,
                obtenerValor(detalle, 6, "concepto de detalle", numeroLinea),
                correoNotificacion);
    }

    private String obtenerValor(String[] valores, Integer indice, String campo, Integer numeroLinea) {
        if (valores.length <= indice || valores[indice].isBlank()) {
            throw new ValidacionArchivoException("No se encontro " + campo + " en la linea " + numeroLinea);
        }
        return valores[indice].trim();
    }

    private String obtenerValorOpcional(String[] valores, Integer indice) {
        if (valores.length <= indice || valores[indice].isBlank()) {
            return null;
        }
        return valores[indice].trim();
    }

    private Integer parsearEntero(String valor, String campo) {
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException exception) {
            throw new ValidacionArchivoException("El campo " + campo + " no contiene un entero valido");
        }
    }

    private BigDecimal parsearMonto(String valor, String campo) {
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException exception) {
            throw new ValidacionArchivoException("El campo " + campo + " no contiene un monto valido");
        }
    }

    private OffsetDateTime parsearFechaHora(String valor) {
        try {
            return OffsetDateTime.parse(valor, FECHA_HORA_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new ValidacionArchivoException("La fecha y hora de generacion no tiene un formato valido ISO_OFFSET_DATE_TIME");
        }
    }

    private String generarHash(String contenidoPlano) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(contenidoPlano.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No fue posible generar el hash del archivo", exception);
        }
    }
}
