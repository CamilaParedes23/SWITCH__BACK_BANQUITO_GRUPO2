# Flujo automatico de procesamiento por scheduler

## Objetivo

El frontend solo necesita cargar el archivo de pagos masivos. A partir de ese momento, el Switch registra el lote y ejecuta internamente las etapas de validacion, procesamiento financiero y liquidacion contable mediante la cola/scheduler.

Los endpoints manuales de validar, procesar y liquidar se mantienen para pruebas tecnicas, diagnostico y Postman, pero no son necesarios para el flujo normal del portal.

## Entrada del flujo

### Portal web

```http
POST /api/v1/pagos-masivos/lotes
```

El frontend envia el archivo y los datos requeridos para identificar empresa, tipo de servicio, cuenta matriz y credencial empresarial.

### SFTP

El cliente SFTP sube el archivo al inbox. El servidor SFTP embebido autentica contra Core, genera metadata tecnica con el usuario y RUC autenticado, y el scanner SFTP registra el lote usando el mismo flujo interno de carga.

## Registro inicial

Cuando se carga el archivo, el Switch:

1. Lee y parsea el archivo.
2. Valida estructura basica: cabecera `H`, detalles `D`, pie `T`, totales, secuenciales y formato.
3. Valida que los datos enviados coincidan con la cabecera.
4. Valida credencial empresarial contra Core cuando aplica.
5. Valida duplicidad por nombre, RUC y hash.
6. Consulta calendario operativo del Core.
7. Guarda lote y lineas en base de datos.
8. Crea siempre un registro en `COLA_PROCESAMIENTO`.

## Programacion del lote

El Switch usa `HORA_CORTE_PROCESO` y el calendario operativo del Core:

- Si el archivo llega en dia habil y antes de la hora de corte, el lote queda `RECIBIDO` y la cola se programa para ejecucion inmediata.
- Si llega despues de la hora de corte, en fin de semana o feriado, el lote queda `ENCOLADO` y la cola se programa para el siguiente dia habil a partir de `HORA_INICIO_LOTES_ENCOLADOS`.

Las horas pueden configurarse por propiedades:

```properties
switch.parametros.hora-corte-proceso=16:00
switch.parametros.hora-inicio-lotes-encolados=08:00
```

## Scheduler de cola

El scheduler corre segun:

```properties
switch.cola.scheduler.enabled=true
switch.cola.scheduler.fixed-delay-ms=60000
switch.cola.max-lotes-por-ciclo=10
switch.cola.reintento-delay-minutos=5
```

En cada ciclo:

1. Busca colas `PENDIENTE` o `REINTENTO`.
2. En modo automatico normal, solo toma colas cuya `fechaProgramadaProceso` ya vencio.
3. Marca la cola como `PROCESANDO`.
4. Ejecuta el flujo interno completo.

Para pruebas manuales sin esperar horario:

```http
POST /api/v1/pagos-masivos/cola/procesar-pendientes
```

Ese endpoint fuerza el procesamiento de pendientes aunque la fecha programada no haya vencido.

## Flujo interno ejecutado por la cola

### 1. Validacion de lote

Si el lote esta `RECIBIDO` o `ENCOLADO`, el scheduler ejecuta la validacion de negocio:

- empresa existe, es juridica, esta activa y tiene pagos masivos;
- cuenta matriz existe, pertenece a la empresa, permite debitos y tiene capacidad financiera;
- cuenta favorita de pagos para cargas SFTP;
- tipo de servicio activo;
- ventana de duplicidad.

Si falla, el lote pasa a `RECHAZADO` y la cola se completa con el motivo de rechazo.

### 2. Procesamiento de lineas

Si la validacion es exitosa, el lote pasa a `VALIDADO` y luego se procesa linea por linea:

- consulta saldo disponible en Core;
- valida limite por tipo de servicio;
- valida cuenta destino contra identificacion del beneficiario;
- ejecuta transferencia real en Core;
- guarda UUIDs de debito y credito Core.

Estados posibles al terminar:

- `PROCESADO_TOTAL`: todas las lineas fueron exitosas.
- `PROCESADO_PARCIAL`: al menos una linea exitosa y alguna rechazada/fallida.
- `FALLIDO`: ninguna linea fue exitosa.

### 3. Liquidacion automatica

Si el resultado es `PROCESADO_TOTAL` o `PROCESADO_PARCIAL`, el scheduler ejecuta automaticamente la liquidacion:

- calcula comision e IVA;
- debita la cuenta matriz por comision + IVA;
- acredita cuenta institucional de ingresos;
- acredita cuenta institucional de IVA;
- guarda UUIDs de transacciones Core;
- cierra el lote como `CERRADO`.

Si el lote queda `FALLIDO`, no se liquida porque no existen pagos exitosos que cobrar.

## Resultado esperado para frontend

El frontend solo necesita:

1. Cargar archivo.
2. Consultar estado del lote.
3. Consultar lineas si desea mostrar detalle.
4. Consultar novedades cuando el lote termine.
5. Consultar comprobante cuando el lote este `CERRADO`.

No debe llamar validar, procesar o liquidar en el flujo normal de usuario.

## Estados principales

```text
RECIBIDO
  -> VALIDANDO
  -> VALIDADO
  -> PROCESANDO
  -> PROCESADO_TOTAL / PROCESADO_PARCIAL
  -> CERRADO

ENCOLADO
  -> VALIDANDO
  -> VALIDADO
  -> PROCESANDO
  -> PROCESADO_TOTAL / PROCESADO_PARCIAL
  -> CERRADO

RECIBIDO / ENCOLADO
  -> VALIDANDO
  -> RECHAZADO

VALIDADO
  -> PROCESANDO
  -> FALLIDO
```

## Consideraciones

- El Core sigue siendo la fuente de verdad para calendario, cuentas, saldos, transferencias y liquidacion.
- La cola no usa Redis, RabbitMQ ni Kafka; se implementa con tablas del propio Switch.
- Los endpoints manuales se conservan para pruebas, pero el flujo funcional del portal queda automatizado.
- Si una liquidacion falla por error tecnico de Core, la cola registra el fallo y aplica la politica de reintento configurada.
