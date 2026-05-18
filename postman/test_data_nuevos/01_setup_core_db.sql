-- Reset de saldos y estados en el Core para las pruebas.

-- 1. Variables de Empresa (Servicios Integrales Quito 002 S.A.)
SET @RUC_EMPRESA := '1790000002001';
SET @CUENTA_MATRIZ := '0010000000619';

-- 2. Variables de Beneficiarios
-- Ben1: Kevin Perez
SET @BEN1_ID := '1700000001'; SET @BEN1_CTA := '0010000000501';
-- Ben2: Maria Gomez
SET @BEN2_ID := '1700000002'; SET @BEN2_CTA := '0010000000002';
-- Ben3: Carlos Rodrigue
SET @BEN3_ID := '1700000003'; SET @BEN3_CTA := '0010000000004';
-- Ben4: Daniela Vargas
SET @BEN4_ID := '1700000004'; SET @BEN4_CTA := '0010000000504';
-- Ben5: Luis Sanchez (Se usará para la prueba de cuenta bloqueada)
SET @BEN5_ID := '1700000005'; SET @BEN5_CTA := '0010000000005';

-- ==============================================
-- ACTUALIZACIÓN DE SALDOS Y ESTADOS
-- ==============================================

-- Asegurar saldo y cupo de sobregiro para la empresa
UPDATE CUENTA SET ESTADO = 'ACTIVA', SALDO_CONTABLE = 5000.00, SALDO_DISPONIBLE = 5000.00, PERMITE_SOBREGIRO = 1, LIMITE_SOBREGIRO = 1000.00 
WHERE NUMERO_CUENTA = @CUENTA_MATRIZ;

-- Asegurar estados ACTIVOS para los 4 beneficiarios principales
UPDATE CUENTA SET ESTADO = 'ACTIVA', SALDO_CONTABLE = 100.00, SALDO_DISPONIBLE = 100.00 
WHERE NUMERO_CUENTA IN (@BEN1_CTA, @BEN2_CTA, @BEN3_CTA, @BEN4_CTA);

-- Bloquear temporalmente la cuenta del Beneficiario 5 para la prueba de error
UPDATE CUENTA SET ESTADO = 'BLOQUEADA' 
WHERE NUMERO_CUENTA = @BEN5_CTA;

-- Liberar cualquier bloqueo lógico activo que pudiera haber en las tablas de bloqueos
UPDATE BLOQUEO_CUENTA SET ESTADO = 'LIBERADO' WHERE ESTADO = 'ACTIVO' AND CUENTA_ID IN (
  SELECT ID FROM CUENTA WHERE NUMERO_CUENTA IN (@CUENTA_MATRIZ, @BEN1_CTA, @BEN2_CTA, @BEN3_CTA, @BEN4_CTA)
);
