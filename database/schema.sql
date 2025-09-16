-- Tabla de usuarios (administradores y proveedores)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'PROVIDER', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Tabla de proveedores (información adicional específica de proveedores)
CREATE TABLE providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(50),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'Colombia',
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    verification_date TIMESTAMP NULL,
    commission_rate DECIMAL(5,2) DEFAULT 10.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_verification_status (verification_status),
    INDEX idx_business_name (business_name)
);

-- Tabla de categorías para los vinilos
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- Tabla de géneros musicales
CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

-- Tabla de artistas
CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bio TEXT,
    country VARCHAR(50),
    formed_year YEAR,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_country (country)
);

-- Tabla de álbumes
CREATE TABLE albums (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    artist_id BIGINT NOT NULL,
    genre_id BIGINT,
    release_year YEAR,
    label VARCHAR(100),
    catalog_number VARCHAR(50),
    description TEXT,
    duration_minutes INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    INDEX idx_title (title),
    INDEX idx_release_year (release_year),
    INDEX idx_catalog_number (catalog_number)
);

-- Tabla de productos (vinilos físicos y digitales)
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    album_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    product_type ENUM('PHYSICAL', 'DIGITAL') NOT NULL,
    condition_type ENUM('NEW', 'LIKE_NEW', 'VERY_GOOD', 'GOOD', 'FAIR', 'POOR') DEFAULT 'NEW',
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    vinyl_size ENUM('7_INCH', '10_INCH', '12_INCH') NULL, -- Solo para vinilos físicos
    vinyl_speed ENUM('33_RPM', '45_RPM', '78_RPM') NULL, -- Solo para vinilos físicos
    file_format VARCHAR(20) NULL, -- Solo para digitales (MP3, FLAC, WAV)
    file_size_mb INT NULL, -- Solo para digitales
    weight_grams INT NULL, -- Solo para físicos
    is_active BOOLEAN DEFAULT TRUE,
    featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (album_id) REFERENCES albums(id),
    FOREIGN KEY (provider_id) REFERENCES providers(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_sku (sku),
    INDEX idx_product_type (product_type),
    INDEX idx_price (price),
    INDEX idx_featured (featured),
    INDEX idx_condition (condition_type)
);

-- Tabla de imágenes de productos
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(200),
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_is_primary (is_primary)
);

-- Tabla de sesiones de usuarios (para JWT tracking)
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_token (session_token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
);