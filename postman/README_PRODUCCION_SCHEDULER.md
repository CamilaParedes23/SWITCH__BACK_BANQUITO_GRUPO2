# Pruebas de produccion - flujo automatico scheduler

Esta carpeta de pruebas valida el flujo real esperado para frontend: cargar un archivo y dejar que el Switch ejecute internamente validacion, procesamiento y liquidacion mediante scheduler.

Coleccion:

```text
postman/Switch_Pagos_Masivos_Produccion_Scheduler.postman_collection.json
```

Archivos:

```text
postman/production_examples/
```

## Archivos incluidos

- `prod_lote_correcto_encolado.csv`: archivo correcto para probar que el lote queda `ENCOLADO`.
- `prod_lote_correcto_automatico.csv`: archivo correcto para probar procesamiento automatico.
- `prod_lote_error_sin_pie.csv`: error estructural; debe fallar en `POST /lotes` con `400` y no crear lote.
- `prod_lote_error_totales.csv`: error estructural de totales; debe fallar en `POST /lotes` con `400` y no crear lote.
- `prod_lote_error_cuenta_matriz.csv`: crea lote, pero debe quedar `RECHAZADO` cuando el scheduler valide contra Core.
- `prod_lote_error_cuenta_destino.csv`: crea lote, valida lote, pero debe quedar `FALLIDO` en procesamiento porque no hay lineas exitosas.

## Como probar que queda ENCOLADO

El estado `ENCOLADO` depende de la hora real de recepcion del servidor, no de la fecha escrita dentro del archivo.

Antes de iniciar, revise en variables de la coleccion que:

```text
idCredencialWebCore=1
```

No use `CRED-1` en ese campo, porque el endpoint de carga del Switch recibe ese parametro como numero. El identificador textual `CRED-1` solo viene del login del Core.

1. En `.env`, configure la hora de corte antes de la hora actual:

```properties
SWITCH_PARAMETROS_HORA_CORTE_PROCESO=00:01
SWITCH_PARAMETROS_HORA_INICIO_LOTES_ENCOLADOS=08:00
```

2. Reinicie el Switch.
3. Importe la coleccion nueva de Postman.
4. Ejecute:

```text
01 Debe quedar ENCOLADO / POST Cargar lote correcto para encolar
01 Debe quedar ENCOLADO / GET Estado lote encolado
```

Resultado esperado:

- `POST /lotes` responde `201`.
- `estado` inicial del lote: `ENCOLADO`.
- El lote no se procesa automaticamente hasta que llegue su `fechaProgramadaProceso`.

## Como probar procesamiento automatico

1. En `.env`, configure la hora de corte despues de la hora actual:

```properties
SWITCH_PARAMETROS_HORA_CORTE_PROCESO=23:59
SWITCH_PARAMETROS_HORA_INICIO_LOTES_ENCOLADOS=08:00
```

2. Reinicie el Switch.
3. Ejecute:

```text
02 Debe procesarse automaticamente / POST Cargar lote correcto automatico
```

4. Espere al menos el valor de:

```properties
SWITCH_COLA_SCHEDULER_FIXED_DELAY_MS
```

5. Consulte:

```text
02 Debe procesarse automaticamente / GET Estado lote automatico
```

Resultado esperado si Core responde correctamente:

- Al cargar, estado inicial: `RECIBIDO`.
- Luego del scheduler: `CERRADO`.
- Si no desea esperar el intervalo del scheduler, use:

```text
02 Debe procesarse automaticamente / POST Forzar scheduler en demo
```

Ese endpoint es solo para demostracion y pruebas.

## Donde modificar horas

Las horas del Switch se configuran en `.env`:

```properties
SWITCH_PARAMETROS_HORA_CORTE_PROCESO=23:59
SWITCH_PARAMETROS_HORA_INICIO_LOTES_ENCOLADOS=08:00
```

Tambien existen en:

```text
src/main/resources/application.properties
```

pero se recomienda cambiarlas en `.env` para no tocar configuracion versionada.

Despues de cambiar `.env`, reinicie el Switch.

## Donde modificar dias habiles y feriados

Los dias habiles no los decide el Switch. El Switch consulta al Core:

```http
GET /api/v1/core/integracion-switch/calendario/dia-habil?fecha={yyyy-MM-dd}
```

Por tanto:

- Para probar dia habil normal, use una fecha actual que Core marque como habil.
- Para probar feriado o fin de semana, ajuste los datos/calendario del Core o use una fecha que Core ya devuelva con `esDiaHabil=false`.
- El Switch solo consume esa respuesta y programa el lote para el siguiente dia habil informado por Core.

## Estados esperados de errores

### Error estructural en carga

Archivos:

- `prod_lote_error_sin_pie.csv`
- `prod_lote_error_totales.csv`

Resultado esperado:

- `POST /lotes` responde `400`.
- No se crea lote.
- No se crea cola.

### Error de negocio en validacion

Archivo:

- `prod_lote_error_cuenta_matriz.csv`

Resultado esperado:

- `POST /lotes` responde `201`.
- Scheduler valida contra Core.
- Lote queda `RECHAZADO`.

### Error por linea en procesamiento

Archivo:

- `prod_lote_error_cuenta_destino.csv`

Resultado esperado:

- `POST /lotes` responde `201`.
- Scheduler valida el lote.
- La linea se rechaza por cuenta destino/beneficiario.
- Como no hay lineas exitosas, el lote queda `FALLIDO`.
- No se liquida.

## Nota sobre duplicidad

El Switch valida duplicidad por RUC, nombre de archivo y hash dentro de la ventana configurada. Si repite la misma prueba muchas veces, cambie el nombre del archivo antes de subirlo o limpie los datos de prueba en la base del Switch.
