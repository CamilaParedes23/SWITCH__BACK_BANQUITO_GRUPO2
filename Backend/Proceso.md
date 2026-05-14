Fase 0: Contexto general y reglas del proyecto
Antes de pedir código, el prompt debe fijar el marco de trabajo. Aquí se le dice a Codex qué sistema está construyendo, qué arquitectura debe respetar y qué restricciones no debe romper.

Debe incluir:

Nombre del sistema: Switch de Pagos Masivos Banco BanQuito.

Tipo de arquitectura: monolito modular en Spring Boot.

No usar microservicios.

No usar colas externas como RabbitMQ, Kafka, etc.

No adelantarse a arquitectura asíncrona distribuida.

Usar PostgreSQL.

Respetar el script físico de base de datos.

Separar el proyecto por módulos funcionales.

Programar contra interfaces.

No usar Lombok, si esa regla del profesor sigue vigente.

Evitar tipos primitivos si el profesor lo pidió.

Usar entidades JPA alineadas al modelo físico.

Mantener nombres en español si así está el script.

No exponer todos los campos de BD en los DTO.

Esta fase no genera código todavía; sirve para que Codex entienda las reglas base.

Fase 1: Crear estructura base del proyecto
Aquí el objetivo sería que Codex genere solo la estructura inicial del proyecto Spring Boot.

Debe crear paquetes como:

com.banquito.switchpagos

├── config

├── common

├── parametro

├── lote

├── archivo

├── procesamiento

├── tarifaje

├── reporte

├── integracioncore

└── SwitchPagosApplication.java

También debería crear:

configuración de datasource,

configuración JPA,

manejo global de errores,

estructura común de respuestas,

excepciones base,

enums generales,

clase principal de Spring Boot.

En esta fase no pediría todavía toda la lógica del negocio. Solo dejaría el proyecto compilando.
Resultado esperado de la fase: Proyecto Spring Boot creado, con estructura modular limpia y configuración inicial funcional.

Fase 2: Generar entidades JPA desde el script de BD
Esta fase debe enfocarse solo en mapear el modelo físico a entidades. Como el script tiene más de 500 líneas, este prompt debe ser muy preciso.

Debe pedir:

crear entidades para las tablas del schema switch_banquito,

respetar nombres de tablas y columnas,

usar BigDecimal para montos,

usar UUID para UUIDs,

usar OffsetDateTime para TIMESTAMPTZ,

usar LocalDate para DATE,

usar Integer, Long, Boolean, no tipos primitivos,

mapear relaciones JPA según las FK,

usar @Version en los campos version,

usar enums para estados, canales, formatos, tipos de reporte, etc.,

no usar Lombok,

incluir constructores vacíos, getters y setters manuales si aplica,

no crear lógica de negocio dentro de las entidades.

También debe aclarar que las referencias al Core son referencias lógicas, no relaciones JPA. Por ejemplo: cuenta_matriz_cargo, cuenta_destino, uuid_debito_core, uuid_credito_core, id_credencial_web_core no deben mapearse como entidades del Core.

Resultado esperado: Entidades JPA completas, compilables y alineadas con el script de BD.

Fase 3: Repositories y consultas necesarias
Después de las entidades, se puede pedir que cree los repositories. Aquí no basta con JpaRepository vacío. Se deben pedir métodos útiles para los RF.

Ejemplos:

Para lotes: buscar por uuidLote, listar por RUC, estado y fecha, detectar duplicidad por RUC, nombre de archivo, hash y ventana de días, consultar estado.

Para líneas: buscar líneas por lote, contar exitosas, contar fallidas, buscar por estado, buscar por secuencial.

Para tarifas: buscar tarifa vigente por tipo de servicio y cantidad de exitosas.

Para parámetros: buscar por código.

Para reportes: buscar por lote y tipo.

Resultado esperado: Repositories creados con consultas derivadas o @Query donde sea necesario.

Fase 4: DTOs y contratos API
Esta fase debe crear los DTOs que van a ver frontend y otros consumidores. Debe basarse en el documento de contratos API, no directamente en las entidades.

Debe incluir DTOs para:

carga de lote, respuesta de carga, estado de lote, líneas del lote, validación de lote, procesamiento de lote, liquidación, tarifas, horarios de corte, comprobante, novedades, error estándar.

También debe indicarse expresamente:

No devolver entidades JPA directamente desde los controllers.

Resultado esperado: DTOs limpios, separados de entidades y alineados con los endpoints oficiales.

Fase 5: Servicios base por módulo
Aquí se empiezan a crear interfaces y clases de servicio.

Debe respetar la división:

lote, archivo, procesamiento, tarifaje, reporte, integracioncore, parametro.

Servicios esperados:

LotePagoService, ParametroSwitchService, ArchivoPagoService, ValidadorArchivoPagoService, ProcesamientoPagoService, TarifajeService, LiquidacionContableService, ReporteLoteService, NotificacionService, CoreBancarioService.

En esta fase puede hacerse lógica parcial, pero todavía no conviene pedir toda la lógica compleja.
Resultado esperado: Servicios creados con interfaces, implementaciones y dependencias bien organizadas.

Fase 6: Implementar flujo de carga y validación de lotes
Esta fase ya implementa RF-01 y RF-02.

Debe pedir:

endpoint para cargar lote,

registro del lote en BD,

validación de horario de corte,

estado RECIBIDO o ENCOLADO,

parseo básico de archivo CSV/TXT,

validación de cabecera, detalle y pie,

validación de sumatorias,

validación de RUC con integración simulada al Core o cliente mock,

validación de duplicidad por nombre de archivo y hash,

actualización de estados del lote,

registro en historial de estados.

Resultado esperado: El sistema puede recibir un archivo, guardar el lote, validar estructura y rechazarlo si incumple reglas globales.

Fase 7: Implementar procesamiento línea por línea
Esta fase cubre RF-03 y RF-04.

Debe pedir:

recorrer líneas del lote secuencialmente,

validar monto mínimo y máximo,

consultar saldo disponible en Core,

validar cuenta destino en Core,

generar UUID de operación por línea,

solicitar débito y crédito al Core,

marcar línea como EXITOSA o RECHAZADA,

registrar código y mensaje de error,

continuar aunque una línea falle,

actualizar contadores del lote.

Aquí conviene decirle a Codex que el Core todavía puede estar simulado con un cliente interno o stub, si el Core real no está disponible.
Resultado esperado: El lote se procesa línea por línea, con éxito parcial permitido.

Fase 8: Implementar tarifaje y liquidación contable
Esta fase cubre RF-06 y RF-07.

Debe pedir:

contar líneas exitosas,

buscar tarifa vigente,

calcular subtotal,

calcular IVA desde parametro_switch,

calcular total,

guardar liquidacion_servicio,

crear detalle_liquidacion,

solicitar al Core: débito global a cuenta matriz, crédito a ingresos, crédito a IVA.

permitir sobregiro en comisión, según requisito,

cambiar estado del lote según resultado.

Resultado esperado: El sistema calcula y registra correctamente comisión, IVA y movimientos contables.

Fase 9: Reportes, novedades y notificaciones
Esta fase cubre RF-05 y RF-08.

Debe pedir:

generar comprobante de liquidación corporativa,

generar reporte de novedades por línea,

guardar reportes en reporte_cierre,

exponer endpoints de consulta,

registrar notificaciones a beneficiarios exitosos,

enviar correos mediante SMTP o dejar un stub si todavía no configuran SMTP,

no afectar la transacción financiera si falla el correo.

Resultado esperado: El frontend puede consultar comprobantes, novedades y estado final del lote.

Fase 10: Controllers y endpoints finales
Revisión de que todos los endpoints estén alineados.

Endpoints oficiales:

POST   /api/v1/pagos-masivos/lotes

GET    /api/v1/pagos-masivos/lotes

GET    /api/v1/pagos-masivos/lotes/{uuid}/estado

DELETE /api/v1/pagos-masivos/lotes/{uuid}

POST   /api/v1/pagos-masivos/lotes/{uuid}/validar

GET    /api/v1/pagos-masivos/lotes/{uuid}/lineas

POST   /api/v1/pagos-masivos/lotes/{uuid}/procesar

POST   /api/v1/pagos-masivos/lotes/{uuid}/liquidar

GET    /api/v1/pagos-masivos/tarifas

GET    /api/v1/pagos-masivos/horarios-corte

GET    /api/v1/pagos-masivos/lotes/{uuid}/novedades

GET    /api/v1/pagos-masivos/lotes/{uuid}/comprobante

Resultado esperado: API completa y coherente con el contrato definido para frontend.

Fase 11: Pruebas y datos de ejemplo
Esta fase es clave para reducir ajustes posteriores.

Debe pedir:

pruebas unitarias de validadores, pruebas de tarifaje, pruebas de duplicidad, pruebas de procesamiento con líneas exitosas y rechazadas, pruebas de controllers.

archivos CSV/TXT de ejemplo, casos de lote válido, caso de lote descuadrado, caso de línea con cuenta inválida, caso de comisión con IVA.

Resultado esperado: Primera versión verificable y no solo código “aparentemente completo”.

Fase 12: Revisión final de consistencia
Última fase antes de entregar al grupo.

Debe pedirle a Codex:

revisar que compile,

revisar que no haya endpoints duplicados,

revisar que no se expongan entidades directamente,

revisar que no se hayan creado microservicios,

revisar que no se usen colas externas,

revisar que los paquetes respeten la arquitectura modular,

revisar que los RF-01 a RF-08 estén cubiertos,

generar un README de ejecución.

Resultado esperado: Proyecto listo para que el equipo empiece ajustes menores.