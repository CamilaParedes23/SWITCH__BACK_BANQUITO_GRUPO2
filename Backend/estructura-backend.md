# Estructura propuesta del backend

Este documento define la estructura de carpetas que se implementara en `Backend/pagosmasivos`, usando como paquete base `com.banquito.switchpagos`.

## Criterios aplicados

- Monolito modular en Spring Boot.
- PostgreSQL como base de datos.
- Programacion contra interfaces en los servicios principales.
- `model` y `repository` encapsulados dentro de su modulo.
- Comunicacion entre modulos solo a traves de `service`.
- Separacion entre DTOs de API y DTOs internos.
- Sin microservicios.
- Sin modulo de colas.
- Solo capas conocidas y con responsabilidad clara.
- Sin enlaces, credenciales o rutas fijas dentro del codigo.
- Configuracion externa orientada a facilitar despliegue en distintos ambientes.

## Estructura de carpetas

```text
Backend/pagosmasivos/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banquito/
│   │   │           └── switchpagos/
│   │   │               ├── SwitchPagosApplication.java
│   │   │               ├── config/
│   │   │               ├── common/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── exception/
│   │   │               │   ├── response/
│   │   │               │   └── util/
│   │   │               ├── catalogo/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── parametro/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── lote/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── archivo/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── parser/
│   │   │               │   ├── repository/
│   │   │               │   ├── service/
│   │   │               │   └── validator/
│   │   │               ├── procesamiento/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── tarifaje/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── reporte/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── enums/
│   │   │               │   ├── model/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               ├── integracioncore/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── api/
│   │   │               │   │   └── internal/
│   │   │               │   ├── client/
│   │   │               │   ├── repository/
│   │   │               │   └── service/
│   │   │               └── auditoria/
│   │   │                   ├── dto/
│   │   │                   │   ├── api/
│   │   │                   │   └── internal/
│   │   │                   ├── enums/
│   │   │                   ├── model/
│   │   │                   ├── repository/
│   │   │                   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-test.properties
│   │       ├── application-prod.properties
│   │       ├── sql/
│   │       ├── templates/
│   │       └── samples/
```

## Ubicacion del modelo principal

- `catalogo/model`
  - `TipoServicio`
- `parametro/model`
  - `ParametroSwitch`
- `lote/model`
  - `LotePago`
  - `HistorialEstadoLote`
  - `ColaProcesamiento`
- `procesamiento/model`
  - `LineaPago`
  - `LimiteTransaccion`
  - `IntentoProcesamiento`
- `tarifaje/model`
  - `TarifaServicio`
  - `LiquidacionServicio`
  - `DetalleLiquidacion`
- `reporte/model`
  - `ReporteCierre`
  - `NotificacionBeneficiario`
- `auditoria/model`
  - `BitacoraAuditoriaSwitch`

## Descripcion de modulos

- `config`
  - Centraliza configuraciones tecnicas del proyecto, propiedades externas y beans de infraestructura.

- `common`
  - Contiene elementos reutilizables del sistema como DTOs comunes, enums compartidos, excepciones, respuestas estandar y utilitarios.

- `catalogo`
  - Maneja catalogos transversales del negocio, en esta fase principalmente `TIPO_SERVICIO`.

- `parametro`
  - Administra parametros operativos persistidos del switch, por ejemplo IVA, hora de corte, ventana de duplicidad y maximo de reintentos.

- `lote`
  - Administra el ciclo de vida del lote: recepcion, canal de ingreso, estado general, historial de cambios y programacion logica de lotes encolados.

- `archivo`
  - Se encarga del procesamiento del archivo recibido: lectura, parseo, validaciones estructurales y verificaciones de formato.

- `procesamiento`
  - Agrupa las instrucciones individuales de pago, los limites transaccionales y los intentos de procesamiento del flujo linea por linea.

- `tarifaje`
  - Maneja el tarifario comercial y la liquidacion contable del servicio, incluyendo detalle de movimientos y estado del debito.

- `reporte`
  - Maneja reportes de cierre y notificaciones a beneficiarios como salidas del proceso del lote.

- `integracioncore`
  - Encapsula la comunicacion con el Core Bancario. En esta fase solo se prepara la estructura del modulo, sin entidades JPA.

- `auditoria`
  - Conserva trazabilidad operativa y evidencia de acciones relevantes del switch mediante la bitacora de auditoria.

## Reglas de comunicacion entre modulos

- Un modulo no debe usar directamente el `repository` de otro modulo.
- Un modulo no debe usar `model` de otro modulo como contrato principal.
- La comunicacion entre modulos se hace por `service`.
- Para intercambio de datos se usaran DTOs internos en `dto/internal`.
- Para exposicion REST se usaran DTOs de API en `dto/api`.

## Uso de enums para valores controlados

Para evitar valores quemados en el codigo, todos los campos que en base de datos esten restringidos por `CHECK`, catalogos o listas cerradas deben manejarse con enums en Java.

### Regla general

- No usar literales como `"ACTIVO"`, `"RECIBIDO"`, `"CSV"`, `"PORTAL_WEB"` o similares dentro de la logica.
- Los valores controlados deben representarse con enums.
- Los enums deben alinearse con los valores permitidos por la base de datos.
- Si un valor cambia por decision funcional, primero debe ajustarse el modelo de datos y luego el enum correspondiente.

### Ubicacion sugerida

- Enums compartidos en `common/enums`.
- Enums muy especificos de un modulo pueden vivir dentro del propio modulo si no se reutilizan.

### Casos que deben salir de constraints o catalogos

- Estados de lote.
- Estados de linea.
- Estado de cola de procesamiento, aunque la entidad viva dentro del modulo `lote`.
- Estados de liquidacion.
- Estados de envio de notificacion.
- Tipos de notificacion.
- Tipos de reporte.
- Tipos de dato de parametro.
- Estado de tipo de servicio.
- Estado de limite transaccional.
- Estado de tarifa.
- Canal de ingreso.
- Formato de archivo.
- Concepto de detalle de liquidacion.
- Tipo de actor de auditoria.

### Beneficio esperado

- Se reduce el uso de strings quemados.
- Se mantiene coherencia entre codigo y base de datos.
- Se facilita validacion, mantenimiento y despliegue.

### Enums identificados desde los constraints del modelo fisico

Los siguientes enums deben definirse para representar de forma explicita los valores cerrados del script `DB/modeloFisicoBD_Switch_v4_postgresql.sql`.

- `common/enums/EstadoTipoServicioEnum`
  - `ACTIVO`
  - `INACTIVO`

- `common/enums/TipoDatoParametroEnum`
  - `NUMERICO`
  - `CADENA`
  - `FECHA`
  - `HORA`
  - `BOOLEANO`
  - `JSON`

- `common/enums/EstadoLimiteTransaccionEnum`
  - `ACTIVO`
  - `INACTIVO`

- `common/enums/EstadoTarifaServicioEnum`
  - `ACTIVA`
  - `INACTIVA`

- `common/enums/CanalIngresoEnum`
  - `PORTAL_WEB`
  - `SFTP`
  - `API`

- `common/enums/FormatoArchivoEnum`
  - `CSV`
  - `TXT`

- `common/enums/EstadoLoteEnum`
  - `RECIBIDO`
  - `VALIDANDO`
  - `VALIDADO`
  - `RECHAZADO`
  - `ENCOLADO`
  - `PROCESANDO`
  - `PROCESADO_PARCIAL`
  - `PROCESADO_TOTAL`
  - `CERRADO`
  - `ANULADO`

- `common/enums/EstadoLineaPagoEnum`
  - `PENDIENTE`
  - `VALIDADA`
  - `RECHAZADA`
  - `ENVIADA_CORE`
  - `EXITOSA`
  - `FALLIDA`
  - `REVERSADA`

- `common/enums/EstadoColaProcesamientoEnum`
  - `PENDIENTE`
  - `TOMADO`
  - `PROCESANDO`
  - `COMPLETADO`
  - `FALLIDO`
  - `REINTENTO`
  - `CANCELADO`

- `common/enums/EstadoIntentoProcesamientoEnum`
  - `INICIADO`
  - `COMPLETADO`
  - `FALLIDO`
  - `CANCELADO`

- `common/enums/EstadoDebitoLiquidacionEnum`
  - `PENDIENTE`
  - `COMPLETADO`
  - `RECHAZADO`
  - `REVERSADO`

- `common/enums/ConceptoDetalleLiquidacionEnum`
  - `DEBITO_CUENTA_MATRIZ`
  - `CREDITO_INGRESOS`
  - `CREDITO_IVA`
  - `REVERSO`

- `common/enums/TipoNotificacionEnum`
  - `PAGO_EXITOSO`
  - `PAGO_RECHAZADO`
  - `PAGO_REVERSADO`

- `common/enums/EstadoEnvioNotificacionEnum`
  - `PENDIENTE`
  - `ENVIADA`
  - `ERROR`
  - `CANCELADA`

- `common/enums/TipoReporteEnum`
  - `COMPROBANTE_LIQUIDACION`
  - `REPORTE_NOVEDADES`

- `common/enums/FormatoReporteEnum`
  - `PDF`
  - `CSV`
  - `XLSX`
  - `JSON`

- `common/enums/TipoActorAuditoriaEnum`
  - `EMPRESA`
  - `USUARIO_CORE`
  - `SISTEMA`
  - `API`

### Regla de implementacion para enums

- Cada campo de base con `CHECK ... IN (...)` debe mapearse a un enum.
- Cada enum debe mantener exactamente los mismos literales definidos en base de datos.
- En modelos JPA se recomienda usar `@Enumerated(EnumType.STRING)` para conservar legibilidad y alineacion con PostgreSQL.
- No se deben repetir estos valores como strings en servicios, validadores, controllers o repositories.

## Responsabilidad por capa

- `controller`: expone endpoints y delega al servicio.
- `service`: contiene la logica y orquestacion del modulo.
- `repository`: resuelve persistencia del modulo.
- `model`: representa entidades persistidas con JPA.
- `dto`: define contratos de entrada y salida.
- `parser`: interpreta archivos de entrada.
- `validator`: valida estructura y reglas del archivo.

## Configuracion por ambiente y despliegue

El backend debe poder desplegarse en distintos ambientes sin modificar el codigo fuente.

### Archivos de configuracion

```text
src/main/resources/
├── application.properties
├── application-dev.properties
├── application-test.properties
├── application-prod.properties
├── sql/
├── templates/
└── samples/
```

### Regla general

- Toda configuracion sensible o dependiente del ambiente debe salir de variables de entorno o de archivos `application-*.properties`.
- No deben existir enlaces hardcodeados en clases Java.
- No deben existir credenciales hardcodeadas en clases Java.
- No deben existir rutas absolutas hardcodeadas en clases Java.
- No deben existir puertos, hosts, cuentas o correos quemados en el codigo.
- La logica del sistema debe ser la misma en `dev`, `test` y `prod`; solo cambia la configuracion.

### Variables de entorno sugeridas

- `SERVER_PORT`
- `APP_TIMEZONE`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_SCHEMA`
- `DB_USER`
- `DB_PASSWORD`
- `CORE_BASE_URL`
- `CORE_CONNECT_TIMEOUT`
- `CORE_READ_TIMEOUT`
- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_USER`
- `SMTP_PASSWORD`
- `SMTP_FROM`
- `LOG_LEVEL`

### Ubicacion recomendada de propiedades tipadas

- `config/properties/ApplicationProperties`
- `config/properties/DatabaseProperties`
- `config/properties/CoreProperties`
- `config/properties/MailProperties`

## Orden sugerido de implementacion

1. `common`, `config`
2. `catalogo`, `parametro`
3. `lote`, `archivo`, `procesamiento`
4. `tarifaje`, `reporte`
5. `integracioncore`, `auditoria`
6. Controllers y DTOs API
