SET search_path TO switch_banquito;

INSERT INTO "PARAMETRO_SWITCH" (
    "CODIGO",
    "NOMBRE",
    "VALOR_TEXTO",
    "TIPO_DATO",
    "DESCRIPCION",
    "ACTUALIZADO_POR"
) VALUES
    ('HORA_CORTE_PROCESO', 'Hora de Corte para Procesamiento Inmediato', '18:00', 'HORA', 'Lotes recibidos antes de esta hora en dia habil se procesan inmediatamente.', 'DOCKER'),
    ('HORA_INICIO_LOTES_ENCOLADOS', 'Hora de Inicio de Lotes Encolados', '00:01', 'HORA', 'Hora de arranque para procesar lotes encolados al siguiente dia habil.', 'DOCKER'),
    ('VENTANA_DUPLICIDAD_DIAS', 'Ventana de Deteccion de Duplicidad', '30', 'NUMERICO', 'Ventana en dias para rechazar archivos duplicados por nombre y hash.', 'DOCKER'),
    ('MAX_REINTENTOS_LOTE', 'Maximo de Reintentos por Lote', '3', 'NUMERICO', 'Numero maximo de reintentos de procesamiento o comunicacion con Core.', 'DOCKER'),
    ('IVA_PORCENTAJE', 'Tasa de IVA Vigente', '0.15', 'NUMERICO', 'IVA vigente aplicado sobre la comision del servicio.', 'DOCKER')
ON CONFLICT ("CODIGO") DO UPDATE
SET "NOMBRE" = EXCLUDED."NOMBRE",
    "VALOR_TEXTO" = EXCLUDED."VALOR_TEXTO",
    "TIPO_DATO" = EXCLUDED."TIPO_DATO",
    "DESCRIPCION" = EXCLUDED."DESCRIPCION",
    "ACTUALIZADO_POR" = EXCLUDED."ACTUALIZADO_POR",
    "FECHA_ACTUALIZACION" = CURRENT_TIMESTAMP;
