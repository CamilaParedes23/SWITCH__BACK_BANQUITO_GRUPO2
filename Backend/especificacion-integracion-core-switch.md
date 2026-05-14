# Especificacion de integracion entre el Switch y el Core Bancario

Este documento define lo que el `Switch de Pagos Masivos Banco BanQuito` necesita del Core Bancario para completar su integracion en una fase posterior.

## Objetivo

- Establecer el contrato funcional minimo que el Core debe exponer al Switch.
- Dejar claro que el Switch no administra datos maestros bancarios.
- Definir operaciones, datos requeridos, respuestas esperadas y errores funcionales.

## Alcance de la integracion

El Core Bancario es la fuente de verdad para:

- existencia y estado de clientes empresa
- activacion del servicio de pagos masivos
- existencia y estado de cuentas
- titularidad de cuentas
- saldo disponible
- ejecucion de debitos y creditos

El Switch utiliza esa informacion para cumplir principalmente:

- `RF-02` Validacion estructural y prevencion de fraude operativo
- `RF-03` Procesamiento financiero linea por linea
- `RF-07` Liquidacion contable de servicios

## Principios de integracion

- El Switch no debe crear entidades JPA del Core.
- El Switch solo persiste referencias logicas devueltas por el Core.
- El Core debe exponer operaciones sincrónicas para validacion y movimiento.
- Toda invocacion al Core debe ser trazable mediante `uuidOperacionSwitch`.
- Los errores tecnicos del Core deben traducirse a causales funcionales del Switch.

## Operaciones requeridas del Core

## 1. Consultar cliente empresa

### Proposito

Validar que el `RUC` exista y que tenga activo el servicio de pagos masivos.

### Uso en el Switch

- `RF-02`
- validacion de cabecera antes de iniciar procesamiento financiero

### Request esperado

```json
{
  "ruc": "1790012345001"
}
```

### Response esperada

```json
{
  "ruc": "1790012345001",
  "razonSocial": "EMPRESA DEMO S.A.",
  "estadoCliente": "ACTIVO",
  "servicioPagosMasivosActivo": true
}
```

### Validaciones que habilita

- cliente existe
- cliente activo
- servicio de pagos masivos activo

## 2. Consultar cuenta

### Proposito

Validar existencia, estado y titularidad de una cuenta bancaria.

### Uso en el Switch

- `RF-03`
- validacion de cuenta matriz
- validacion de cuenta destino

### Request esperado

```json
{
  "numeroCuenta": "12345678901234567890"
}
```

### Response esperada

```json
{
  "numeroCuenta": "12345678901234567890",
  "identificacionTitular": "1790012345001",
  "nombreTitular": "EMPRESA DEMO S.A.",
  "estadoCuenta": "ACTIVA",
  "permiteDepositos": true,
  "moneda": "USD"
}
```

### Validaciones que habilita

- cuenta existe
- cuenta activa
- titularidad de cuenta destino
- cuenta destino permite depositos

## 3. Consultar saldo

### Proposito

Obtener el saldo disponible de la cuenta matriz antes de ejecutar cada linea o movimientos de liquidacion.

### Uso en el Switch

- `RF-03`
- `RF-07`

### Request esperado

```json
{
  "numeroCuenta": "12345678901234567890"
}
```

### Response esperada

```json
{
  "numeroCuenta": "12345678901234567890",
  "saldoDisponible": 25000.75,
  "saldoContable": 25100.75,
  "moneda": "USD"
}
```

## 4. Debitar cuenta

### Proposito

Debitar fondos desde la cuenta matriz o desde la cuenta origen definida para movimientos contables.

### Uso en el Switch

- `RF-03` debito por linea
- `RF-07` debito consolidado de comision e IVA

### Request esperado

```json
{
  "numeroCuenta": "12345678901234567890",
  "monto": 100.00,
  "moneda": "USD",
  "referencia": "NOMINA MAYO",
  "uuidOperacionSwitch": "8f4cbf6f-6db5-4fd0-aeb0-e9fc2bcb0e11",
  "permiteSobregiro": false
}
```

### Response esperada

```json
{
  "uuidMovimiento": "13dbaf8d-2e12-4cf8-bc23-8c8d4670f501",
  "codigoResultado": "OK",
  "mensajeResultado": "Debito ejecutado correctamente",
  "fechaProceso": "2026-05-10T15:20:00Z"
}
```

## 5. Acreditar cuenta

### Proposito

Acreditar fondos a una cuenta destino o cuenta contable definida por el banco.

### Uso en el Switch

- `RF-03` credito al beneficiario
- `RF-07` credito a ingresos e IVA

### Request esperado

```json
{
  "numeroCuenta": "09876543210987654321",
  "monto": 100.00,
  "moneda": "USD",
  "referencia": "NOMINA MAYO",
  "uuidOperacionSwitch": "8f4cbf6f-6db5-4fd0-aeb0-e9fc2bcb0e11"
}
```

### Response esperada

```json
{
  "uuidMovimiento": "c9ca59d6-4c98-4b0e-a454-a2f6f3fe610f",
  "codigoResultado": "OK",
  "mensajeResultado": "Credito ejecutado correctamente",
  "fechaProceso": "2026-05-10T15:20:02Z"
}
```

## Relacion entre operaciones y requisitos funcionales

| Requisito | Operaciones Core necesarias |
|---|---|
| `RF-02` | Consultar cliente empresa |
| `RF-03` | Consultar cuenta, consultar saldo, debitar cuenta, acreditar cuenta |
| `RF-07` | Consultar saldo, debitar cuenta, acreditar cuenta |

## Catalogo minimo de errores del Core

El Core deberia responder un catalogo consistente para que el Switch pueda traducirlo a mensajes funcionales.

### Errores de cliente

- `CLIENTE_NO_EXISTE`
- `CLIENTE_INACTIVO`
- `SERVICIO_NO_ACTIVO`

### Errores de cuenta origen

- `CUENTA_ORIGEN_NO_EXISTE`
- `CUENTA_ORIGEN_INACTIVA`
- `SALDO_INSUFICIENTE`
- `MONEDA_NO_COMPATIBLE`

### Errores de cuenta destino

- `CUENTA_DESTINO_NO_EXISTE`
- `CUENTA_DESTINO_INACTIVA`
- `CUENTA_BLOQUEADA`
- `NO_PERMITE_DEPOSITOS`
- `TITULARIDAD_NO_COINCIDE`

### Errores operativos y tecnicos

- `TIMEOUT_CORE`
- `TRANSACCION_RECHAZADA`
- `ERROR_TECNICO_CORE`
- `UUID_DUPLICADO`

## Reglas tecnicas recomendadas

## Idempotencia

- El Switch debe enviar `uuidOperacionSwitch` en cada operacion financiera.
- El Core debe ser capaz de detectar repeticion de una misma operacion.

## Trazabilidad

- El Core debe devolver `uuidMovimiento` por cada debito o credito.
- El Switch persistira estos UUID como referencia logica.

## Timeouts

- El contrato del Core debe definir timeout maximo por operacion.
- El Switch debe tratar timeout como error recuperable o rechazo segun el caso de uso.

## Formato monetario

- Todos los montos deben manejarse como valores decimales exactos.
- La moneda debe viajar de forma explicita cuando el contrato lo requiera.

## Zona horaria

- Las fechas tecnicas deben devolverse en formato compatible con `OffsetDateTime`.

## Reglas funcionales que debe respetar el Core

- La cuenta destino debe poder validarse por numero de cuenta y titularidad.
- El saldo debe reflejar disponibilidad real para ejecutar la linea.
- El debito y el credito deben responder con identificadores unicos.
- La validacion de servicio activo del cliente empresa debe ser deterministica.

## Dependencia del backend actual

El backend del Switch ya cuenta con una interfaz de dominio para encapsular la integracion:

- `Backend/pagosmasivos/src/main/java/com/banquito/switchpagos/integracioncore/service/CoreBancarioService.java`

Actualmente esa interfaz esta respaldada por una implementacion simulada. Cuando exista el contrato oficial del Core, el cambio esperado es reemplazar la implementacion simulada por un cliente HTTP o adaptador real, sin rediseñar la logica principal del Switch.

## Alcance fuera de este documento

Este documento no define:

- autenticacion real contra el Core
- detalle de seguridad de transporte
- certificados, VPN o mecanismos de red
- SLA operativos definitivos

Esos puntos deberan acordarse con el equipo responsable del Core y de infraestructura.

## Conclusiones

- El Switch no necesita el modelo de datos interno del Core; necesita un contrato claro de servicios.
- Las operaciones minimas del Core ya pueden definirse desde ahora, aunque la integracion real se haga despues.
- Este contrato permite consolidar el Switch y dejar preparada la futura integracion sin rehacer la logica del backend.
