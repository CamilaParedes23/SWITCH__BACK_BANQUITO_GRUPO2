# Clases y estructura base del backend

Este documento enumera la estructura modular y las clases base que se implementaran en la primera fase del backend del sistema `Switch de Pagos Masivos Banco BanQuito`, usando como paquete base `com.banquito.switchpagos`.

## Alcance de esta fase

En esta fase solo se consideran:

- estructura de paquetes
- clase principal de Spring Boot
- clases `model` JPA
- enums
- carpetas reservadas para `dto`, `repository` y `service`

No se incluyen todavia:

- controllers
- servicios implementados
- repositories implementados
- logica de negocio
- validaciones funcionales
- endpoints
- pruebas

## Estructura base

```text
Backend/pagosmasivos/
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

## Clase principal

- `com.banquito.switchpagos.SwitchPagosApplication`

## Clases por modulo

### `common/enums`

Enums transversales derivados de constraints y catalogos del modelo fisico:

- `EstadoTipoServicioEnum`
- `TipoDatoParametroEnum`
- `EstadoLimiteTransaccionEnum`
- `EstadoTarifaServicioEnum`
- `CanalIngresoEnum`
- `FormatoArchivoEnum`
- `EstadoLoteEnum`
- `EstadoLineaPagoEnum`
- `EstadoColaProcesamientoEnum`
- `EstadoIntentoProcesamientoEnum`
- `EstadoDebitoLiquidacionEnum`
- `ConceptoDetalleLiquidacionEnum`
- `TipoNotificacionEnum`
- `EstadoEnvioNotificacionEnum`
- `TipoReporteEnum`
- `FormatoReporteEnum`
- `TipoActorAuditoriaEnum`

### `catalogo/model`

- `TipoServicio`

Responsabilidad:

- representar el catalogo maestro de tipos de servicio del switch

### `parametro/model`

- `ParametroSwitch`

Responsabilidad:

- representar parametros operativos persistidos del sistema

### `lote/model`

- `LotePago`
- `HistorialEstadoLote`
- `ColaProcesamiento`

Responsabilidad:

- representar el lote recibido
- registrar historial de cambios de estado
- persistir la programacion logica de lotes encolados

### `procesamiento/model`

- `LineaPago`
- `LimiteTransaccion`
- `IntentoProcesamiento`

Responsabilidad:

- representar cada instruccion individual de pago
- almacenar limites por tipo de servicio
- registrar intentos de procesamiento

### `tarifaje/model`

- `TarifaServicio`
- `LiquidacionServicio`
- `DetalleLiquidacion`

Responsabilidad:

- representar tarifas escalonadas
- registrar liquidacion de comision e IVA
- almacenar el detalle de movimientos contables

### `reporte/model`

- `NotificacionBeneficiario`
- `ReporteCierre`

Responsabilidad:

- persistir notificaciones a beneficiarios
- persistir comprobantes y reportes de novedades

### `auditoria/model`

- `BitacoraAuditoriaSwitch`

Responsabilidad:

- registrar auditoria operativa del sistema

## Modulos sin clases JPA en esta fase

### `archivo`

No se crean entidades JPA en esta fase.

Razon:

- la informacion del archivo ya queda persistida principalmente en `LotePago` y `LineaPago`

### `integracioncore`

No se crean entidades JPA en esta fase.

Razon:

- el Core Bancario es externo
- sus referencias se manejaran como campos simples, no como entidades ni relaciones JPA

## Relaciones JPA esperadas

Relaciones unidireccionales de hijo hacia padre:

- `LimiteTransaccion -> TipoServicio`
- `LotePago -> TipoServicio`
- `LineaPago -> LotePago`
- `HistorialEstadoLote -> LotePago`
- `ColaProcesamiento -> LotePago`
- `IntentoProcesamiento -> ColaProcesamiento`
- `TarifaServicio -> TipoServicio`
- `LiquidacionServicio -> LotePago`
- `LiquidacionServicio -> TarifaServicio`
- `DetalleLiquidacion -> LiquidacionServicio`
- `NotificacionBeneficiario -> LineaPago`
- `ReporteCierre -> LotePago`

## Campos que deben quedar como referencias logicas

No deben mapearse como relaciones JPA:

- `ID_CREDENCIAL_WEB_CORE`
- `CUENTA_MATRIZ_CARGO`
- `CUENTA_DESTINO`
- `UUID_DEBITO_CORE`
- `UUID_CREDITO_CORE`
- `UUID_GRUPO_CORE`
- `UUID_TRANSACCION_CORE`
- `CUENTA_ORIGEN_CORE`
- `CUENTA_DESTINO_CORE`

## Reglas tecnicas para las clases `model`

- usar `model`, no `entity`
- no usar Lombok
- no usar tipos primitivos
- usar `BigDecimal` para montos
- usar `UUID` para identificadores UUID
- usar `OffsetDateTime` para `TIMESTAMPTZ`
- usar `LocalDate` para `DATE`
- usar `JsonNode` para `JSONB` cuando aplique
- usar `String` para `INET`
- incluir constructor vacio
- incluir constructor solo con PK
- implementar manualmente getters y setters
- implementar `equals` y `hashCode` con base en la PK
- implementar `toString` sin relaciones completas
- usar `@Version` en campos `VERSION`
- usar `@Enumerated(EnumType.STRING)` en campos controlados por enum
- usar `@Table(schema = "switch_banquito", name = "...")`
- usar `@Column(name = "...")` en todos los campos persistentes

## Lista total de clases esperadas en esta fase

### Aplicacion

- `SwitchPagosApplication`

### Enums

- `EstadoTipoServicioEnum`
- `TipoDatoParametroEnum`
- `EstadoLimiteTransaccionEnum`
- `EstadoTarifaServicioEnum`
- `CanalIngresoEnum`
- `FormatoArchivoEnum`
- `EstadoLoteEnum`
- `EstadoLineaPagoEnum`
- `EstadoColaProcesamientoEnum`
- `EstadoIntentoProcesamientoEnum`
- `EstadoDebitoLiquidacionEnum`
- `ConceptoDetalleLiquidacionEnum`
- `TipoNotificacionEnum`
- `EstadoEnvioNotificacionEnum`
- `TipoReporteEnum`
- `FormatoReporteEnum`
- `TipoActorAuditoriaEnum`

### Modelos

- `TipoServicio`
- `ParametroSwitch`
- `LotePago`
- `HistorialEstadoLote`
- `ColaProcesamiento`
- `LineaPago`
- `LimiteTransaccion`
- `IntentoProcesamiento`
- `TarifaServicio`
- `LiquidacionServicio`
- `DetalleLiquidacion`
- `NotificacionBeneficiario`
- `ReporteCierre`
- `BitacoraAuditoriaSwitch`
