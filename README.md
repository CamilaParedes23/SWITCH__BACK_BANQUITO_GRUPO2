# Switch de Pagos Masivos Banco BanQuito

Backend Spring Boot organizado como monolito modular para el Switch de Pagos Masivos.

## Flujo funcional principal

1. `POST /api/v1/pagos-masivos/lotes`
2. `POST /api/v1/pagos-masivos/lotes/{uuidLote}/validar`
3. `POST /api/v1/pagos-masivos/lotes/{uuidLote}/procesar`
4. `POST /api/v1/pagos-masivos/lotes/{uuidLote}/liquidar`
5. `GET /api/v1/pagos-masivos/lotes/{uuidLote}/novedades?formato=JSON`
6. `GET /api/v1/pagos-masivos/lotes/{uuidLote}/comprobante?formato=JSON`

## Formato simple de archivo CSV/TXT

```text
H,1790000001001,NOM,2026-04-20T10:30:00-05:00,0010000000001,2,1500.00
D,1,1700000002,Juan Perez,0010000000002,1000.00,Sueldo Abril,juan@correo.com
D,2,1700000003,Maria Lopez,0010000000003,500.00,Sueldo Abril,maria@correo.com
T,ABC123HASH,2,1500.00
```

## Integraciones actuales

- Core Bancario: usa REST real con `core.base-url`.
- Si se carga por portal web, puede enviarse `usernameCredencialWebCore=empresa001` para validar la credencial empresarial contra el endpoint oficial del Core.
- SMTP: simulado; las notificaciones pendientes se marcan como `ENVIADA` si el correo tiene formato basico valido, o `ERROR` si no.
- Reportes PDF/CSV/XLSX: en esta fase se persiste contenido JSON estructurado con el formato solicitado como metadato; no se genera un archivo binario real.
