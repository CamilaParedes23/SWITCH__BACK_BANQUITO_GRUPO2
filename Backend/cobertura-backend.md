# Cobertura funcional del backend

Este documento resume la cobertura funcional actual del backend de `Switch de Pagos Masivos Banco BanQuito` respecto a los requisitos `RF-01` a `RF-08` y a los endpoints ya expuestos.

## Estado general

- El backend ya cubre la mayor parte del flujo funcional de fase 1.
- La base actual permite carga, validacion, procesamiento, liquidacion, reportes y notificaciones simuladas.
- Los principales faltantes ya no son de estructura, sino de integracion real, endurecimiento operativo y pruebas.

## Endpoints implementados

### Lotes

- `POST /api/v1/pagos-masivos/lotes`
  - Carga un lote.
  - Cubre principalmente `RF-01` y parte de `RF-02`.

- `GET /api/v1/pagos-masivos/lotes`
  - Lista lotes por filtros opcionales.
  - Apoya trazabilidad operativa.

- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/estado`
  - Consulta el estado del lote.
  - Apoya `RF-01`, `RF-02`, `RF-04`, `RF-08`.

- `DELETE /api/v1/pagos-masivos/lotes/{uuidLote}`
  - Anula lotes solo en estados permitidos.
  - Apoya control operativo del flujo.

- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/validar`
  - Ejecuta validacion manual de lotes `RECIBIDO`.
  - Apoya `RF-02`.

- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/lineas`
  - Devuelve el detalle de lineas del lote.
  - Apoya `RF-03`, `RF-04`, `RF-08`.

- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/procesar`
  - Procesa el lote linea por linea.
  - Cubre `RF-03` y `RF-04`.

- `POST /api/v1/pagos-masivos/lotes/{uuidLote}/liquidar`
  - Calcula y registra liquidacion del servicio.
  - Cubre `RF-06` y `RF-07`.

- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/novedades`
  - Devuelve novedades por linea.
  - Cubre `RF-04` y `RF-08`.

- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/comprobante`
  - Devuelve comprobante de liquidacion.
  - Cubre `RF-07` y `RF-08`.

- `GET /api/v1/pagos-masivos/lotes/{uuidLote}/notificaciones`
  - Consulta notificaciones generadas para el lote.
  - Apoya `RF-05`.

### Notificaciones

- `POST /api/v1/pagos-masivos/notificaciones/procesar`
  - Ejecuta despacho simulado de notificaciones pendientes.
  - Cubre implementacion actual de `RF-05`.

### Tarifas y parametros

- `GET /api/v1/pagos-masivos/tarifas`
  - Consulta tarifas vigentes.
  - Apoya `RF-06`.

- `GET /api/v1/pagos-masivos/horarios-corte`
  - Consulta parametros operativos de corte.
  - Apoya `RF-01`.

## Matriz de cobertura por requisito

| Requisito | Estado | Cobertura actual | Pendiente principal |
|---|---|---|---|
| `RF-01` Ingesta y horarios de corte | Alto | Carga de lote, `RECIBIDO` o `ENCOLADO`, cola persistida y liberacion automatica | Feriados reales |
| `RF-02` Validacion estructural y prevencion de fraude operativo | Medio/Alto | Layout `CAB/DET/PIE`, sumatorias, duplicidad por nombre y hash | Validacion real de cliente/RUC con servicio activo |
| `RF-03` Procesamiento financiero linea por linea | Alto | Limites, saldo, cuenta destino, titularidad, debito y credito simulados | Integracion real con Core |
| `RF-04` Resiliencia transaccional del lote | Alto | Rechazo por linea, continuidad del lote, procesamiento parcial | Endurecimiento fino de catalogo de errores |
| `RF-05` Notificacion inmediata al beneficiario | Medio | Outbox en BD y procesamiento simulado de pendientes | SMTP real y disparo inmediato real |
| `RF-06` Calculo de tarifaje y comisiones | Alto | Tarifas vigentes, subtotal, IVA desde parametros, total | Validacion funcional fina con negocio real |
| `RF-07` Liquidacion contable de servicios | Medio/Alto | Liquidacion consolidada, detalle, comprobante y movimientos simulados | Integracion contable/Core real |
| `RF-08` Cuadre y reporte de cierre | Alto | Reporte de novedades, comprobante y consultas | Ajustes finales de formato/reporteria |

## Cobertura del flujo principal

### Flujo funcional actual

1. La empresa carga un archivo plano.
2. El backend valida formato, estructura, sumatorias y duplicidad.
3. El lote queda `RECIBIDO`, `ENCOLADO` o `RECHAZADO`.
4. Si el lote esta `ENCOLADO`, un scheduler lo libera automaticamente a `VALIDADO`.
5. Un lote `VALIDADO` puede procesarse linea por linea.
6. El procesamiento puede terminar en `PROCESADO_TOTAL` o `PROCESADO_PARCIAL`.
7. Un lote procesado puede liquidarse y pasar a `CERRADO`.
8. Se generan reportes y notificaciones persistidas.

### Estados implementados del lote

- `RECIBIDO`
- `VALIDADO`
- `RECHAZADO`
- `ENCOLADO`
- `PROCESANDO`
- `PROCESADO_PARCIAL`
- `PROCESADO_TOTAL`
- `CERRADO`
- `ANULADO`

## Pendientes reales para cierre funcional

### Alta prioridad

- Integracion real con Core Bancario.
- Validacion real de cliente/RUC y servicio activo.
- SMTP real para notificaciones.

### Media prioridad

- Calendario real de feriados.
- Reforzar catalogo de errores y mensajes operativos.
- Ajustar formato definitivo de reportes si el frontend lo requiere.

### Baja prioridad tecnica

- Pruebas automatizadas.
- Endurecimiento adicional de logging y monitoreo.
- Optimizaciones menores de limpieza y refactor.

## Conclusiones

- El backend ya cuenta con una base funcional amplia para la fase 1 del proyecto.
- Los requisitos `RF-01` a `RF-08` tienen cobertura mayoritaria, con faltantes localizados principalmente en integraciones reales.
- El siguiente paso recomendado ya no es ampliar estructura, sino validar integraciones externas y preparar cierre de entrega.
