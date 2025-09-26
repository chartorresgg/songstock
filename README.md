# 🎵 SongStock - Sistema de Gestión de Tienda de Discos

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Descripción

SongStock es una plataforma ágil para coleccionistas de vinilos y amantes de la música digital.  
Permite a proveedores gestionar catálogos, inventarios y ventas, mientras compradores pueden explorar, comprar y recibir notificaciones.  

## 🚀 Características principales
- Registro y autenticación segura de proveedores y compradores
- Gestión de usuarios y roles (admin, proveedor, comprador)
- Persistencia de datos confiable
- Catálogo de vinilos y canciones digitales
- Carrito de compras y pedidos
- Notificaciones y reportería para proveedores


## 🏗️ Arquitectura

```
┌─────────────────┐
│   Controllers   │ ← API REST Endpoints
├─────────────────┤
│    Services     │ ← Lógica de Negocio
├─────────────────┤
│  Repositories   │ ← Acceso a Datos (JPA)
├─────────────────┤
│   Entities      │ ← Modelo de Datos
└─────────────────┘
```

## 🛠️ Stack Tecnológico

- **Backend:** Java 17, Spring Boot 3.2, Spring Security
- **Frontend**: React / CSS
- **Base de Datos:** MySQL 8.0 con JPA/Hibernate
- **Autenticación:** JWT (JSON Web Tokens)
- **Documentación:** OpenAPI/Swagger
- **Testing:** JUnit 5, Testcontainers
- **Build:** Maven 3.8+


## 🚀 Instalación y Configuración

### Prerrequisitos

- ☑️ Java 17+
- ☑️ Maven 3.8+
- ☑️ MySQL 8.0+
- ☑️ Git
- ☑️ Postman
- ☑️ SpringBoot


### 1. Clonar el Repositorio

```bash
git clone https://github.com/chartorresgg/songstock.git
cd songstock
```

### 2. Configurar Base de Datos

```sql
-- Conectar a MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE song_stock CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional)
CREATE USER 'song_stock_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON vinyl_store.* TO 'vinylstore_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Ejecutar Schema

```bash
mysql -u root -p vinyl_store < database/schema.sql
```

### 4. Configurar Variables de Entorno

```bash
# Copiar archivo de configuración
cp src/main/resources/application-example.yml src/main/resources/application-local.yml

# Editar configuración local
vim src/main/resources/application-local.yml
```

### 5. Compilar y Ejecutar

```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de MySQL | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_NAME` | Nombre de BD | `vinyl_store` |
| `DB_USERNAME` | Usuario de BD | `root` |
| `DB_PASSWORD` | Password de BD | - |
| `JWT_SECRET` | Clave secreta JWT | - |
| `JWT_EXPIRATION` | Expiración JWT (ms) | `86400000` |

## 📡 API Endpoints

### Autenticación
- `POST /api/v1/auth/login` - Iniciar sesión
- `POST /api/v1/auth/register-provider` - Registrar proveedor
- `POST /api/v1/auth/logout` - Cerrar sesión

### Usuarios
- `GET /api/v1/users` - Listar usuarios
- `POST /api/v1/users` - Crear usuario
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario

### Proveedores
- `GET /api/v1/providers` - Listar proveedores
- `PATCH /api/v1/providers/{id}/verify` - Verificar proveedor
- `PATCH /api/v1/providers/{id}/reject` - Rechazar proveedor

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=UserServiceTest

# Ejecutar con coverage
mvn test jacoco:report
```

## 📊 Base de Datos

### Entidades Principales

- **User** - Usuarios del sistema (admin, provider, customer)
- **Provider** - Información adicional de proveedores
- **Category** - Categorías de productos
- **Genre** - Géneros musicales
- **Artist** - Artistas
- **Album** - Álbumes
- **Product** - Productos (vinilos físicos/digitales)

### Diagrama ER

Ver documentación completa en `/docs/database/`

## 🔐 Seguridad

- Autenticación basada en JWT
- Encriptación de passwords con BCrypt
- Roles y permisos por endpoint
- CORS configurado
- Validación de entrada de datos

## 🚀 Deployment

### Docker

```bash
# Construir imagen
docker build -t vinyl-store-api .

# Ejecutar con Docker Compose
docker-compose up
```

### Producción

```bash
# Compilar para producción
mvn clean package -Pprod

# Ejecutar JAR
java -jar target/vinyl-store-backend-1.0.0.jar --spring.profiles.active=prod
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📋 Roadmap

- [x] Sistema de autenticación y autorización
- [x] Gestión de usuarios y proveedores
- [x] Catálogo de categorías y géneros
- [ ] Gestión completa de productos
- [ ] Sistema de carrito de compras
- [ ] Procesamiento de pedidos
- [ ] Dashboard de administración
- [ ] Reportes y analytics

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## 👥 Autores

- **Tu Nombre** - *Desarrollo inicial* - [TuGitHub](https://github.com/tu-usuario)

## 🆘 Soporte

Para soporte, envía un email a soporte@vinylstore.com o crea un issue en GitHub.

---

**Hecho con ❤️ para la comunidad musical** 🎵