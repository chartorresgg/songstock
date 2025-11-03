# 🎵 SongStock - Marketplace de Vinilos y Música Digital

<div align="center">

![SongStock Logo]()

**Marketplace moderno para coleccionistas de vinilos y amantes de la música digital**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue?logo=react)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue?logo=typescript)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

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
- **Build**: Maven 3.9

### Frontend
- **Framework**: React 18
- **Lenguaje**: TypeScript 5.0
- **Build Tool**: Vite 5
- **Routing**: React Router 6
- **State Management**: Context API
- **Estilos**: Tailwind CSS 3
- **Iconos**: Lucide React
- **HTTP Client**: Axios

### Herramientas
- **API Docs**: Swagger/OpenAPI 3.0
- **Control de Versiones**: Git
- **Containerización**: Docker (opcional)

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
git clone https://github.com/tu-usuario/songstock.git
cd songstock
```

### 2️⃣ Configurar Base de Datos
```bash
# Crear base de datos
mysql -u root -p < schema.sql

# Datos iniciales (opcional)
mysql -u root -p song_stock < initial-data.sql
```

### 3️⃣ Configurar Backend

**application.properties**
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
cd backend
mvn clean install
mvn spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080/api/v1`

### 4️⃣ Configurar Frontend

**Instalar dependencias**
```bash
cd frontend
npm install
```

**Configurar variables de entorno** (`.env`)
```env
VITE_API_URL=http://localhost:8080/api/v1
```

**Ejecutar Frontend**
```bash
npm run dev
```

La aplicación estará disponible en: `http://localhost:3000`

---

## 📂 Estructura del Proyecto

```
songstock/
├── backend/
│   ├── src/main/java/com/songstock/
│   │   ├── controller/         # Endpoints REST
│   │   ├── service/            # Lógica de negocio
│   │   ├── repository/         # Acceso a datos (JPA)
│   │   ├── entity/             # Entidades JPA
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── security/           # JWT, filtros, config
│   │   ├── exception/          # Manejo de errores
│   │   └── util/               # Utilidades
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/         # Componentes React
│   │   ├── pages/              # Páginas/vistas
│   │   ├── contexts/           # Context API
│   │   ├── services/           # Llamadas API
│   │   ├── types/              # TypeScript types
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── schema.sql                  # Schema de base de datos
├── initial-data.sql            # Datos de prueba
├── docker-compose.yml          # Orquestación (opcional)
├── README.md
└── LICENSE
```

---

## 📚 Documentación API

Una vez levantado el backend, accede a la documentación interactiva Swagger:

```
http://localhost:8080/api/v1/swagger-ui.html
```

### Principales Endpoints

#### 🔐 Autenticación
```http
POST   /auth/login              # Iniciar sesión
POST   /auth/register           # Registro de cliente
POST   /auth/forgot-password    # Recuperar contraseña
```

#### 🎵 Catálogo
```http
GET    /catalog/search          # Buscar productos (paginado)
GET    /catalog/featured        # Productos destacados
GET    /albums/{id}/formats     # Formatos disponibles de un álbum
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
GET    /products/my-products    # Mis productos
POST   /products                # Crear producto
PUT    /items/{id}/accept       # Aceptar pedido
PUT    /items/{id}/ship         # Registrar envío
```

#### 🎼 Recopilaciones
```http
GET    /compilations            # Mis recopilaciones
GET    /compilations/public     # Recopilaciones públicas
POST   /compilations            # Crear recopilación
POST   /compilations/{id}/songs/{songId}  # Agregar canción
```

---

## 🧪 Datos de Prueba

### Usuarios Preconfigurados

| Rol | Username | Email | Password |
|-----|----------|-------|----------|
| Admin | admin | admin@songstock.com | Admin123! |
| Proveedor | vinyl_store | store@example.com | Store123! |
| Cliente | john_doe | john@example.com | User123! |

---

## 🐳 Docker (Opcional)

```bash
# Levantar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

---

## 🗺️ Roadmap

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
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Convención de Commits
```
Add: nueva funcionalidad
Fix: corrección de bug
Update: actualización de código existente
Docs: cambios en documentación
Style: formato, punto y coma faltante, etc.
Refactor: refactorización de código
Test: agregar tests
```

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo [LICENSE](LICENSE) para más detalles.

---

## 👥 Autores

- **Desarrollo Backend** - Spring Boot + MySQL
- **Desarrollo Frontend** - React + TypeScript + Tailwind
- **Arquitectura** - Microservicios REST

---

## 📞 Contacto

- **Website**: [songstock.com](https://songstock.com)
- **Email**: contacto@songstock.com
- **GitHub**: [@songstock](https://github.com/tu-usuario/songstock)

---

<div align="center">

**⭐ Si te gustó el proyecto, dale una estrella en GitHub ⭐**

Hecho con ❤️ para los amantes de la música

</div>
