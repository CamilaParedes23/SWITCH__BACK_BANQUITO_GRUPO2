# Documento de Requisitos Funcionales
## Switch de Pagos Masivos (V1)

- Entidad: Banco BanQuito
- Version del documento fuente: 1.3
- Fecha del documento fuente: Abril 2026
- Fuente: `Requisitos/BancoBanQuito-RequisitosFuncionales-SwitchPagosMasivos.pdf`

## 1. Introduccion y antecedentes

Banco BanQuito enfrenta una crisis de retencion en su segmento de Banca Empresarial debido a la falta de automatizacion y a retrasos en la liquidacion de fondos. Esto ha provocado la perdida de 15 clientes corporativos grandes hacia la competencia.

### Impacto del problema

- Fuga de capitales superior a 45 millones de dolares mensuales.
- Perdida proyectada de 500,000 dolares anuales en ingresos por comisiones transaccionales.
- Alta frustracion de los clientes corporativos, especialmente CFOs y responsables financieros.
- Afectacion operativa a empleados y proveedores de las empresas clientes por pagos tardios.

### Objetivo del negocio

Implementar un Switch de Pagos Masivos altamente confiable que:

- Devuelva autonomia operativa a las empresas.
- Garantice liquidacion exacta y oportuna.
- Recupere la competitividad del banco.
- Reactive una fuente clave de ingresos por servicios no financieros.

## 2. Contexto de negocio

### 2.1 Valor del cliente corporativo

El cliente corporativo tiene un impacto mucho mayor que el cliente individual.

#### Razones

- Mantiene saldos altos en cuentas a la vista.
- Genera captacion indirecta de empleados que abren cuentas para recibir pagos.
- Produce un volumen elevado y recurrente de transacciones.
- Aporta ingresos por comisiones masivas.

#### Riesgo de perdida

Perder un cliente corporativo significa:

- Perder liquidez central.
- Perder ingresos por pagos masivos.
- Perder potencialmente miles de clientes individuales vinculados a la nomina.

### 2.2 Que son los pagos masivos

Los pagos masivos consisten en procesar cientos o miles de pagos simultaneos a partir de un lote de instrucciones enviado por una empresa.

### Flujo conceptual

- La empresa envia un archivo con instrucciones.
- El banco debita los fondos de una cuenta matriz.
- El sistema dispersa esos fondos a las cuentas destino de los beneficiarios.
- Todo el proceso debe ser automatizado.

### Casos de uso mencionados

- Nomina quincenal.
- Pagos a proveedores.

### 2.3 Ingresos por comisiones e impuestos

El banco cobra una comision por el servicio prestado y debe aplicar IVA.

#### Reglas contables obligatorias

- El valor neto de la comision debe ir a una cuenta contable de ingresos.
- El valor del IVA debe ir a una cuenta contable de pasivos por pagar.
- No se pueden mezclar ambos fondos.
- La separacion correcta de estos valores es un requisito regulatorio y contable.

#### Cuentas contables involucradas

- Cuenta contable de ingresos por tarifas.
- Cuenta contable de pasivos por impuestos por pagar.

## 3. Esquema tarifario comercial

La tarifa se calcula exclusivamente sobre las transacciones exitosas del lote.

### Tabla de tarifas

| Volumen de transacciones exitosas | Tarifa unitaria |
|---|---:|
| 1 a 10 | 0.50 |
| 11 a 100 | 0.40 |
| 101 a 500 | 0.30 |
| 501 a 1,000 | 0.20 |
| 1,001 a 10,000 | 0.10 |
| 10,001 en adelante | 0.05 |

### Regla tributaria

Al subtotal de comision generado se debe aplicar la tasa de IVA vigente del 15%.

## 4. Alcance de la fase 1

La primera fase cubre unicamente el procesamiento intrabancario.

### Alcance funcional

- Cuentas de origen pertenecientes a BanQuito.
- Cuentas de destino pertenecientes a BanQuito.
- Procesamiento sincronico.
- Procesamiento unitario, linea por linea.
- Calculo automatico de comisiones e impuestos.
- Liquidacion consolidada posterior de costos del servicio.

### Canales de entrada

- Portal Web de Banca Empresas.
- SFTP seguro.

### Sistemas externos relacionados

- Core Bancario.
- Servidor SMTP para notificaciones.

### Rol del switch

El Switch de Pagos Masivos actua como orquestador central y debe:

- Recibir el archivo.
- Aplicar reglas de negocio.
- Comunicarse de forma sincronica con el Core.
- Despachar notificaciones.
- Generar reportes para la empresa emisora.

## 5. Especificacion avanzada del archivo de entrada

El sistema debe procesar archivos planos estructurados con tres bloques obligatorios.

### 5.1 Registro de cabecera

Debe incluir:

- Identificador del cliente: RUC de la empresa.
- Tipo de servicio: por ejemplo `NOM` o `PRV`.
- Fecha y hora de generacion.
- Cuenta matriz de cargo.
- Total de registros.
- Monto total de control.

### 5.2 Registros de detalle

Cada linea de detalle debe incluir:

- Secuencial.
- Identificacion del beneficiario.
- Nombre del beneficiario.
- Cuenta destino.
- Monto a transferir.
- Referencia o concepto.
- Correo de notificacion.

### 5.3 Registro de pie de control

Debe incluir:

- Hash o codigo de seguridad.
- Suma de verificacion del monto total y numero de registros.

## 6. Requisitos funcionales detallados

### RF-01. Ingesta y horarios de corte

El sistema debe recibir instrucciones mediante:

- Portal Web de Banca Empresas, con carga manual.
- Buzon SFTP seguro, con carga automatizada.

#### Regla de horario

- Archivos recibidos antes de las 18:00 deben procesarse inmediatamente.
- Archivos recibidos despues de las 18:00 deben quedar en estado `Encolado`.
- Archivos recibidos en fines de semana o feriados tambien deben quedar `Encolado`.
- El procesamiento de archivos encolados debe iniciar automaticamente a las 00:01 del siguiente dia habil.

#### Regla especial para SFTP

- Se asume el uso de la cuenta marcada como favorita para pagos.

### RF-02. Validacion estructural y prevencion de fraude operativo

Antes de invocar al Core Bancario, el sistema debe rechazar completamente el archivo si ocurre cualquiera de estas condiciones:

#### Validaciones obligatorias

- La suma de montos del detalle no coincide con cabecera y pie.
- El RUC de cabecera no corresponde a un cliente con el servicio activo.
- El mismo nombre de archivo y hash ya fue procesado con exito en los ultimos 30 dias.

#### Objetivo de estas validaciones

- Prevenir inconsistencias.
- Evitar procesamiento de clientes no autorizados.
- Evitar duplicidad accidental de nomina o pagos masivos.

### RF-03. Procesamiento financiero linea por linea

El sistema debe iterar de forma secuencial por cada linea del detalle.

#### Reglas por linea

1. Validacion de limites:
   - Verificar que el monto individual no exceda el limite maximo permitido para el tipo de transaccion.

2. Validacion de origen:
   - Verificar saldo disponible en la cuenta matriz para cubrir el monto de esa linea.

3. Validacion de destino:
   - Confirmar que la cuenta destino exista.
   - Confirmar que la cuenta destino pertenezca a la identificacion indicada.
   - Confirmar que su estado permita depositos.
   - Rechazar si esta bloqueada o inactiva.

4. Liquidacion:
   - Generar el debito en la cuenta origen.
   - Generar el credito en la cuenta destino.

### RF-04. Resiliencia transaccional del lote

La falla de una linea individual no debe abortar el archivo completo.

#### Comportamiento esperado

- Registrar el error de la linea fallida.
- Marcar la linea como `Rechazada`.
- Guardar la causal del rechazo.
- Continuar con la siguiente instruccion hasta terminar todo el lote.

#### Objetivo

Permitir procesamiento parcial exitoso del lote sin perder trazabilidad de errores individuales.

### RF-05. Notificacion inmediata al beneficiario

Despues de que una linea se liquide exitosamente, el sistema debe generar una orden de notificacion al correo del beneficiario.

#### Contenido minimo de la notificacion

- Monto acreditado.
- Concepto del pago.
- Nombre de la empresa emisora.

#### Momento

- Inmediatamente despues del estado `Exitoso` de la linea.

### RF-06. Calculo de tarifaje y comisiones

Al finalizar el procesamiento de todas las lineas, el sistema debe calcular el costo del servicio.

#### Pasos del calculo

1. Contabilizar el numero exacto de transacciones exitosas.
2. Determinar la tarifa unitaria aplicable segun el volumen exitoso.
3. Calcular subtotal de comision:
   - `transacciones_exitosas * tarifa_unitaria`
4. Calcular monto del IVA:
   - `subtotal_comision * 0.15`
5. Calcular total a debitar por servicios:
   - `subtotal_comision + monto_iva`

#### Regla critica

Solo deben considerarse transacciones con estado exitoso para determinar tarifa y comision.

### RF-07. Liquidacion contable de servicios

Despues del calculo de tarifas, el sistema debe ejecutar automaticamente la liquidacion contable en el Core.

#### Movimientos obligatorios

1. Debito global:
   - Un unico debito a la cuenta matriz por el total a debitar por servicios.

2. Credito a ingresos:
   - Un credito por el subtotal de comision a la cuenta interna `INGRESOS_SERVICIOS_MASIVOS`.

3. Credito a impuestos:
   - Un credito por el monto del IVA a la cuenta interna `PASIVOS_IVA_RETENIDO`.

#### Regla especial de negocio

Si no existe saldo remanente suficiente para cubrir la comision al final del proceso, el debito debe realizarse de todas formas, permitiendo sobregiro en la cuenta matriz.

### RF-08. Cuadre y reporte de cierre

Una vez ejecutados los movimientos contables, el sistema debe:

- Cambiar el estado del archivo a `Procesado`.
- Generar salidas de cierre para la empresa.

#### Entregables obligatorios

1. Comprobante de liquidacion corporativa:
   - Monto total dispersado con exito.
   - Detalle del calculo de la comision.
   - IVA retenido.
   - Total final debitado.

2. Reporte de novedades:
   - Estado definitivo de cada linea.
   - Indicacion de `Exitosa` o `Rechazada`.
   - Codigo o motivo explicito del rechazo cuando aplique.

## 7. Reglas de negocio derivadas del documento

### Reglas de procesamiento

- El lote se procesa linea por linea y de forma sincronica.
- El lote puede tener resultados mixtos: lineas exitosas y lineas rechazadas.
- El calculo del cobro del servicio ocurre despues de terminar el lote.
- La liquidacion contable del servicio es consolidada.

### Reglas de validacion

- Debe existir validacion estructural completa previa al procesamiento financiero.
- Debe existir control de duplicidad por nombre de archivo y hash en ventana de 30 dias.
- Debe verificarse habilitacion del cliente para usar pagos masivos.
- Deben verificarse limites, saldo, estado de cuenta y correspondencia del beneficiario.

### Reglas contables

- Comision e IVA deben separarse contablemente.
- El total de servicios se debita globalmente al final.
- El cobro del servicio puede llevar a sobregiro.
- El IVA no constituye ingreso del banco.

### Reglas de operacion

- Existen dos canales de ingreso.
- Debe existir una cola de procesamiento por horario y dia habil.
- Debe existir integracion con Core Bancario.
- Debe existir integracion con servidor SMTP.
- Deben generarse reportes para la empresa.

## 8. Entidades funcionales que el sistema necesita manejar

Aunque el documento no define explicitamente el modelo de datos, funcionalmente se requiere manejar al menos:

- Lote de pagos.
- Cabecera del archivo.
- Detalle de pagos.
- Pie de control.
- Cliente empresa.
- Servicio de pagos masivos habilitado.
- Cuenta matriz.
- Cuenta destino.
- Beneficiario.
- Estado de linea.
- Estado de lote.
- Cola de procesamiento.
- Notificacion al beneficiario.
- Tarifario comercial.
- Liquidacion de comision.
- Liquidacion de IVA.
- Reporte de cierre.
- Reporte de novedades.
- Trazabilidad de errores y rechazos.

## 9. Casos funcionales principales

### Caso 1. Carga y aceptacion de lote

- La empresa envia archivo por Web o SFTP.
- El sistema valida horario y decide procesamiento inmediato o encolado.
- El sistema valida estructura y duplicidad.
- Si todo es correcto, el lote avanza a procesamiento.

### Caso 2. Rechazo temprano de lote

- El sistema detecta inconsistencia de control, cliente no habilitado o duplicidad.
- El lote se rechaza antes de afectar el Core.
- Debe quedar evidencia de la causa.

### Caso 3. Procesamiento parcial exitoso

- Algunas lineas pasan validaciones y otras no.
- Las lineas exitosas se liquidan.
- Las fallidas quedan rechazadas con motivo.
- El lote no se aborta.

### Caso 4. Cobro del servicio

- Se cuentan solo las lineas exitosas.
- Se calcula tarifa.
- Se calcula comision e IVA.
- Se debita el total del servicio.
- Se separan contablemente ingresos e impuestos.

### Caso 5. Cierre operativo

- El sistema marca el lote como procesado.
- Genera comprobante de liquidacion.
- Genera reporte de novedades.
- Dispara o deja registro de notificaciones.

## 10. Consideraciones de implementacion derivadas

### Necesidades tecnicas evidentes

- Motor de parsing de archivos.
- Validaciones estructurales y de negocio.
- Manejo de cola por horario y dia habil.
- Orquestacion transaccional con Core Bancario.
- Persistencia de estados por lote y linea.
- Registro de errores por linea.
- Calculo tarifario parametrizable.
- Integracion SMTP o mecanismo de outbox para correo.
- Generacion de reportes estructurados.
- Auditoria y trazabilidad operativa.

### Consideraciones de control

- Idempotencia o prevencion de doble procesamiento.
- Trazabilidad completa por lote y por linea.
- Evidencia de rechazos y causales.
- Separacion entre procesamiento financiero y notificacion.
- Consistencia contable entre comision e IVA.

## 11. Lista de requisitos funcionales que se deberian implementar

### Requisitos funcionales base

- RF-01 Ingesta y horarios de corte.
- RF-02 Validacion estructural y prevencion de fraude operativo.
- RF-03 Procesamiento financiero linea por linea.
- RF-04 Resiliencia transaccional del lote.
- RF-05 Notificacion inmediata al beneficiario.
- RF-06 Calculo de tarifaje y comisiones.
- RF-07 Liquidacion contable de servicios.
- RF-08 Cuadre y reporte de cierre.

### Funcionalidades concretas a construir

- Recepcion de archivos por `Portal Web` y por `SFTP`.
- Validacion de horario de recepcion con regla `antes de 18:00` frente a `despues de 18:00`.
- Encolamiento automatico para fines de semana y feriados.
- Arranque automatico de procesamiento a `00:01` del siguiente dia habil.
- Parsing de archivo con `cabecera`, `detalle` y `pie`.
- Validacion de sumas de control y numero de registros.
- Validacion de RUC y habilitacion del cliente para pagos masivos.
- Deteccion de duplicidad por `nombre de archivo + hash` en ventana de `30 dias`.
- Validacion de limite maximo por tipo de transaccion.
- Validacion de saldo disponible de cuenta matriz por linea.
- Validacion de existencia, titularidad y estado de cuenta destino.
- Debito en cuenta origen y credito en cuenta destino por cada linea exitosa.
- Registro de rechazos por linea con motivo o codigo.
- Continuidad del lote ante fallas parciales.
- Generacion de notificacion al beneficiario para lineas exitosas.
- Conteo de transacciones exitosas.
- Aplicacion del tarifario escalonado.
- Calculo de subtotal de comision, IVA y total de servicio.
- Debito consolidado por servicios a la cuenta matriz.
- Credito contable a `INGRESOS_SERVICIOS_MASIVOS`.
- Credito contable a `PASIVOS_IVA_RETENIDO`.
- Permitir sobregiro para el debito de comision al cierre.
- Cambio de estado final del lote a `Procesado`.
- Generacion de comprobante de liquidacion corporativa.
- Generacion de reporte de novedades por linea.
- Trazabilidad de estados, errores y resultados por lote y por linea.

### Priorizacion sugerida

#### Prioridad alta

- RF-01
- RF-02
- RF-03
- RF-04
- RF-06
- RF-07
- RF-08

#### Prioridad media

- RF-05

RF-05 se ubica en prioridad media solo por dependencia tecnica respecto al flujo principal, pero sigue siendo un requisito obligatorio del alcance funcional.
