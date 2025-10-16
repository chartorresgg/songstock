Resumen
- Migra el backend a Java 21 (propiedad `java.version` en `pom.xml`).
- Añade soporte de compilación y pruebas con Java 21 en GitHub Actions (`.github/workflows/ci-java21.yml`) y publica la imagen en GHCR.
- Añade dos Dockerfiles:
  - `songstock-backend/Dockerfile` (multi-stage: build con Maven + runtime OpenJDK 21).
  - `songstock-backend/Dockerfile.runtime` (runtime-only, asume jar ya construido).
- Documentación mínima en `README.md`.

Cambios principales
- `songstock-backend/pom.xml`: `java.version` -> 21; `<release>${java.version}</release>` en `maven-compiler-plugin`; consolidación de `spring-boot-maven-plugin` con `mainClass`.
- `.github/workflows/ci-java21.yml`: workflow para build/test con JDK 21 y job para publicar imagen en GHCR.
- `songstock-backend/Dockerfile` y `songstock-backend/Dockerfile.runtime`.
- `README.md`: notas sobre la migración y CI/Docker.

¿Cómo verificar localmente? (QA rápido)
1. Activar JDK 21 en tu sesión (ejemplo PowerShell si instalaste Zulu en C:\Program Files\Zulu\zulu-21):
   ```powershell
   $env:JAVA_HOME='C:\Program Files\Zulu\zulu-21'
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```
2. Compilar y correr tests:
   ```powershell
   cd songstock-backend
   .\mvnw.cmd clean test
   ```
   - Resultado esperado: BUILD SUCCESS (ya pasaron localmente en mi sesión).
3. Construir imagen Docker (opcional):
   - Multi-stage (construye dentro del contenedor):
     ```powershell
     docker build -t songstock-backend:local -f songstock-backend/Dockerfile .
     ```
   - Runtime-only (si ya tienes `target/*.jar`):
     ```powershell
     docker build -t songstock-backend:local -f songstock-backend/Dockerfile.runtime .
     ```
   - Ejecutar:
     ```powershell
     docker run --rm -p 8080:8080 songstock-backend:local
     ```

Notas sobre CI / Publicación en GHCR
- El workflow usa `secrets.GITHUB_TOKEN` para autenticarse en GHCR. Si tu repositorio/organización requiere permisos extra para publicar paquetes, activa “permissions: packages: write” o configura un PAT con permisos `packages:write` y colócalo en `secrets`.
- Si prefieres Docker Hub, puedo actualizar el workflow para usar `DOCKER_USERNAME` y `DOCKER_PASSWORD` (secrets) y publicar ahí en lugar de GHCR.

Checklist de PR (por favor márcalo cuando corresponda)
- [ ] Revisar cambios en `pom.xml` y confirmación de `mainClass` correcto.
- [ ] Ejecutar `mvnw clean test` localmente con JDK 21.
- [ ] Confirmar que CI pasa en la rama (merge si todo OK).
- [ ] Validar despliegue en entorno staging (imagen GHCR o Docker Hub).
- [ ] Actualizar documentación de despliegue si procede.

Rollback / Plan de contingencia
- Si surge un problema tras merge, revertir el PR (GitHub ofrece revert) para volver a la versión Java anterior.
- Alternativamente, cambiar en `pom.xml` `java.version` a 17 y actualizar Dockerfile/workflow según sea necesario.

Etiquetas / Reviewers sugeridos
- Etiqueta: maintenance, java, ci
- Reviewers sugeridos: los desarrolladores backend / responsables DevOps del repo (p. ej. @tu-equipo-backend)
