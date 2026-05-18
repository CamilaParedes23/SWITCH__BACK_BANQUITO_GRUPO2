-- Script para limpiar/reiniciar la base de datos del Switch.
-- Ejecutar en PostgreSQL (esquema switch_banquito).

SET search_path TO switch_banquito;

-- Borrar datos en orden de dependencias para evitar errores de Foreign Key
DELETE FROM "REPORTE_CIERRE";
DELETE FROM "NOTIFICACION_BENEFICIARIO";
DELETE FROM "DETALLE_LIQUIDACION";
DELETE FROM "LIQUIDACION_SERVICIO";
DELETE FROM "INTENTO_PROCESAMIENTO";
DELETE FROM "COLA_PROCESAMIENTO";
DELETE FROM "HISTORIAL_ESTADO_LOTE";
DELETE FROM "LINEA_PAGO";
DELETE FROM "LOTE_PAGO";
DELETE FROM "BITACORA_AUDITORIA_SWITCH";

-- Reiniciar secuencias si es necesario (PostgreSQL 10+ IDENTITY)
-- ALTER SEQUENCE switch_banquito."LOTE_PAGO_ID_LOTE_seq" RESTART WITH 1;
-- etc. (Esto es opcional, el DELETE es suficiente para probar de nuevo).
