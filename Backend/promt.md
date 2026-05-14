Estoy construyendo el backend del sistema “Switch de Pagos Masivos Banco BanQuito” en Spring Boot.

Quiero que trabajes únicamente en esta primera fase del proyecto:

1. Crear o ajustar la estructura base del proyecto como monolito modular.
2. Crear los paquetes generales según la arquitectura definida.
3. Asignar las clases de entidad JPA a los módulos correspondientes.
4. Crear únicamente las clases de entidad, enums necesarios y estructura interna de paquetes.
5. No implementar todavía servicios, controllers, repositories avanzados, lógica de negocio, validaciones de negocio, endpoints ni pruebas.

Contexto funcional del sistema:
El Switch de Pagos Masivos permite a empresas cliente cargar archivos de pagos masivos por Banca Web Empresas o por SFTP. El sistema recibe el lote, valida su estructura, procesa pagos línea por línea, calcula comisiones e IVA, liquida contablemente el servicio, notifica beneficiarios y genera reportes de cierre. El procesamiento de esta fase es intrabancario, sincrónico y unitario, es decir, línea por línea. No usar microservicios ni colas externas.

El Core Bancario existe como sistema externo/lógico y es la fuente de verdad de cuentas, saldos y movimientos. En este proyecto del Switch solo se guardan referencias lógicas al Core, por ejemplo números de cuenta, UUIDs de transacciones o identificadores externos. No crear entidades JPA del Core ni relaciones JPA hacia tablas del Core.

Arquitectura modular definida:
El proyecto debe organizarse como un monolito modular por módulos de negocio y soporte. Usar como paquete base:

com.banquito.switchpagos

Crear o respetar esta estructura general:

com.banquito.switchpagos
├── config
├── common
├── catalogo
├── parametro
├── lote
├── archivo
├── procesamiento
├── tarifaje
├── reporte
├── integracioncore
├── auditoria
└── SwitchPagosApplication.java

La división de componentes de negocio del Switch es:

1. API / Entrada de Lotes
   - Se refleja principalmente en el módulo lote.
   - Maneja recepción de lotes, canal de ingreso, estado del lote, horario de corte y programación lógica de lotes encolados.

2. Gestión de Archivos y Validación
   - Se refleja en el módulo archivo.
   - En esta fase no necesariamente tendrá entidades propias, porque la información de archivo está persistida principalmente en LOTE_PAGO y LINEA_PAGO.
   - Crear la estructura del módulo, pero no inventar entidades si no existen en la BD.

3. Motor de Procesamiento de Pagos
   - Se refleja en el módulo procesamiento.
   - Maneja líneas de pago, límites transaccionales e intentos de procesamiento.

4. Motor de Tarifaje y Liquidación Contable
   - Se refleja en el módulo tarifaje.
   - Maneja tarifas, liquidación del servicio y detalle de movimientos contables.

5. Reportes y Notificaciones
   - Se refleja en el módulo reporte.
   - Maneja reportes de cierre y notificaciones a beneficiarios.

6. Integración Core Bancario
   - Se refleja en el módulo integracioncore.
   - Por ahora solo crear estructura del módulo.
   - No crear entidades JPA aquí porque el Core no pertenece a esta base de datos.

Módulos de soporte:

7. catalogo
   - Para catálogos transversales como TIPO_SERVICIO.

8. parametro
   - Para configuración operativa persistida en PARAMETRO_SWITCH.
   - Este módulo no es un componente principal del diagrama, pero sí debe existir en código porque varios módulos consultarán parámetros como IVA, hora de corte, ventana de duplicidad y máximo de reintentos.

9. auditoria
   - Para BITACORA_AUDITORIA_SWITCH.
   - Es un soporte transversal para trazabilidad y auditoría.

Estructura interna sugerida de cada módulo:
Cada módulo puede tener esta estructura interna, pero puedes omitir carpetas que no apliquen todavía o agregar alguna si es estrictamente necesario:

modulo/
├── dto/
│   ├── api/
│   └── internal/
├── model/
├── repository/
├── enums/
└── service/

Importante:
- No usar el nombre de capa entity. Las entidades JPA deben ir en paquetes llamados model.
- No usar mappers.
- Sí usar enums cuando existan campos con valores controlados por CHECK constraints.
- El paquete dto/api será para DTOs expuestos por endpoints hacia frontend u otros consumidores externos.
- El paquete dto/internal será para DTOs de comunicación interna entre módulos o resultados internos de servicios.
- El paquete model contendrá las entidades JPA del módulo.
- El paquete repository contendrá los repositorios JPA del módulo, aunque en esta fase no es obligatorio implementar repositorios si todavía solo se están creando entidades.
- El paquete enums contendrá los enums usados por las entidades y reglas del módulo.
- El paquete service queda reservado para interfaces de servicios del módulo.
- En esta fase no implementar todavía interfaces de servicios ni clases ServiceImpl; solo dejar preparada la carpeta service.
- Puedes ajustar la estructura interna de un módulo si lo ves necesario, pero conserva la división general de módulos indicada.

Asignación de tablas a módulos:
Crear las entidades JPA en los siguientes módulos:

1. catalogo/model
   - TipoServicio para la tabla TIPO_SERVICIO

2. parametro/model
   - ParametroSwitch para la tabla PARAMETRO_SWITCH

3. lote/model
   - LotePago para la tabla LOTE_PAGO
   - HistorialEstadoLote para la tabla HISTORIAL_ESTADO_LOTE
   - ColaProcesamiento para la tabla COLA_PROCESAMIENTO

4. procesamiento/model
   - LineaPago para la tabla LINEA_PAGO
   - LimiteTransaccion para la tabla LIMITE_TRANSACCION
   - IntentoProcesamiento para la tabla INTENTO_PROCESAMIENTO

5. tarifaje/model
   - TarifaServicio para la tabla TARIFA_SERVICIO
   - LiquidacionServicio para la tabla LIQUIDACION_SERVICIO
   - DetalleLiquidacion para la tabla DETALLE_LIQUIDACION

6. reporte/model
   - NotificacionBeneficiario para la tabla NOTIFICACION_BENEFICIARIO
   - ReporteCierre para la tabla REPORTE_CIERRE

7. auditoria/model
   - BitacoraAuditoriaSwitch para la tabla BITACORA_AUDITORIA_SWITCH

8. archivo
   - Crear estructura del módulo, pero no crear entidades JPA si no hay tabla propia.

9. integracioncore
   - Crear estructura del módulo, pero no crear entidades JPA.

Reglas obligatorias para las clases de entidad JPA:

1. Si una tabla tuviera clave primaria compuesta, crear una clase aparte para la PK.
   - Revisar el script completo.
   - Si no hay PK compuesta, no crear clases PK innecesarias.
   - Las restricciones UNIQUE compuestas no son claves primarias compuestas.

2. Nunca usar tipos primitivos como int, long, double, boolean, etc.
   - Usar siempre wrappers: Integer, Long, Boolean, etc.

3. Para dinero o valores monetarios nunca usar float ni double.
   - Usar siempre BigDecimal.

4. No usar Lombok.
   - No usar @Getter, @Setter, @Data, @NoArgsConstructor, @AllArgsConstructor ni similares.
   - Escribir manualmente constructores, getters, setters, equals, hashCode y toString.

5. Generar equals() y hashCode() solo con base en la PK.
   - No incluir todos los atributos.
   - Si la PK es autogenerada, basarse en el identificador de la entidad.
   - Si la PK es natural, como CODIGO en TIPO_SERVICIO o PARAMETRO_SWITCH, basarse en ese campo.

6. Cada entidad debe tener:
   - Constructor vacío.
   - Constructor solo con la PK.
   - No crear constructores con todos los atributos.

7. Sobrescribir el método toString().
   - No incluir relaciones completas en toString() para evitar recursividad o salidas excesivas.
   - Incluir solo campos identificadores y campos descriptivos básicos.

8. Las relaciones entre entidades deben ser preferentemente de hijo a padre.
   - Usar relaciones unidireccionales @ManyToOne desde la entidad hija hacia la entidad padre cuando exista FK interna en la BD.
   - Evitar relaciones bidireccionales innecesarias.
   - No crear colecciones @OneToMany si no son necesarias en esta fase.
   - No mapear relaciones hacia el Core Bancario porque son referencias lógicas, no FKs físicas del schema del Switch.

9. Programar contra interfaces como regla general del proyecto.
   - En esta fase no crear todavía servicios, pero la estructura debe permitir que luego los módulos expongan contratos por interfaces.

10. Usar @Transient solo cuando el atributo no exista en la base de datos.
    - No crear atributos calculados por ahora, salvo que sea estrictamente necesario.
    - No marcar como @Transient columnas reales de la BD.

11. Aplicar reglas de Clean Code:
    - Variables con nombres sustantivos y descriptivos.
    - Métodos con nombres verbales y descriptivos.
    - Usar CamelCase en Java.
    - No usar nombres como x, a, b, y, z.
    - Evitar comentarios innecesarios.
    - Usar comentarios solo para decisiones técnicas complejas.

Reglas de tipos Java:
- BIGINT → Long
- INTEGER → Integer
- VARCHAR / CHAR → String
- NUMERIC(p,s) → BigDecimal
- UUID → java.util.UUID
- TIMESTAMPTZ → java.time.OffsetDateTime
- DATE → java.time.LocalDate
- BOOLEAN → Boolean
- JSONB → preferentemente com.fasterxml.jackson.databind.JsonNode si el proyecto usa Hibernate 6; si no es viable, usar String y dejarlo documentado con un comentario mínimo.
- INET → String

Reglas para enums:
Crear enums para los campos con dominios cerrados definidos por CHECK constraints. Usar @Enumerated(EnumType.STRING).

Enums esperados, ajustando nombres si lo ves necesario:

- EstadoTipoServicio: ACTIVO, INACTIVO
- TipoDatoParametro: NUMERICO, CADENA, FECHA, HORA, BOOLEANO, JSON
- EstadoLimiteTransaccion: ACTIVO, INACTIVO
- EstadoTarifaServicio: ACTIVA, INACTIVA
- CanalIngreso: PORTAL_WEB, SFTP, API
- FormatoArchivo: CSV, TXT
- EstadoLote: RECIBIDO, VALIDANDO, VALIDADO, RECHAZADO, ENCOLADO, PROCESANDO, PROCESADO_PARCIAL, PROCESADO_TOTAL, CERRADO, ANULADO
- EstadoLineaPago: PENDIENTE, VALIDADA, RECHAZADA, ENVIADA_CORE, EXITOSA, FALLIDA, REVERSADA
- EstadoColaProcesamiento: PENDIENTE, TOMADO, PROCESANDO, COMPLETADO, FALLIDO, REINTENTO, CANCELADO
- EstadoIntentoProcesamiento: INICIADO, COMPLETADO, FALLIDO, CANCELADO
- EstadoDebitoLiquidacion: PENDIENTE, COMPLETADO, RECHAZADO, REVERSADO
- ConceptoDetalleLiquidacion: DEBITO_CUENTA_MATRIZ, CREDITO_INGRESOS, CREDITO_IVA, REVERSO
- TipoNotificacion: PAGO_EXITOSO, PAGO_RECHAZADO, PAGO_REVERSADO
- EstadoEnvioNotificacion: PENDIENTE, ENVIADA, ERROR, CANCELADA
- TipoReporte: COMPROBANTE_LIQUIDACION, REPORTE_NOVEDADES
- FormatoReporte: PDF, CSV, XLSX, JSON
- TipoActorAuditoria: EMPRESA, USUARIO_CORE, SISTEMA, API

Reglas de JPA y PostgreSQL:
- El schema de BD es switch_banquito.
- Las tablas y columnas del script están en mayúsculas y entre comillas.
- Configurar el proyecto o las anotaciones JPA para respetar los nombres exactos de tablas y columnas.
- Puedes usar spring.jpa.properties.hibernate.globally_quoted_identifiers=true o comillas explícitas en @Table y @Column, pero debes hacerlo de forma consistente para que funcione con PostgreSQL.
- Usar @Table(schema = "switch_banquito", name = "...") en todas las entidades.
- Usar @Column(name = "...") en todos los atributos persistentes.
- Usar @Id para claves primarias.
- Usar @GeneratedValue(strategy = GenerationType.IDENTITY) para columnas GENERATED ALWAYS AS IDENTITY.
- Usar @Version en los campos VERSION donde existan.
- No crear relaciones JPA para columnas que son referencias lógicas al Core Bancario:
  - ID_CREDENCIAL_WEB_CORE
  - CUENTA_MATRIZ_CARGO
  - CUENTA_DESTINO
  - UUID_DEBITO_CORE
  - UUID_CREDITO_CORE
  - UUID_GRUPO_CORE
  - UUID_TRANSACCION_CORE
  - CUENTA_ORIGEN_CORE
  - CUENTA_DESTINO_CORE

Relaciones internas esperadas según FKs del script:
- LimiteTransaccion → TipoServicio
- TarifaServicio → TipoServicio
- LotePago → TipoServicio
- LineaPago → LotePago
- HistorialEstadoLote → LotePago
- ColaProcesamiento → LotePago
- IntentoProcesamiento → ColaProcesamiento
- LiquidacionServicio → LotePago
- LiquidacionServicio → TarifaServicio
- DetalleLiquidacion → LiquidacionServicio
- NotificacionBeneficiario → LineaPago
- ReporteCierre → LotePago

Todas estas relaciones deben ser unidireccionales desde hijo hacia padre.

No hacer por ahora:
- No crear controllers.
- No crear endpoints.
- No crear lógica de negocio.
- No crear validadores de archivo.
- No crear procesamiento de pagos.
- No crear integración real con Core.
- No crear envío SMTP.
- No crear pruebas.
- No crear mappers.
- No exponer entidades como DTOs.
- No crear servicios ni implementaciones de servicios todavía; solo dejar preparada la carpeta service.
- No implementar repositories todavía, salvo que el proyecto requiera una interfaz mínima vacía para compilar. Si no es necesario, solo dejar preparada la carpeta repository.
- No agregar microservicios.
- No agregar Kafka, RabbitMQ, Redis Streams ni colas externas.
- No cambiar nombres de tablas ni columnas del script.
- No inventar tablas que no estén en el script.

Resultado esperado:
- Proyecto organizado por módulos.
- Estructura interna de módulos creada con dto/api, dto/internal, model, repository, enums y service cuando aplique.
- Entidades JPA completas y compilables.
- Enums creados y usados en las entidades.
- Relaciones JPA internas correctamente mapeadas.
- Referencias al Core tratadas solo como campos simples.
- Código sin Lombok.
- Código sin tipos primitivos.
- Dinero representado con BigDecimal.
- Constructores, getters, setters, equals, hashCode y toString escritos manualmente.
- El proyecto debe compilar.

A continuación te proporcionaré el script completo de la base de datos del Switch. Usa ese script como fuente principal para crear las entidades:

