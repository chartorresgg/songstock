# Ruta de documentación e ingeniería

> **Estado:** borrador vivo · **Creado:** 2026-09-28
> Este documento define **qué** hay que hacer, **en qué orden** y **por qué**.
> Cuando una tarea se cumpla, márcala `[x]` y deja el enlace al commit o PR.

---

## 1. Objetivo

Convertir este proyecto académico en un proyecto de portafolio que demuestre **dominio sólido de Java y Spring Boot** y **criterio de ingeniería**, no solo código que funciona.

Cada entregable debe cumplir tres condiciones:

1. **Verificable contra el código.** No se documenta nada que el código no haga.
2. **Justificado.** Cada decisión relevante tiene su porqué por escrito (ADR).
3. **Mantenible.** La documentación vive en el repo y cambia en los mismos PRs que el código.

---

## 2. Diagnóstico inicial (2026-09-28)

### 2.1 Documentación existente

| Artefacto | Estado | Observación |
|---|---|---|
| `README.md` | ⚠️ Inexacto | Rutas `backend/` y `frontend/` (las reales son `songstock-backend/` y `songstock-frontend/`). Ubica `schema.sql` en la raíz (está en `database/`). Dice "Microservicios REST" y es un **monolito por capas**. Logo placeholder, contacto ficticio y badge MIT sin archivo `LICENSE`. |
| `estructura.md` | ❌ Eliminar | Salida de `tree` de ~559 KB, con codificación rota. No es documentación. |
| `enunciado_proyecto.md` | ✅ Útil | Fuente de requisitos y base de la trazabilidad. |
| Postman (`songstock-backend/docs/postman/`) | 🟡 Parcial | 6 colecciones por HU, dispersas y sin environment. |
| OpenAPI | 🟡 Latente | `springdoc` está en el `pom.xml`, pero los ~160 endpoints (16 controllers) no tienen anotaciones ni spec exportado. |
| Base de datos (`database/`) | 🟡 Parcial | `schema.sql` y migraciones con nombre Flyway (`V1__`, `V2__`), pero **Flyway no está en el pom**. Sin diagrama ER ni diccionario de datos. |
| `docker-compose.yml` | ❌ Vacío | El README lo presenta como si funcionara. |
| Tests | ❌ Inexistentes | Solo el test por defecto (`SongstockBackendApplicationTests`). |

### 2.2 Hallazgos técnicos verificados

| # | Hallazgo | Evidencia | Severidad |
|---|---|---|---|
| H1 | Contraseña de MySQL y secreto JWT en texto plano, versionados | `application.properties` aparece en 7 de 38 commits, desde `bf5f905` | 🔴 Crítica |
| H2 | Controllers que importan repositorios (se saltan la capa de servicio) | `AuthController`, `OrderController`, `ProductController`, `NotificationController` | 🟠 Media |
| H3 | El perfil `dev` sobrescribe `ddl-auto=validate` con `update`: Hibernate **modifica el esquema**, al contrario de lo que dice el comentario en `application.properties` | `application-dev.yml` | 🟠 Media |
| H4 | Logging configurado para un paquete que no existe (`com.vinylstore`) | `application-dev.yml` | 🟢 Baja |
| H5 | `application-prod.yml` vacío: no hay configuración de producción | — | 🟠 Media |
| H6 | Controllers de depuración expuestos | `DebugController`, `SimpleTestController`, `CorsController` | 🟠 Media |
| H7 | `ApiResponse` duplicado | `dto/ApiResponse.java` y `util/ApiResponse.java` | 🟢 Baja |
| H8 | `*.tsbuildinfo` versionados (artefactos de build) | `songstock-frontend/` | 🟢 Baja |

### 2.3 Hipótesis pendiente de verificar

> **HP-1: reglas de autorización que nunca coinciden.**
> `server.servlet.context-path=/api/v1`, y Spring Security compara la ruta **sin** el context-path.
> En `WebSecurityConfig`, las reglas de `artists`, `genres` y `albums` solo existen con el prefijo `/api/v1/...`. Las de `products`, `users` y `providers` están duplicadas con y sin prefijo.
> **Si la hipótesis es cierta:** esas rutas caen en `anyRequest().authenticated()`. El GET "público" exigiría login y **cualquier usuario autenticado (incluido CUSTOMER) podría hacer DELETE**, salvo que haya `@PreAuthorize` en el controller.
> **Cómo verificarla:** un test con `@WebMvcTest` + `@WithMockUser(roles = "CUSTOMER")` sobre `DELETE /artists/{id}`.
> **Resultado:** _pendiente_

---

## 3. Decisiones tomadas

| Decisión | Razonamiento |
|---|---|
| **Mantener el monolito.** No migrar a microservicios. | No hay requisitos medibles de escala ni varios equipos. Los microservicios resuelven sobre todo un problema organizacional y agregan red, consistencia eventual y sagas. El backend es **stateless** (JWT + `SessionCreationPolicy.STATELESS`), así que ya escala horizontalmente. El cuello de botella sería MySQL, no la arquitectura. |
| **Posible evolución a monolito modular** (package-by-feature + Spring Modulith) | Da límites claros entre dominios sin el costo de los sistemas distribuidos. **Condición previa:** tener tests. Se registrará como ADR. |
| **La documentación vive en `/docs` (docs-as-code).** La Wiki funciona como portal. | La Wiki es un repo aparte: no se revisa en PRs ni se versiona con el código. Los diagramas van en **Mermaid**, que GitHub muestra de forma nativa. |
| **Renombrar la aplicación** | "SongStock" es el nombre de la actividad académica. El objetivo es un concepto propio, orientado a demostrar dominio de Java/Spring Boot. |
| **Primero cambiar los secretos, después limpiar el historial** | Limpiar el historial es higiene, no seguridad: un secreto que ya se publicó se considera comprometido. |
| **Roadmap como documento** | Da una ruta clara. Si crece, las tareas pueden pasar a GitHub Issues/Milestones. |

### Decisiones abiertas

- [ ] **Nuevo nombre y concepto del producto.** Bloquea el renombrado y parte de la documentación.
- [ ] **Estrategia de historial:** reescribir con `git filter-repo` (conserva los commits, cambian los hashes) o empezar un repo nuevo sin historial.
- [ ] **¿Publicar la documentación con MkDocs Material + GitHub Pages?**
- [ ] **Usuario de BD de la aplicación:** ¿con qué privilegios? Depende de H3 (`ddl-auto`) y de adoptar Flyway.

---

## 4. Fases

> El orden importa: cada fase se apoya en la anterior.

### Fase 0: Higiene y seguridad
_Por qué primero: hoy estos puntos le restan valor al proyecto frente a cualquier revisor._

- [ ] Cambiar la contraseña de MySQL y el secreto JWT (H1)
- [ ] Externalizar la configuración con variables de entorno + `application.properties.example`
- [ ] Limpiar el historial (`git filter-repo`) según la decisión abierta
- [ ] Verificar la hipótesis HP-1 y corregirla si aplica
- [ ] Revisar H3: ¿`validate` o `update` en dev? ¿Adoptar Flyway?
- [ ] Eliminar los controllers de depuración o restringirlos a un perfil (H6)
- [ ] Agregar `LICENSE`
- [ ] Eliminar `estructura.md`
- [ ] Agregar `*.tsbuildinfo` al `.gitignore` (H8)

### Fase 1: Renombrado e identidad
- [ ] Definir el nombre y el concepto del producto
- [ ] Renombrar el repo en GitHub (*Settings → Rename*: conserva historial, issues y redirecciones)
- [ ] Renombrar en el código: paquete `com.songstock`, `artifactId`, nombre de BD, textos del frontend
- [ ] Reescribir el README (ver §6)

### Fase 2: Base de la documentación
- [ ] Estructura `/docs` e índice (`docs/README.md`)
- [ ] C4: Contexto y Contenedores
- [ ] Modelo de dominio (diagrama de clases, solo el dominio)
- [ ] Diagrama ER + diccionario de datos
- [ ] ADR-0001: Monolito por capas

### Fase 3: API
_Por qué aquí: es lo más valorado en un rol backend._

- [ ] Anotaciones OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`, `@SecurityScheme` bearer JWT)
- [ ] Exportar `docs/04-api/openapi.yaml`
- [ ] Convenciones: formato de respuesta, errores, códigos HTTP, paginación
- [ ] Matriz de roles y permisos (ADMIN / PROVIDER / CUSTOMER × endpoint)
- [ ] Unificar las colecciones de Postman y agregar un environment

### Fase 4: Comportamiento y decisiones
- [ ] Diagramas de secuencia (ver §5)
- [ ] Diagramas de estado: `OrderStatus`, `OrderItemStatus`
- [ ] ADRs: JWT stateless, `OrderItem` con estado por proveedor, `open-in-view=false`, DTOs + MapStruct
- [ ] Historias de usuario con criterios de aceptación
- [ ] Matriz de trazabilidad: requisito → HU → endpoint → test

### Fase 5: Calidad y operación
_Por qué: sin tests, la documentación son solo promesas._

- [ ] Tests unitarios de los servicios
- [ ] `@WebMvcTest` (incluye tests de autorización) y `@DataJpaTest`
- [ ] Tests de integración con Testcontainers (ya está en el pom)
- [ ] CI con GitHub Actions + badges de build y cobertura (JaCoCo)
- [ ] `docker-compose.yml` funcional + diagrama de despliegue
- [ ] Documentar configuración y entornos, y estrategia de pruebas
- [ ] `SECURITY.md` + modelo de amenazas breve (OWASP Top 10)

### Fase 6: Publicación
- [ ] Wiki como portal (onboarding, glosario, FAQ → enlaces a `/docs`)
- [ ] MkDocs + GitHub Pages (opcional)
- [ ] README final con capturas o GIF, decisiones destacadas y aprendizajes
- [ ] `CHANGELOG.md` + primer release con SemVer

### Evolución (después de la Fase 5)
- [ ] Evaluar la migración a monolito modular (Spring Modulith) con ADR
- [ ] Implementar el envío de correos que pide el enunciado (hoy solo hay notificaciones internas)

---

## 5. Diagramas: qué y cómo

| Diagrama | Contenido | Herramienta | Regla |
|---|---|---|---|
| **Arquitectura (C4)** | Contexto: actores (Cliente, Proveedor, Admin) y sistemas externos. Contenedores: SPA React, API Spring Boot, MySQL. Componentes: capas + filtro JWT. | Mermaid / C4-PlantUML | Nombrar la arquitectura como realmente es. |
| **Despliegue** | Navegador, servidor estático, JVM con el `.jar`, MySQL, puertos, perfiles | PlantUML (UML deployment) | Dibujar solo lo que existe; nada de infraestructura hipotética. |
| **Clases** | Solo el dominio: `User`, `Provider`, `Product`, `Album`, `Song`, `Order`, `OrderItem`, `OrderReview`, `Compilation` + enums | Mermaid `classDiagram` | No incluir las ~150 clases del proyecto. |
| **Secuencia** | Login JWT · checkout multi-proveedor · aceptar/rechazar ítem · registrar envío → notificación · valoración | Mermaid `sequenceDiagram` | Incluir `AuthTokenFilter` y los límites de `@Transactional`. |
| **Estados** | Ciclo de vida de `Order` y `OrderItem` | Mermaid `stateDiagram-v2` | Cada transición indica quién la dispara. |
| **ER** | Tablas, claves y cardinalidades | SchemaSpy / MySQL Workbench | Generar desde `schema.sql` y completar a mano con las reglas de negocio. |

---

## 6. Estructura objetivo

> Se crea **por fases** y solo con contenido real. Las carpetas vacías y los archivos "TODO" restan valor.

```
/
├── README.md                 ← presentación: concepto, capturas, quickstart, enlaces a /docs
├── LICENSE
├── CONTRIBUTING.md
├── SECURITY.md
├── CHANGELOG.md
├── .github/
│   ├── ISSUE_TEMPLATE/
│   ├── pull_request_template.md
│   └── workflows/ci.yml
└── docs/
    ├── README.md             ← índice
    ├── ROADMAP.md            ← este documento
    ├── 01-requirements/      ← enunciado, historias de usuario, trazabilidad
    ├── 02-architecture/      ← C4, atributos de calidad, adr/
    ├── 03-design/            ← modelo de dominio, secuencias, estados
    ├── 04-api/               ← openapi.yaml, convenciones, auth, postman/
    ├── 05-database/          ← ER, diccionario de datos, migraciones
    ├── 06-deployment/        ← diagrama de despliegue, setup local, configuración, docker
    ├── 07-quality/           ← estrategia de pruebas, seguridad
    └── 08-operations/        ← observabilidad (Actuator, logs), runbook
```

**README de portafolio:** qué problema resuelve · capturas o GIF de los tres roles · stack · quickstart que funcione tal cual · decisiones técnicas destacadas (enlaces a los ADRs) · qué aprendí y próximos pasos · demo desplegada si es posible.

---

## 7. Principios de trabajo

1. **Evidencia antes que opinión.** Cada afirmación de la documentación se puede comprobar en el código o en un test.
2. **Decisiones con porqué.** Si no puedes defender una decisión en una entrevista, todavía no está tomada.
3. **Nada de sobreingeniería.** La complejidad se justifica con requisitos, no con "por si acaso".
4. **La documentación cambia en el mismo PR que el código.**
5. **Commits convencionales** (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`). Hay que corregir el README, que hoy describe otra convención.
