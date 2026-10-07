# 🎵 SongStock - Marketplace de Vinilos y Música Digital

<div align="center">

**Marketplace moderno para coleccionistas de vinilos y amantes de la música digital**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue?logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue?logo=typescript)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)

[Características](#-características) •
[Tecnologías](#️-stack-tecnológico) •
[Instalación](#-instalación) •
[API Docs](#-documentación-api) •
[Contribuir](#-contribuir)

</div>

---

## 📖 Descripción

**SongStock** es una plataforma web fullstack que conecta coleccionistas de vinilos con compradores, permitiendo además la venta de música en formato digital. El sistema ofrece:

- 🎧 **Catálogo dual**: Vinilos físicos y álbumes digitales MP3
- 🔍 **Búsqueda avanzada**: Filtros por género, artista, año, precio y condición
- 📦 **Gestión de órdenes**: Sistema completo de pedidos con múltiples proveedores
- ⭐ **Sistema de reviews**: Valoración de transacciones post-entrega
- 🎼 **Recopilaciones**: Playlists personalizadas públicas/privadas
- 👥 **Roles diferenciados**: Administradores, proveedores y clientes

---

## ✨ Características

### 👤 Para Compradores
- Explorar catálogo de vinilos y música digital
- Ver formatos alternativos del mismo álbum (digital ↔ vinilo)
- Crear recopilaciones de canciones favoritas
- Buscar recopilaciones públicas de otros usuarios
- Carrito de compras con checkout completo
- Historial de órdenes y valoraciones

### 🏪 Para Proveedores
- Gestionar catálogo de productos (vinilos y digitales)
- Definir precio, inventario y condición (nuevo/usado)
- Recibir notificaciones de nuevos pedidos
- Confirmar/rechazar órdenes con motivo
- Registrar envíos con fecha estimada
- Dashboard con métricas de ventas

### 🔐 Para Administradores
- Gestión de usuarios y proveedores
- Sistema de invitaciones para nuevos proveedores
- Panel de estadísticas generales
- Gestión de catálogo maestro (géneros, artistas, álbumes)

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.2.x
- **Lenguaje**: Java 17
- **Base de Datos**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Seguridad**: Spring Security + JWT
- **Validación**: Bean Validation (JSR-380)
- **Build**: Maven (incluye wrapper `mvnw`)

### Frontend
- **Framework**: React 19
- **Lenguaje**: TypeScript 5.9
- **Build Tool**: Vite 7
- **Routing**: React Router 7
- **State Management**: Context API
- **Estilos**: Tailwind CSS 3
- **Iconos**: Lucide React
- **HTTP Client**: Axios

### Herramientas
- **API Docs**: Swagger UI / OpenAPI 3 (springdoc), documentación parcial
- **Pruebas manuales de API**: colecciones de Postman en `songstock-backend/docs/postman/`
- **Control de Versiones**: Git

---

## 🚀 Instalación

### Prerrequisitos
```bash
# Backend
- Java 17 o superior
- Maven 3.9+
- MySQL 8.0+

# Frontend
- Node.js 18+
- npm 9+ o yarn
```

### 1️⃣ Clonar el Repositorio
```bash
git clone https://github.com/chartorresgg/songstock.git
cd songstock
```

### 2️⃣ Configurar Base de Datos
```bash
# Crear la base de datos song_stock y sus tablas
mysql -u root -p < database/schema.sql
```

> `database/initial-data.sql` y las migraciones de `database/migrations/` están vacíos por ahora.

### 3️⃣ Configurar Backend

Ajusta `songstock-backend/src/main/resources/application.properties` con tus credenciales:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/song_stock
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# JWT
jwt.secret=tu_clave_secreta_muy_larga_y_segura
jwt.expiration=86400000

# Servidor
server.port=8080
server.servlet.context-path=/api/v1
```

**Ejecutar Backend**
```bash
cd songstock-backend
./mvnw spring-boot:run      # En Windows: mvnw.cmd spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080/api/v1`

### 4️⃣ Configurar Frontend

**Instalar dependencias**
```bash
cd songstock-frontend
npm install
```

La URL del backend está fijada en `src/config/api.config.ts` (`http://localhost:8080/api/v1`). Por ahora no se lee de variables de entorno.

**Ejecutar Frontend**
```bash
npm run dev
```

La aplicación estará disponible en: `http://localhost:3000`

---

## 📂 Estructura del Proyecto

```
songstock/
├── songstock-backend/
│   ├── src/main/java/com/songstock/
│   │   ├── config/             # Configuración e inicialización de datos
│   │   ├── controller/         # Endpoints REST
│   │   ├── service/            # Lógica de negocio
│   │   ├── repository/         # Acceso a datos (JPA)
│   │   ├── entity/             # Entidades JPA
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── mapper/             # Conversión entidad ↔ DTO
│   │   ├── security/           # JWT, filtros, config
│   │   ├── exception/          # Manejo de errores
│   │   └── util/               # Utilidades
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   ├── docs/postman/           # Colecciones de Postman por historia de usuario
│   └── pom.xml
│
├── songstock-frontend/
│   ├── src/
│   │   ├── components/         # Componentes React
│   │   ├── pages/              # Páginas/vistas
│   │   ├── contexts/           # Context API
│   │   ├── services/           # Llamadas API
│   │   ├── config/             # Configuración (URL de la API)
│   │   ├── types/              # TypeScript types
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── database/
│   ├── schema.sql              # Schema de base de datos
│   └── migrations/             # Reservado para migraciones (vacío)
├── docs/
│   └── ROADMAP.md              # Ruta de documentación e ingeniería
├── enunciado_proyecto.md       # Requisitos del proyecto académico
└── README.md
```

---

## 📚 Documentación API

Una vez levantado el backend, accede a la documentación interactiva Swagger:

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

> La documentación OpenAPI es parcial: solo algunos controllers tienen anotaciones `@Tag`/`@Operation`. Todas las rutas llevan el prefijo `/api/v1` (context-path).

### Principales Endpoints

#### 🔐 Autenticación
```http
POST   /auth/login              # Iniciar sesión
POST   /auth/register-customer  # Registro de cliente
POST   /auth/forgot-password    # Recuperar contraseña
```

#### 🎵 Catálogo
```http
GET    /catalog/search          # Buscar productos (paginado)
GET    /catalog/featured        # Productos destacados
GET    /products/album/{albumId}/all-formats  # Formatos disponibles de un álbum
GET    /songs/search            # Buscar canciones
```

#### 🛒 Órdenes
```http
POST   /orders                  # Crear orden
GET    /orders/my-orders        # Mis compras
POST   /orders/{id}/review      # Valorar orden
```

#### 🏪 Proveedores
```http
GET    /products/catalog/my-products  # Mis productos
POST   /products                      # Crear producto
PUT    /orders/items/{itemId}/accept  # Aceptar ítem de un pedido
PUT    /orders/items/{itemId}/ship    # Registrar envío
```

#### 🎼 Recopilaciones
```http
GET    /compilations            # Mis recopilaciones
POST   /compilations            # Crear recopilación
POST   /compilations/{id}/songs/{songId}  # Agregar canción
```

---

## 🧪 Datos de Prueba

### Usuario Preconfigurado

Al arrancar, `DataInitializer` crea el administrador si no existe:

| Rol | Username | Email | Password |
|-----|----------|-------|----------|
| Admin | admin | admin@songstock.com | admin123 |

Los proveedores se registran por invitación del administrador y los clientes con `POST /auth/register-customer`.

---

## 🗺️ Roadmap

El plan de documentación e ingeniería está en [`docs/ROADMAP.md`](docs/ROADMAP.md).

### ✅ Implementado
- [x] Sistema de autenticación JWT
- [x] Catálogo de vinilos y digitales
- [x] Gestión de órdenes multi-proveedor
- [x] Sistema de reviews
- [x] Recopilaciones privadas
- [x] Dashboard de proveedores

### 🚧 En Desarrollo
- [ ] Búsqueda de recopilaciones públicas
- [ ] Venta de canciones individuales MP3
- [ ] Notificaciones por email (SMTP)
- [ ] Pasarela de pagos (PSE, tarjetas)

### 📋 Planeado
- [ ] Chat en tiempo real (WebSockets)
- [ ] Sistema de wishlists
- [ ] Estadísticas avanzadas con gráficos
- [ ] PWA (Progressive Web App)
- [ ] App móvil (React Native)

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor:

1. Fork el proyecto
2. Crea una rama para tu cambio (`git checkout -b feat/mi-cambio`)
3. Commit tus cambios (`git commit -m 'feat: nueva funcionalidad'`)
4. Push a la rama (`git push origin feat/mi-cambio`)
5. Abre un Pull Request

### Convención de Commits

Se usa [Conventional Commits](https://www.conventionalcommits.org/es/):

```
feat: nueva funcionalidad
fix: corrección de bug
docs: cambios en documentación
refactor: refactorización sin cambio de comportamiento
test: agregar o corregir tests
chore: tareas de mantenimiento (build, dependencias, configuración)
```

---

## 📝 Licencia

Licencia pendiente de definir. Mientras no exista un archivo `LICENSE`, aplican los derechos de autor por defecto.

---

## 👥 Autores

- **Desarrollo Backend** - Spring Boot + MySQL
- **Desarrollo Frontend** - React + TypeScript + Tailwind
- **Arquitectura** - Monolito por capas (API REST Spring Boot + SPA React)

---

<div align="center">

**⭐ Si te gustó el proyecto, dale una estrella en GitHub ⭐**

Hecho con ❤️ para los amantes de la música

</div>
