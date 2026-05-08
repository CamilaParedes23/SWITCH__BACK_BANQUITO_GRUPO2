# Cambios realizados al modelo del Switch

## Archivo modificado

- `DB/modeloFisicoBD_Switch_v3_postgresql.sql`

## Cambios aplicados

### 1. Columna `version` en todas las tablas
Se agrego la columna:

- `version INTEGER NOT NULL DEFAULT 0`

en todas las tablas del modelo.

#### Por que se hizo
Para soportar control de concurrencia con Optimistic Locking y evitar que dos procesos sobrescriban el mismo registro al mismo tiempo.

## 2. Unificacion del hash en `lote_pago`
Se eliminaron las columnas separadas de hash y se dejo una sola:

- `hash_control`

#### Por que se hizo
Los requisitos funcionales solo necesitan un valor de control del lote para validacion y deteccion de duplicidad. Mantener dos hashes generaba complejidad innecesaria.

## 3. Eliminacion de `tamano_bytes`
Se elimino la columna:

- `tamano_bytes`

#### Por que se hizo
Era un dato tecnico auxiliar que no participa directamente en las reglas funcionales del sistema.

## 4. Ajuste del indice de duplicidad
Se actualizo el indice de duplicidad del lote para usar `hash_control`.

#### Por que se hizo
Para mantener consistente la validacion de archivos duplicados despues de simplificar el modelo.

## 5. Conservacion de `canal_ingreso`
La columna `canal_ingreso` se mantuvo sin cambios.

#### Por que se mantuvo
Porque si forma parte del comportamiento funcional del sistema y permite saber si el lote llego por `PORTAL_WEB` o por `SFTP`.

## Resultado
Los cambios simplifican el modelo, eliminan redundancia y lo dejan mejor alineado con los requisitos funcionales, sin afectar el flujo principal del Switch de Pagos Masivos.
