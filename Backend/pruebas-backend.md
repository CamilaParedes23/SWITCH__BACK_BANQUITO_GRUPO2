# Pruebas manuales del backend

Este documento resume una forma rapida de probar el flujo principal del backend usando los archivos de ejemplo ubicados en `Backend/pagosmasivos/src/main/resources/samples`.

## Formato oficial del archivo

El parser actual acepta unicamente el delimitador `;` y los tipos de registro `CAB`, `DET` y `PIE`.

```text
CAB;rucEmpresa;codigoTipoServicio;fechaHoraGeneracion;cuentaMatrizCargo;totalRegistros;montoTotal
DET;secuencial;identificacionBeneficiario;nombreBeneficiario;cuentaDestino;monto;conceptoReferencia;correoNotificacion
PIE;hashControl;totalRegistros;montoTotal
```

Reglas principales:

- Debe existir exactamente una `CAB`.
- Debe existir al menos una `DET`.
- Debe existir exactamente una `PIE`.
- El orden debe ser `CAB`, luego `DET`, luego `PIE`.
- `correoNotificacion` en `DET` es opcional.

## Archivos de ejemplo

- `Backend/pagosmasivos/src/main/resources/samples/lotes-validos/lote-valido.csv`
  - Archivo valido para flujo completo.

- `Backend/pagosmasivos/src/main/resources/samples/lotes-validos/lote-parcial.csv`
  - Archivo estructuralmente valido con una linea que fallara en procesamiento porque la cuenta destino empieza con `999`.

- `Backend/pagosmasivos/src/main/resources/samples/lotes-invalidos/lote-descuadrado.csv`
  - Archivo invalido por descuadre entre cabecera, detalle y pie.

- `Backend/pagosmasivos/src/main/resources/samples/lotes-invalidos/lote-formato-invalido.txt`
  - Archivo invalido por detalle incompleto.

## Flujo sugerido

### 1. Cargar lote

```bash
curl -X POST "http://localhost:8080/api/v1/pagos-masivos/lotes" \
  -F "archivo=@src/main/resources/samples/lotes-validos/lote-valido.csv" \
  -F "canalIngreso=PORTAL_WEB" \
  -F "formatoArchivo=CSV"
```

### 2. Consultar estado del lote

```bash
curl "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/estado"
```

### 3. Validar lote

```bash
curl -X POST "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/validar"
```

### 4. Consultar lineas

```bash
curl "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/lineas"
```

### 5. Procesar lote

```bash
curl -X POST "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/procesar"
```

### 6. Liquidar lote

```bash
curl -X POST "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/liquidar"
```

### 7. Consultar novedades y comprobante

```bash
curl "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/novedades"
curl "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/comprobante"
curl "http://localhost:8080/api/v1/pagos-masivos/lotes/{uuidLote}/notificaciones"
curl -X POST "http://localhost:8080/api/v1/pagos-masivos/notificaciones/procesar"
```

## Endpoints complementarios

```bash
curl "http://localhost:8080/api/v1/pagos-masivos/lotes"
curl "http://localhost:8080/api/v1/pagos-masivos/tarifas"
curl "http://localhost:8080/api/v1/pagos-masivos/horarios-corte"
```

## Notas

- El Core Bancario esta simulado.
- Una cuenta que empiece con `999` se considera inexistente en el stub actual.
- Un correo invalido o que contenga `fail@` se procesa como error simulado de notificacion.
- Un lote `ENCOLADO` no puede procesarse ni liquidarse hasta que el scheduler lo mueva automaticamente a `VALIDADO`.
- La base real sigue siendo necesaria para arrancar la aplicacion completamente.
- El proyecto sigue apuntando a Java 21, aunque la validacion temporal de compilacion se hizo con Java 17 del entorno actual.
