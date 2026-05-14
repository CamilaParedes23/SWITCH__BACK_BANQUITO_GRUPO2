# Backend Switch Pagos Masivos

Backend Spring Boot del sistema `Switch de Pagos Masivos Banco BanQuito`.

## Formato oficial del archivo

El backend usa un parser estructural con los siguientes registros y delimitador oficial `;`:

```text
CAB;rucEmpresa;codigoTipoServicio;fechaHoraGeneracion;cuentaMatrizCargo;totalRegistros;montoTotal
DET;secuencial;identificacionBeneficiario;nombreBeneficiario;cuentaDestino;monto;conceptoReferencia;correoNotificacion
PIE;hashControl;totalRegistros;montoTotal
```

Reglas actuales:

- se acepta solo `;` como delimitador
- se acepta solo `CAB`, `DET`, `PIE`
- debe existir una sola `CAB`
- debe existir una sola `PIE`
- debe existir al menos una `DET`
- el orden debe ser `CAB -> DET -> PIE`
- `correoNotificacion` es opcional en `DET`

## Requisitos

- Java 21
- Maven Wrapper incluido en el proyecto
- PostgreSQL con el schema `switch_banquito`
- Script base aplicado desde `DB/modeloFisicoBD_Switch_v4_postgresql.sql`

## Variables de entorno

Usa `Backend/pagosmasivos/.env` como archivo de trabajo y `Backend/pagosmasivos/.env.example` como referencia.

Variables principales:

- `SERVER_PORT`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_SCHEMA`
- `DB_USER`
- `DB_PASSWORD`
- `MULTIPART_MAX_FILE_SIZE`
- `MULTIPART_MAX_REQUEST_SIZE`
- `LOG_LEVEL_WEB`
- `LOG_LEVEL_APP`
- `SCHEDULER_COLA_DELAY_MS`

## Perfiles

- `dev`
- `test`
- `prod`

## Ejecucion

### 1. Compilar

```bash
./mvnw clean compile
```

### 2. Ejecutar en desarrollo

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Empaquetar

```bash
./mvnw clean package
```

## Ejecucion con Docker Compose

### 1. Revisar variables

- Usa `Backend/pagosmasivos/.env` para ejecutar localmente.
- Usa `Backend/pagosmasivos/.env.example` como plantilla o respaldo.
- Si no defines variables, `docker-compose.yml` usa valores por defecto locales.

### 2. Levantar PostgreSQL y backend

```bash
docker compose up --build
```

### 3. Levantar en segundo plano

```bash
docker compose up --build -d
```

### 4. Detener contenedores

```bash
docker compose down
```

### 5. Reiniciar desde cero

```bash
docker compose down -v
docker compose up --build
```

## Inicializacion de base local

- El contenedor de PostgreSQL carga automaticamente:
  - `DB/modeloFisicoBD_Switch_v4_postgresql.sql`
  - `Backend/pagosmasivos/docker/postgres/02-seed-dev.sql`
- La semilla local asegura parametros operativos basicos para pruebas manuales.

## Endpoints disponibles

- `POST /api/v1/pagos-masivos/lotes`
- `GET /api/v1/pagos-masivos/lotes`
- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/estado`
- `DELETE /api/v1/pagos-masivos/lotes/{uuidLote}`
- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/validar`
- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/lineas`
- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/procesar`
- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/liquidar`
- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/novedades`
- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/comprobante`
- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/notificaciones`
- `POST /api/v1/pagos-masivos/notificaciones/procesar`
- `GET /api/v1/pagos-masivos/tarifas`
- `GET /api/v1/pagos-masivos/horarios-corte`

## Regla de lotes encolados

- Un lote `ENCOLADO` no puede procesarse ni liquidarse manualmente.
- El cambio `ENCOLADO -> VALIDADO` ocurre automaticamente cuando llega la `FECHA_PROGRAMADA_PROCESO`.
- El scheduler interno usa `SCHEDULER_COLA_DELAY_MS` para revisar lotes pendientes.

## Archivos de ejemplo

Archivos para pruebas manuales:

- `src/main/resources/samples/lotes-validos/lote-valido.csv`
- `src/main/resources/samples/lotes-validos/lote-parcial.csv`
- `src/main/resources/samples/lotes-invalidos/lote-descuadrado.csv`
- `src/main/resources/samples/lotes-invalidos/lote-formato-invalido.txt`

Guia de pruebas manuales:

- `Backend/pruebas-backend.md`

## Notas actuales

- El Core Bancario esta simulado en esta fase.
- La deteccion de cuentas inexistentes usa un stub simple.
- El envio de notificaciones es simulado; correos con formato invalido o que contengan `fail@` quedan en error.
- Los lotes encolados se liberan automaticamente a `VALIDADO` por scheduler interno.
- La compilacion temporal se valido en este entorno con Java 17, pero el proyecto debe ejecutarse con Java 21.
