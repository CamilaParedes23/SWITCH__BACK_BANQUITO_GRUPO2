Guía de Arquitectura: Monolito Modular - Switch de Pagos
1. ¿En qué módulo va cada entidad?
Cada entidad debe vivir en el módulo que sea dueño de esa información. El criterio principal es: ¿Qué módulo administra su ciclo de vida?

Módulo	Entidad	Justificación
lote/	LotePago.java	Aquí se registra, consulta, anula y controla su estado general.
procesamiento/	LineaPago.java	Representa las instrucciones individuales procesadas línea por línea.
tarifaje/	TarifaServicio.java, LiquidacionServicio.java, DetalleLiquidacion.java	Nace después del cálculo de comisiones e IVA.
reporte/	ReporteCierre.java, NotificacionBeneficiario.java	Representa salidas finales generadas para la empresa.
parametro/	ParametroSwitch.java	Es configuración operativa transversal.
2. Acceso a Entidades entre Módulos
Recomendación: No permitir el acceso directo como regla general para evitar el acoplamiento fuerte.

Evitar: Que tarifaje importe el LotePagoRepository de lote.

Mejor: Que tarifaje dependa de LotePagoService.

De este modo, el módulo lote expone solo lo necesario mediante DTOs:

Java
// Ejemplo de método expuesto en el Service
LotePagoResumenDTO obtenerResumenParaLiquidacion(UUID uuidLote);
void marcarComoCerrado(UUID uuidLote);
3. Regla práctica para Entities y Repositories
Los repositories y entities son internos del módulo. Los demás módulos deberían comunicarse mediante Services.

Elemento	¿Uso externo directo?	Recomendación
Controller	No	Solo recibe llamadas externas (API).
Service	Sí	Es la puerta de entrada oficial del módulo.
Repository	No	Debe quedarse encapsulado dentro de su módulo.
Entity	Evitar	Solo si es estrictamente necesario y controlado.
DTO	Sí	Representa datos de intercambio entre módulos o hacia la API.
Enum	Sí	Si es compartido, puede estar en common.
4. Comunicación Interna
La comunicación debe fluir a través de la capa de servicio para proteger las reglas de negocio (validaciones, auditoría, historiales).

Incorrecto: lote.setEstado(EstadoLote.PROCESADO_TOTAL);

Correcto: lotePagoService.cambiarEstado(uuidLote, EstadoLote.PROCESADO_TOTAL, "Motivo");

5. Uso de DTOs Internos vs. API
Es vital diferenciar el propósito de los objetos de datos:

DTOs de API: Diseñados para el contrato con el Frontend (CargaLoteRequestDTO, etc.).

DTOs Internos (de Aplicación): Usados para pasar información entre servicios (ResumenLoteParaLiquidacionDTO).

Regla de oro: No usar entidades JPA como objetos de intercambio principal. Es más limpio pasar un DTO con 5 campos necesarios que una entidad con 30.

6. Estructura de Paquetes Recomendada
Para este proyecto, se sugiere una estructura intermedia que sea clara pero no excesivamente compleja:

Plaintext
procesamiento/
├── service/
│   ├── ProcesamientoPagoService.java (Interfaz)
│   └── ProcesamientoPagoServiceImpl.java (Implementación)
├── dto/
├── entity/
└── repository/
7. Interfaces y Programación por Contratos
Se recomienda usar Interfaz + Implementación para los servicios principales para cumplir con el requisito de "programar contra interfaces".

Ejemplo de contrato:

Java
public interface LotePagoService {
    CargaLoteResponseDTO registrarLote(CargaLoteRequestDTO request);
    EstadoLoteDTO consultarEstado(UUID uuidLote);
    void cambiarEstado(UUID uuidLote, EstadoLoteEnum nuevoEstado, String motivo);
}
Clases que NO necesitan interfaz obligatoriamente:

Utilidades internas (HashArchivoUtil).

Mappers o validadores pequeños y privados del módulo.

8. Resumen de Reglas para el Equipo
Propiedad: Cada entidad pertenece al módulo que administra su ciclo de vida.

Encapsulamiento: Los repositories son privados del módulo.

Puentes: La comunicación entre módulos es solo vía Services.

Contratos: Los servicios principales deben definirse mediante interfaces.

Desacoplamiento: Usar DTOs para mover datos entre módulos; evitar enviar la entidad JPA completa.

Responsabilidad del Controller: Solo coordina llamadas al service; no tiene lógica de negocio ni toca la base de datos.

Persistencia: Las entidades JPA representan datos, no controlan el flujo del negocio.

Conclusión para el desarrollo:
Dentro de un módulo, usa sus entidades y repositories libremente. Para hablar con otro módulo, pide permiso a su Service y recibe un DTO.

1. Si es clave compuesta, crear una clase aparte para la PK
2. NUNCA usar datos primitivos como int, double, etc., sino de tipo wrapper como Integer, Long, etc.
3. Para el dinero NUNCA usar float ni double, SIEMPRE BigDecimal
4. NO usar Lombok
5. Se debe generar los métodos equals() y hashCode() solo para la PK, no para todos los atributos
6. Deben tener el constructor vacío y el constructor con la PK, no con más atributos
7. Es recomendable sobreescribir el método toString()
8. Las relaciones entre entidades son de hijo a padre, evitar relaciones bidireccionales innecesarias
9. Programar contra interfaces (Principio LSP de SOLID)
10. Usar @Transient solo cuando el atributo no exista en la base de datos, ejemplo: edad calculada
11. Tener en cuenta las reglas de CleanCode:
* Nombres:
* Variables → sustantivos
* Métodos → verbos
* Usar nombres descriptivos (NO x, a, b, y, z, etc.)
* Mantener un estándar (CamelCase en Java)
* Comentarios: Evitar comentarios innecesarios. Solo para decisiones complejas