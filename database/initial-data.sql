-- Datos iniciales de ejemplo para pruebas HU-19 (tracks y vinilos)
-- Este script intenta no duplicar registros si se ejecuta varias veces.

-- 1) Crear un artista de ejemplo
INSERT INTO artists (name, bio, country, formed_year, is_active, created_at)
SELECT 'The Example Band', 'Banda de ejemplo para pruebas', 'Colombia', 2000, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM artists WHERE name = 'The Example Band');

-- 2) Crear un género de ejemplo
INSERT INTO genres (name, description, is_active, created_at)
SELECT 'Rock', 'Rock de ejemplo', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Rock');

-- 3) Crear un álbum de ejemplo
INSERT INTO albums (title, artist_id, genre_id, release_year, label, catalog_number, description, is_active, created_at)
SELECT 'Example Album', a.id, g.id, 2020, 'Example Label', 'EX-001', 'Álbum de prueba con pistas y vinilos', TRUE, NOW()
FROM (SELECT id FROM artists WHERE name = 'The Example Band') a, (SELECT id FROM genres WHERE name = 'Rock') g
WHERE NOT EXISTS (SELECT 1 FROM albums WHERE title = 'Example Album' AND artist_id = a.id);

-- 4) Crear categoría 'Vinilo' si no existe
INSERT INTO categories (name, description, is_active, created_at)
SELECT 'Vinilo', 'Categoría para productos vinilo', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Vinilo');

-- 5) Crear proveedor de ejemplo (usa users y providers tablas si existen)
-- Dependencias: users/providers tablas pueden variar en tu esquema real; inserto solo en providers asumiendo que ya hay un user con id=1
INSERT INTO providers (user_id, business_name, tax_id, address, city, state, postal_code, country, verification_status, created_at)
SELECT u.id, 'Example Vinyl Store', 'TAX-EX-001', 'Calle Falsa 123', 'Bogotá', 'Cundinamarca', '110111', 'Colombia', 'VERIFIED', NOW()
FROM (SELECT id FROM users WHERE username = 'example_provider' LIMIT 1) u
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'example_provider')
	AND NOT EXISTS (SELECT 1 FROM providers WHERE business_name = 'Example Vinyl Store');

-- Si no existe el user example_provider, crearlo (contraseña dummy)
INSERT INTO users (username, email, password, first_name, last_name, phone, role, is_active, created_at)
SELECT 'example_provider', 'provider@example.com', 'password', 'Example', 'Provider', '3000000000', 'PROVIDER', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'example_provider');

-- Asegurarse de que providers tenga la entrada (después de crear user)
INSERT INTO providers (user_id, business_name, tax_id, address, city, state, postal_code, country, verification_status, created_at)
SELECT u.id, 'Example Vinyl Store', 'TAX-EX-001', 'Calle Falsa 123', 'Bogotá', 'Cundinamarca', '110111', 'Colombia', 'VERIFIED', NOW()
FROM (SELECT id FROM users WHERE username = 'example_provider') u
WHERE NOT EXISTS (SELECT 1 FROM providers WHERE business_name = 'Example Vinyl Store');

-- 6) Agregar tracks para el álbum
INSERT INTO tracks (title, duration_seconds, album_id, track_number, is_active, created_at)
SELECT 'Example Track 1 (MP3)', 210, al.id, 1, TRUE, NOW()
FROM (SELECT id FROM albums WHERE title = 'Example Album') al
WHERE NOT EXISTS (SELECT 1 FROM tracks WHERE title = 'Example Track 1 (MP3)' AND album_id = al.id);

INSERT INTO tracks (title, duration_seconds, album_id, track_number, is_active, created_at)
SELECT 'Example Track 2', 195, al.id, 2, TRUE, NOW()
FROM (SELECT id FROM albums WHERE title = 'Example Album') al
WHERE NOT EXISTS (SELECT 1 FROM tracks WHERE title = 'Example Track 2' AND album_id = al.id);

INSERT INTO tracks (title, duration_seconds, album_id, track_number, is_active, created_at)
SELECT 'Example Track 3', 180, al.id, 3, TRUE, NOW()
FROM (SELECT id FROM albums WHERE title = 'Example Album') al
WHERE NOT EXISTS (SELECT 1 FROM tracks WHERE title = 'Example Track 3' AND album_id = al.id);

-- 7) Crear productos vinilo asociados al álbum (si no existen)
-- Buscar album id, provider id y category id
-- Usamos valores por defecto para SKU y precios de ejemplo
INSERT INTO products (album_id, provider_id, category_id, sku, product_type, condition_type, price, stock_quantity, vinyl_size, vinyl_speed, weight_grams, is_active, featured, created_at)
SELECT al.id, pr.id, c.id, CONCAT('VINYL-', al.id, '-1'), 'PHYSICAL', 'NEW', 29.99, 5, '12_INCH', '33_RPM', 180, TRUE, FALSE, NOW()
FROM (SELECT id FROM albums WHERE title = 'Example Album') al,
		 (SELECT id FROM providers WHERE business_name = 'Example Vinyl Store' LIMIT 1) pr,
		 (SELECT id FROM categories WHERE name = 'Vinilo' LIMIT 1) c
WHERE NOT EXISTS (SELECT 1 FROM products WHERE album_id = al.id AND sku = CONCAT('VINYL-', al.id, '-1'));

INSERT INTO products (album_id, provider_id, category_id, sku, product_type, condition_type, price, stock_quantity, vinyl_size, vinyl_speed, weight_grams, is_active, featured, created_at)
SELECT al.id, pr.id, c.id, CONCAT('VINYL-', al.id, '-2'), 'PHYSICAL', 'USED', 19.99, 2, '12_INCH', '33_RPM', 180, TRUE, FALSE, NOW()
FROM (SELECT id FROM albums WHERE title = 'Example Album') al,
		 (SELECT id FROM providers WHERE business_name = 'Example Vinyl Store' LIMIT 1) pr,
		 (SELECT id FROM categories WHERE name = 'Vinilo' LIMIT 1) c
WHERE NOT EXISTS (SELECT 1 FROM products WHERE album_id = al.id AND sku = CONCAT('VINYL-', al.id, '-2'));

-- Fin de datos de ejemplo

