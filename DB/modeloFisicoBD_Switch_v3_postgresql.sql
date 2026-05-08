-- ============================================================================
-- BANCO BANQUITO - SWITCH DE PAGOS MASIVOS
-- Modelo físico relacional en PostgreSQL
-- Versión: 1.0 final base para PowerDesigner
-- Fecha: Abril 2026
--
-- Criterio de diseño:
-- - Base conceptual: switch_pagos_masivos_v3.2.sql.
-- - Alineado con: BancoBanQuito-Corev1.pdf, BancoBanQuito-RequisitosFuncionales-SwitchPagosMasivos.pdf
--   y Guia de Trazabilidad-Requisitos-BD.docx.
-- - Core bancario definitivo: MariaDB. El Switch guarda referencias logicas hacia el Core,
--   sin foreign keys fisicas entre motores distintos.
-- - Nomenclatura: tablas y campos en espanol, sin tildes para compatibilidad SQL.
-- - Motor: PostgreSQL.
-- - Relacionamiento: FK declaradas por ALTER TABLE para facilitar ingenieria inversa en PowerDesigner.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS switch_banquito;
SET search_path TO switch_banquito;

-- ============================================================================
-- 1. CATALOGO: TIPO_SERVICIO
-- ============================================================================
CREATE TABLE tipo_servicio (
    codigo              VARCHAR(10)   NOT NULL,
    nombre              VARCHAR(100)  NOT NULL,
    descripcion          VARCHAR(300),
    estado              VARCHAR(15)   NOT NULL DEFAULT 'ACTIVO',
    version             INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMPTZ,
    CONSTRAINT pk_tipo_servicio PRIMARY KEY (codigo),
    CONSTRAINT chk_tipo_servicio_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
);

COMMENT ON TABLE tipo_servicio IS 'Catalogo maestro de servicios de pagos masivos. Permite que limites, tarifas y lotes dependan de un catalogo formal y no de textos libres. Ejemplos DRF: NOM nomina, PRV proveedores.';
COMMENT ON COLUMN tipo_servicio.codigo IS 'Codigo corto usado en la cabecera del archivo. Ejemplos: NOM, PRV.';

-- ============================================================================
-- 2. PARAMETRO_SWITCH
-- ============================================================================
CREATE TABLE parametro_switch (
    codigo              VARCHAR(50)  NOT NULL,
    nombre              VARCHAR(100) NOT NULL,
    valor_texto         VARCHAR(255) NOT NULL,
    tipo_dato           VARCHAR(15)  NOT NULL,
    descripcion          VARCHAR(500),
    version             INTEGER      NOT NULL DEFAULT 0,
    fecha_actualizacion TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_por     VARCHAR(100),
    CONSTRAINT pk_parametro_switch PRIMARY KEY (codigo),
    CONSTRAINT chk_parametro_switch_tipo CHECK (tipo_dato IN ('NUMERICO','CADENA','FECHA','HORA','BOOLEANO','JSON'))
);

COMMENT ON TABLE parametro_switch IS 'Parametros operativos simples del Switch: IVA vigente, hora de corte, ventana de duplicidad, reintentos maximos. Evita quemar reglas operativas en codigo.';

-- ============================================================================
-- 3. LIMITE_TRANSACCION
-- ============================================================================
CREATE TABLE limite_transaccion (
    id_limite           INTEGER       GENERATED ALWAYS AS IDENTITY,
    tipo_servicio       VARCHAR(10)   NOT NULL,
    monto_minimo        NUMERIC(19,4) NOT NULL DEFAULT 0.01,
    monto_maximo        NUMERIC(19,4) NOT NULL,
    moneda              CHAR(3)       NOT NULL DEFAULT 'USD',
    vigente_desde       DATE          NOT NULL,
    vigente_hasta       DATE,
    estado              VARCHAR(15)   NOT NULL DEFAULT 'ACTIVO',
    version             INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_limite_transaccion PRIMARY KEY (id_limite),
    CONSTRAINT chk_limite_montos CHECK (monto_minimo > 0 AND monto_maximo >= monto_minimo),
    CONSTRAINT chk_limite_vigencia CHECK (vigente_hasta IS NULL OR vigente_hasta >= vigente_desde),
    CONSTRAINT chk_limite_estado CHECK (estado IN ('ACTIVO','INACTIVO'))
);

COMMENT ON TABLE limite_transaccion IS 'Limites minimo y maximo por linea individual y tipo de servicio. Cubre RF-03 del Switch: validar que el monto individual no supere el limite permitido.';

-- ============================================================================
-- 4. TARIFA_SERVICIO
-- ============================================================================
CREATE TABLE tarifa_servicio (
    id_tarifa           INTEGER       GENERATED ALWAYS AS IDENTITY,
    tipo_servicio       VARCHAR(10)   NOT NULL,
    rango_desde         INTEGER       NOT NULL,
    rango_hasta         INTEGER,
    tarifa_unitaria     NUMERIC(10,4) NOT NULL,
    moneda              CHAR(3)       NOT NULL DEFAULT 'USD',
    vigente_desde       DATE          NOT NULL,
    vigente_hasta       DATE,
    estado              VARCHAR(15)   NOT NULL DEFAULT 'ACTIVA',
    version             INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tarifa_servicio PRIMARY KEY (id_tarifa),
    CONSTRAINT chk_tarifa_rango CHECK (rango_desde >= 1 AND (rango_hasta IS NULL OR rango_hasta >= rango_desde)),
    CONSTRAINT chk_tarifa_unitaria CHECK (tarifa_unitaria >= 0),
    CONSTRAINT chk_tarifa_vigencia CHECK (vigente_hasta IS NULL OR vigente_hasta >= vigente_desde),
    CONSTRAINT chk_tarifa_estado CHECK (estado IN ('ACTIVA','INACTIVA'))
);

COMMENT ON TABLE tarifa_servicio IS 'Tarifario escalonado por volumen de transacciones exitosas. Cubre RF-06 y el esquema tarifario comercial del documento Switch.';
COMMENT ON COLUMN tarifa_servicio.rango_hasta IS 'NULL representa rango abierto, por ejemplo 10001 en adelante.';

-- ============================================================================
-- 5. LOTE_PAGO
-- ============================================================================
CREATE TABLE lote_pago (
    id_lote                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    uuid_lote                  UUID          NOT NULL DEFAULT gen_random_uuid(),
    clave_idempotencia         UUID          NOT NULL DEFAULT gen_random_uuid(),
    ruc_empresa                VARCHAR(13)   NOT NULL,
    id_credencial_web_core     INTEGER,
    tipo_servicio              VARCHAR(10)   NOT NULL,
    cuenta_matriz_cargo        VARCHAR(20)   NOT NULL,
    fecha_hora_generacion      TIMESTAMPTZ   NOT NULL,
    total_registros_declarado  INTEGER       NOT NULL,
    monto_total_declarado      NUMERIC(19,4) NOT NULL,
    total_registros_pie        INTEGER,
    monto_total_pie            NUMERIC(19,4),
    total_registros_validados  INTEGER,
    total_registros_rechazados INTEGER,
    monto_total_validado       NUMERIC(19,4),
    nombre_archivo             VARCHAR(255)  NOT NULL,
    hash_control               VARCHAR(128)  NOT NULL,
    formato_archivo            VARCHAR(10)   NOT NULL,
    ruta_almacenamiento        VARCHAR(500),
    canal_ingreso              VARCHAR(15)   NOT NULL,
    estado                     VARCHAR(25)   NOT NULL DEFAULT 'RECIBIDO',
    motivo_rechazo_global      VARCHAR(500),
    fecha_recepcion            TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio_validacion    TIMESTAMPTZ,
    fecha_fin_validacion       TIMESTAMPTZ,
    fecha_inicio_proceso       TIMESTAMPTZ,
    fecha_fin_proceso          TIMESTAMPTZ,
    fecha_cierre               TIMESTAMPTZ,
    version                    INTEGER       NOT NULL DEFAULT 0,
    fecha_actualizacion        TIMESTAMPTZ,
    CONSTRAINT pk_lote_pago PRIMARY KEY (id_lote),
    CONSTRAINT uq_lote_uuid UNIQUE (uuid_lote),
    CONSTRAINT uq_lote_clave_idempotencia UNIQUE (clave_idempotencia),
    CONSTRAINT chk_lote_ruc CHECK (LENGTH(ruc_empresa) = 13),
    CONSTRAINT chk_lote_canal CHECK (canal_ingreso IN ('PORTAL_WEB','SFTP','API')),
    CONSTRAINT chk_lote_formato CHECK (formato_archivo IN ('CSV','TXT')),
    CONSTRAINT chk_lote_estado CHECK (estado IN ('RECIBIDO','VALIDANDO','VALIDADO','RECHAZADO','ENCOLADO','PROCESANDO','PROCESADO_PARCIAL','PROCESADO_TOTAL','CERRADO','ANULADO')),
    CONSTRAINT chk_lote_totales_declarados CHECK (total_registros_declarado > 0 AND monto_total_declarado > 0),
    CONSTRAINT chk_lote_totales_pie CHECK ((total_registros_pie IS NULL OR total_registros_pie >= 0) AND (monto_total_pie IS NULL OR monto_total_pie >= 0)),
    CONSTRAINT chk_lote_totales_validados CHECK (
        (total_registros_validados IS NULL OR total_registros_validados >= 0) AND
        (total_registros_rechazados IS NULL OR total_registros_rechazados >= 0) AND
        (monto_total_validado IS NULL OR monto_total_validado >= 0)
    )
);

COMMENT ON TABLE lote_pago IS 'Representa el archivo/lote completo enviado por la empresa. Incluye datos de cabecera, pie de control, duplicidad, canal, estado y tiempos del proceso. Cubre RF-01, RF-02 y especificacion de archivo del Switch.';
COMMENT ON COLUMN lote_pago.id_credencial_web_core IS 'Referencia logica hacia CORE MariaDB: CREDENCIAL_WEB.ID. No existe FK fisica por estar en motores distintos.';
COMMENT ON COLUMN lote_pago.cuenta_matriz_cargo IS 'Referencia logica hacia CORE MariaDB: CUENTA.NUMERO_CUENTA. Cuenta de donde se debitan pagos y comisiones.';
COMMENT ON COLUMN lote_pago.hash_control IS 'Hash/codigo de seguridad del lote. Se usa junto a nombre_archivo y ruc_empresa para detectar duplicidad en la ventana configurada y validar el control del archivo.';

-- ============================================================================
-- 6. LINEA_PAGO
-- ============================================================================
CREATE TABLE linea_pago (
    id_linea                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    id_lote                     BIGINT        NOT NULL,
    secuencial                  INTEGER       NOT NULL,
    identificacion_beneficiario VARCHAR(20)   NOT NULL,
    nombre_beneficiario         VARCHAR(200)  NOT NULL,
    cuenta_destino              VARCHAR(20)   NOT NULL,
    monto                       NUMERIC(19,4) NOT NULL,
    concepto_referencia         VARCHAR(300),
    correo_notificacion         VARCHAR(200),
    estado                      VARCHAR(25)   NOT NULL DEFAULT 'PENDIENTE',
    codigo_error                VARCHAR(50),
    mensaje_error               VARCHAR(300),
    uuid_operacion_switch       UUID          NOT NULL DEFAULT gen_random_uuid(),
    uuid_debito_core            UUID,
    uuid_credito_core           UUID,
    uuid_grupo_core             UUID,
    fecha_validacion            TIMESTAMPTZ,
    fecha_envio_core            TIMESTAMPTZ,
    fecha_respuesta_core        TIMESTAMPTZ,
    version                     INTEGER       NOT NULL DEFAULT 0,
    fecha_proceso               TIMESTAMPTZ,
    CONSTRAINT pk_linea_pago PRIMARY KEY (id_linea),
    CONSTRAINT uq_linea_secuencial UNIQUE (id_lote, secuencial),
    CONSTRAINT uq_linea_uuid_operacion UNIQUE (uuid_operacion_switch),
    CONSTRAINT chk_linea_monto CHECK (monto > 0),
    CONSTRAINT chk_linea_estado CHECK (estado IN ('PENDIENTE','VALIDADA','RECHAZADA','ENVIADA_CORE','EXITOSA','FALLIDA','REVERSADA'))
);

COMMENT ON TABLE linea_pago IS 'Detalle de cada instruccion de pago del lote. Una linea fallida no aborta el archivo completo: queda RECHAZADA con codigo/mensaje y el lote continua. Cubre RF-03 y RF-04 del Switch.';
COMMENT ON COLUMN linea_pago.uuid_operacion_switch IS 'UUID generado por el Switch para idempotencia de la linea frente al Core. Se envia al Core para evitar doble procesamiento.';
COMMENT ON COLUMN linea_pago.uuid_debito_core IS 'UUID de la transaccion de debito generada en el Core, si aplica.';
COMMENT ON COLUMN linea_pago.uuid_credito_core IS 'UUID de la transaccion de credito generada en el Core, si aplica.';

-- ============================================================================
-- 7. HISTORIAL_ESTADO_LOTE
-- ============================================================================
CREATE TABLE historial_estado_lote (
    id_historial        BIGINT       GENERATED ALWAYS AS IDENTITY,
    id_lote             BIGINT       NOT NULL,
    estado_anterior     VARCHAR(25),
    estado_nuevo        VARCHAR(25)  NOT NULL,
    motivo              VARCHAR(500),
    cambiado_por        VARCHAR(100),
    version             INTEGER      NOT NULL DEFAULT 0,
    fecha_cambio        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_historial_estado_lote PRIMARY KEY (id_historial),
    CONSTRAINT chk_historial_estado_lote CHECK (estado_nuevo IN ('RECIBIDO','VALIDANDO','VALIDADO','RECHAZADO','ENCOLADO','PROCESANDO','PROCESADO_PARCIAL','PROCESADO_TOTAL','CERRADO','ANULADO'))
);

COMMENT ON TABLE historial_estado_lote IS 'Bitacora inmutable de transiciones de estado del lote. Soporta auditoria, trazabilidad y UX del monitor de lotes.';

-- ============================================================================
-- 8. COLA_PROCESAMIENTO
-- ============================================================================
CREATE TABLE cola_procesamiento (
    id_cola                    BIGINT       GENERATED ALWAYS AS IDENTITY,
    id_lote                    BIGINT       NOT NULL,
    fecha_habil_programada     DATE         NOT NULL,
    fecha_encolado             TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_programada_proceso   TIMESTAMPTZ  NOT NULL,
    estado_cola                VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    prioridad                  INTEGER      NOT NULL DEFAULT 5,
    intentos                   INTEGER      NOT NULL DEFAULT 0,
    max_intentos               INTEGER      NOT NULL DEFAULT 3,
    tomado_por                 VARCHAR(100),
    tomado_en                  TIMESTAMPTZ,
    proximo_reintento_en       TIMESTAMPTZ,
    ultimo_error               VARCHAR(500),
    version                    INTEGER      NOT NULL DEFAULT 0,
    fecha_actualizacion        TIMESTAMPTZ,
    CONSTRAINT pk_cola_procesamiento PRIMARY KEY (id_cola),
    CONSTRAINT uq_cola_lote UNIQUE (id_lote),
    CONSTRAINT chk_cola_estado CHECK (estado_cola IN ('PENDIENTE','TOMADO','PROCESANDO','COMPLETADO','FALLIDO','REINTENTO','CANCELADO')),
    CONSTRAINT chk_cola_intentos CHECK (intentos >= 0 AND max_intentos >= 1),
    CONSTRAINT chk_cola_prioridad CHECK (prioridad BETWEEN 1 AND 10)
);

COMMENT ON TABLE cola_procesamiento IS 'Gestiona lotes recibidos fuera de horario, fines de semana o feriados. La fecha habil programada se calcula consultando el Core/FERIADO via API, sin FK fisica.';

-- ============================================================================
-- 9. INTENTO_PROCESAMIENTO
-- ============================================================================
CREATE TABLE intento_procesamiento (
    id_intento         BIGINT       GENERATED ALWAYS AS IDENTITY,
    id_cola            BIGINT       NOT NULL,
    numero_intento     INTEGER      NOT NULL,
    fecha_inicio       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin          TIMESTAMPTZ,
    estado             VARCHAR(20)  NOT NULL,
    codigo_error       VARCHAR(50),
    mensaje_error      VARCHAR(500),
    solicitud_core     JSONB,
    respuesta_core     JSONB,
    version            INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT pk_intento_procesamiento PRIMARY KEY (id_intento),
    CONSTRAINT uq_intento_cola_numero UNIQUE (id_cola, numero_intento),
    CONSTRAINT chk_intento_estado CHECK (estado IN ('INICIADO','COMPLETADO','FALLIDO','CANCELADO')),
    CONSTRAINT chk_intento_numero CHECK (numero_intento >= 1)
);

COMMENT ON TABLE intento_procesamiento IS 'Registra intentos de procesamiento y comunicacion con el Core. Permite reintentos controlados y diagnostico de fallas.';

-- ============================================================================
-- 10. LIQUIDACION_SERVICIO
-- ============================================================================
CREATE TABLE liquidacion_servicio (
    id_liquidacion             BIGINT        GENERATED ALWAYS AS IDENTITY,
    id_lote                    BIGINT        NOT NULL,
    id_tarifa_aplicada         INTEGER       NOT NULL,
    transacciones_exitosas     INTEGER       NOT NULL DEFAULT 0,
    transacciones_fallidas     INTEGER       NOT NULL DEFAULT 0,
    tarifa_unitaria_aplicada   NUMERIC(10,4) NOT NULL,
    iva_porcentaje_aplicado    NUMERIC(5,4)  NOT NULL,
    subtotal_comision          NUMERIC(19,4) NOT NULL,
    monto_iva                  NUMERIC(19,4) NOT NULL,
    total_debitado             NUMERIC(19,4) NOT NULL,
    estado_debito              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    permite_sobregiro          BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_liquidacion          TIMESTAMPTZ,
    version                    INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion             TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_liquidacion_servicio PRIMARY KEY (id_liquidacion),
    CONSTRAINT uq_liquidacion_lote UNIQUE (id_lote),
    CONSTRAINT chk_liquidacion_estado CHECK (estado_debito IN ('PENDIENTE','COMPLETADO','RECHAZADO','REVERSADO')),
    CONSTRAINT chk_liquidacion_cantidades CHECK (transacciones_exitosas >= 0 AND transacciones_fallidas >= 0),
    CONSTRAINT chk_liquidacion_valores CHECK (tarifa_unitaria_aplicada >= 0 AND iva_porcentaje_aplicado >= 0 AND subtotal_comision >= 0 AND monto_iva >= 0 AND total_debitado >= 0)
);

COMMENT ON TABLE liquidacion_servicio IS 'Cabecera de la liquidacion de comision e IVA del lote. Cubre RF-06 y RF-07 del Switch. Guarda snapshot de tarifa e IVA para auditoria historica.';

-- ============================================================================
-- 11. DETALLE_LIQUIDACION
-- ============================================================================
CREATE TABLE detalle_liquidacion (
    id_detalle               BIGINT        GENERATED ALWAYS AS IDENTITY,
    id_liquidacion           BIGINT        NOT NULL,
    concepto                 VARCHAR(30)   NOT NULL,
    monto                    NUMERIC(19,4) NOT NULL,
    uuid_transaccion_core    UUID,
    cuenta_origen_core       VARCHAR(20),
    cuenta_destino_core      VARCHAR(20),
    version                  INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion           TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_detalle_liquidacion PRIMARY KEY (id_detalle),
    CONSTRAINT chk_detalle_concepto CHECK (concepto IN ('DEBITO_CUENTA_MATRIZ','CREDITO_INGRESOS','CREDITO_IVA','REVERSO')),
    CONSTRAINT chk_detalle_monto CHECK (monto >= 0)
);

COMMENT ON TABLE detalle_liquidacion IS 'Detalle de los movimientos contables de la liquidacion: debito a cuenta matriz, credito a ingresos y credito a IVA. Las cuentas son referencias logicas al Core MariaDB.';

-- ============================================================================
-- 12. NOTIFICACION_BENEFICIARIO
-- ============================================================================
CREATE TABLE notificacion_beneficiario (
    id_notificacion          BIGINT       GENERATED ALWAYS AS IDENTITY,
    id_linea                 BIGINT       NOT NULL,
    correo_destino           VARCHAR(200) NOT NULL,
    tipo_notificacion        VARCHAR(25)  NOT NULL DEFAULT 'PAGO_EXITOSO',
    asunto                   VARCHAR(200),
    contenido                JSONB,
    estado_envio             VARCHAR(15)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_envio              TIMESTAMPTZ,
    error_envio              VARCHAR(300),
    reintentos               INTEGER      NOT NULL DEFAULT 0,
    version                  INTEGER      NOT NULL DEFAULT 0,
    proximo_reintento_en     TIMESTAMPTZ,
    CONSTRAINT pk_notificacion_beneficiario PRIMARY KEY (id_notificacion),
    CONSTRAINT chk_notificacion_tipo CHECK (tipo_notificacion IN ('PAGO_EXITOSO','PAGO_RECHAZADO','PAGO_REVERSADO')),
    CONSTRAINT chk_notificacion_estado CHECK (estado_envio IN ('PENDIENTE','ENVIADA','ERROR','CANCELADA')),
    CONSTRAINT chk_notificacion_reintentos CHECK (reintentos >= 0)
);

COMMENT ON TABLE notificacion_beneficiario IS 'Cola/outbox de notificaciones a beneficiarios. Evita depender del SMTP dentro de la transaccion financiera y permite reintentos.';

-- ============================================================================
-- 13. REPORTE_CIERRE
-- ============================================================================
CREATE TABLE reporte_cierre (
    id_reporte             BIGINT       GENERATED ALWAYS AS IDENTITY,
    id_lote                BIGINT       NOT NULL,
    tipo_reporte           VARCHAR(35)  NOT NULL,
    contenido_json         JSONB        NOT NULL,
    nombre_archivo         VARCHAR(255),
    formato_archivo        VARCHAR(10),
    url_archivo            VARCHAR(500),
    hash_reporte           VARCHAR(128),
    version                INTEGER      NOT NULL DEFAULT 0,
    fecha_generacion       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descargado_empresa     BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_descarga         TIMESTAMPTZ,
    CONSTRAINT pk_reporte_cierre PRIMARY KEY (id_reporte),
    CONSTRAINT uq_reporte_lote_tipo UNIQUE (id_lote, tipo_reporte),
    CONSTRAINT chk_reporte_tipo CHECK (tipo_reporte IN ('COMPROBANTE_LIQUIDACION','REPORTE_NOVEDADES')),
    CONSTRAINT chk_reporte_formato CHECK (formato_archivo IS NULL OR formato_archivo IN ('PDF','CSV','XLSX','JSON'))
);

COMMENT ON TABLE reporte_cierre IS 'Reportes finales del lote: comprobante de liquidacion corporativa y reporte de novedades. Cubre RF-08 del Switch.';

-- ============================================================================
-- 14. BITACORA_AUDITORIA_SWITCH
-- ============================================================================
CREATE TABLE bitacora_auditoria_switch (
    id_auditoria           BIGINT       GENERATED ALWAYS AS IDENTITY,
    tipo_actor             VARCHAR(20)  NOT NULL,
    id_actor               VARCHAR(50),
    ruc_empresa            VARCHAR(13),
    accion                 VARCHAR(100) NOT NULL,
    entidad                VARCHAR(80)  NOT NULL,
    id_entidad             VARCHAR(80),
    datos_antes            JSONB,
    datos_despues          JSONB,
    direccion_ip           INET,
    agente_usuario         VARCHAR(300),
    version                INTEGER      NOT NULL DEFAULT 0,
    fecha_creacion         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_bitacora_auditoria_switch PRIMARY KEY (id_auditoria),
    CONSTRAINT chk_bitacora_tipo_actor CHECK (tipo_actor IN ('EMPRESA','USUARIO_CORE','SISTEMA','API'))
);

COMMENT ON TABLE bitacora_auditoria_switch IS 'Bitacora inmutable del Switch. Registra acciones relevantes para trazabilidad, seguridad y auditoria operativa.';

-- ============================================================================
-- INDICES
-- ============================================================================
CREATE INDEX idx_tipo_servicio_estado ON tipo_servicio (estado);
CREATE INDEX idx_limite_tipo_estado ON limite_transaccion (tipo_servicio, estado, vigente_desde, vigente_hasta);
CREATE INDEX idx_tarifa_tipo_rango ON tarifa_servicio (tipo_servicio, estado, rango_desde, rango_hasta);

CREATE INDEX idx_lote_ruc_fecha ON lote_pago (ruc_empresa, fecha_recepcion DESC);
CREATE INDEX idx_lote_estado ON lote_pago (estado);
CREATE INDEX idx_lote_cuenta_matriz ON lote_pago (cuenta_matriz_cargo);
CREATE INDEX idx_lote_duplicidad_archivo ON lote_pago (ruc_empresa, nombre_archivo, hash_control, fecha_recepcion DESC);
CREATE INDEX idx_lote_canal_estado ON lote_pago (canal_ingreso, estado);

CREATE INDEX idx_linea_lote_estado ON linea_pago (id_lote, estado);
CREATE INDEX idx_linea_cuenta_destino ON linea_pago (cuenta_destino);
CREATE INDEX idx_linea_uuid_operacion ON linea_pago (uuid_operacion_switch);
CREATE INDEX idx_linea_uuid_debito_core ON linea_pago (uuid_debito_core);
CREATE INDEX idx_linea_uuid_credito_core ON linea_pago (uuid_credito_core);

CREATE INDEX idx_historial_lote_fecha ON historial_estado_lote (id_lote, fecha_cambio DESC);
CREATE INDEX idx_cola_scheduler ON cola_procesamiento (estado_cola, fecha_programada_proceso, prioridad);
CREATE INDEX idx_intento_cola ON intento_procesamiento (id_cola, numero_intento);

CREATE INDEX idx_liquidacion_lote ON liquidacion_servicio (id_lote);
CREATE INDEX idx_detalle_liquidacion ON detalle_liquidacion (id_liquidacion);
CREATE INDEX idx_detalle_uuid_core ON detalle_liquidacion (uuid_transaccion_core);

CREATE INDEX idx_notificacion_estado ON notificacion_beneficiario (estado_envio, proximo_reintento_en);
CREATE INDEX idx_reporte_lote ON reporte_cierre (id_lote);
CREATE INDEX idx_auditoria_ruc_fecha ON bitacora_auditoria_switch (ruc_empresa, fecha_creacion DESC);
CREATE INDEX idx_auditoria_entidad ON bitacora_auditoria_switch (entidad, id_entidad);

-- ============================================================================
-- RELACIONES - FOREIGN KEYS
-- ============================================================================
ALTER TABLE limite_transaccion
    ADD CONSTRAINT fk_limite_tipo_servicio
    FOREIGN KEY (tipo_servicio) REFERENCES tipo_servicio (codigo)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE tarifa_servicio
    ADD CONSTRAINT fk_tarifa_tipo_servicio
    FOREIGN KEY (tipo_servicio) REFERENCES tipo_servicio (codigo)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE lote_pago
    ADD CONSTRAINT fk_lote_tipo_servicio
    FOREIGN KEY (tipo_servicio) REFERENCES tipo_servicio (codigo)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE linea_pago
    ADD CONSTRAINT fk_linea_lote
    FOREIGN KEY (id_lote) REFERENCES lote_pago (id_lote)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE historial_estado_lote
    ADD CONSTRAINT fk_historial_lote
    FOREIGN KEY (id_lote) REFERENCES lote_pago (id_lote)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cola_procesamiento
    ADD CONSTRAINT fk_cola_lote
    FOREIGN KEY (id_lote) REFERENCES lote_pago (id_lote)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE intento_procesamiento
    ADD CONSTRAINT fk_intento_cola
    FOREIGN KEY (id_cola) REFERENCES cola_procesamiento (id_cola)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE liquidacion_servicio
    ADD CONSTRAINT fk_liquidacion_lote
    FOREIGN KEY (id_lote) REFERENCES lote_pago (id_lote)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE liquidacion_servicio
    ADD CONSTRAINT fk_liquidacion_tarifa
    FOREIGN KEY (id_tarifa_aplicada) REFERENCES tarifa_servicio (id_tarifa)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE detalle_liquidacion
    ADD CONSTRAINT fk_detalle_liquidacion
    FOREIGN KEY (id_liquidacion) REFERENCES liquidacion_servicio (id_liquidacion)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE notificacion_beneficiario
    ADD CONSTRAINT fk_notificacion_linea
    FOREIGN KEY (id_linea) REFERENCES linea_pago (id_linea)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE reporte_cierre
    ADD CONSTRAINT fk_reporte_lote
    FOREIGN KEY (id_lote) REFERENCES lote_pago (id_lote)
    ON UPDATE RESTRICT ON DELETE RESTRICT;

-- ============================================================================
-- DATOS BASE MINIMOS
-- ============================================================================
INSERT INTO tipo_servicio (codigo, nombre, descripcion) VALUES
    ('NOM', 'Pago de Nomina', 'Dispersión masiva de sueldos y beneficios a empleados.'),
    ('PRV', 'Pago a Proveedores', 'Liquidación masiva de obligaciones comerciales a proveedores.');

INSERT INTO parametro_switch (codigo, nombre, valor_texto, tipo_dato, descripcion, actualizado_por) VALUES
    ('IVA_PORCENTAJE', 'Tasa de IVA Vigente', '0.15', 'NUMERICO', 'IVA vigente aplicado sobre la comision del servicio. 0.15 equivale a 15%.', 'SISTEMA'),
    ('HORA_CORTE_PROCESO', 'Hora de Corte para Procesamiento Inmediato', '18:00', 'HORA', 'Lotes recibidos antes de esta hora en dia habil se procesan inmediatamente.', 'SISTEMA'),
    ('HORA_INICIO_LOTES_ENCOLADOS', 'Hora de Inicio de Lotes Encolados', '00:01', 'HORA', 'Hora de arranque para procesar lotes encolados al siguiente dia habil.', 'SISTEMA'),
    ('VENTANA_DUPLICIDAD_DIAS', 'Ventana de Deteccion de Duplicidad', '30', 'NUMERICO', 'Ventana en dias para rechazar archivos duplicados por nombre y hash.', 'SISTEMA'),
    ('MAX_REINTENTOS_LOTE', 'Maximo de Reintentos por Lote', '3', 'NUMERICO', 'Numero maximo de reintentos de procesamiento o comunicacion con Core.', 'SISTEMA');

INSERT INTO limite_transaccion (tipo_servicio, monto_minimo, monto_maximo, moneda, vigente_desde) VALUES
    ('NOM', 0.01, 50000.00, 'USD', CURRENT_DATE),
    ('PRV', 0.01, 100000.00, 'USD', CURRENT_DATE);

INSERT INTO tarifa_servicio (tipo_servicio, rango_desde, rango_hasta, tarifa_unitaria, moneda, vigente_desde) VALUES
    ('NOM', 1, 10, 0.5000, 'USD', CURRENT_DATE),
    ('NOM', 11, 100, 0.4000, 'USD', CURRENT_DATE),
    ('NOM', 101, 500, 0.3000, 'USD', CURRENT_DATE),
    ('NOM', 501, 1000, 0.2000, 'USD', CURRENT_DATE),
    ('NOM', 1001, 10000, 0.1000, 'USD', CURRENT_DATE),
    ('NOM', 10001, NULL, 0.0500, 'USD', CURRENT_DATE),
    ('PRV', 1, 10, 0.5000, 'USD', CURRENT_DATE),
    ('PRV', 11, 100, 0.4000, 'USD', CURRENT_DATE),
    ('PRV', 101, 500, 0.3000, 'USD', CURRENT_DATE),
    ('PRV', 501, 1000, 0.2000, 'USD', CURRENT_DATE),
    ('PRV', 1001, 10000, 0.1000, 'USD', CURRENT_DATE),
    ('PRV', 10001, NULL, 0.0500, 'USD', CURRENT_DATE);

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
