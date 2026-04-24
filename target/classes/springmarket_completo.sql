-- =============================================
-- SPRING MARKET — Script SQL Completo
-- Incluye: creación de BD, tablas y datos de prueba
-- Ejecutar en MySQL Workbench o MySQL CLI
-- =============================================

-- 1. Crear y seleccionar la base de datos
CREATE DATABASE IF NOT EXISTS springmarket
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE springmarket;

-- 2. Eliminar tablas en orden inverso (por claves foráneas)
DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS venta;
DROP TABLE IF EXISTS inventario;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS proveedor;
DROP TABLE IF EXISTS empleado;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS sucursal;

-- =============================================
-- 3. CREAR TABLAS
-- =============================================

CREATE TABLE sucursal (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(100) NOT NULL,
  direccion   VARCHAR(255),
  telefono    VARCHAR(20),
  encargado   VARCHAR(100)
);

CREATE TABLE categoria (
  id_categoria  BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE proveedor (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(100) NOT NULL,
  contacto    VARCHAR(100),
  telefono    VARCHAR(20),
  email       VARCHAR(100),
  direccion   VARCHAR(255)
);

CREATE TABLE empleado (
  id_empleado   BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  password      VARCHAR(255) NOT NULL,
  cargo         VARCHAR(50)  NOT NULL,  -- ADMIN | CAJERO
  id_sucursal   BIGINT,
  CONSTRAINT fk_emp_sucursal FOREIGN KEY (id_sucursal) REFERENCES sucursal(id)
);

CREATE TABLE cliente (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(100) NOT NULL,
  email       VARCHAR(100) NOT NULL UNIQUE,
  telefono    VARCHAR(20),
  direccion   VARCHAR(255),
  password    VARCHAR(255) NOT NULL
);

CREATE TABLE producto (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(150) NOT NULL,
  precio          DECIMAL(10,2) NOT NULL,
  stock           INT DEFAULT 0,
  id_categoria    BIGINT,
  id_proveedor    BIGINT,
  CONSTRAINT fk_prod_cat  FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
  CONSTRAINT fk_prod_prov FOREIGN KEY (id_proveedor) REFERENCES proveedor(id)
);

CREATE TABLE inventario (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_producto             BIGINT NOT NULL,
  id_sucursal             BIGINT NOT NULL,
  stock                   INT DEFAULT 0,
  stock_minimo            INT DEFAULT 0,
  ultima_actualizacion    DATETIME,
  CONSTRAINT fk_inv_prod FOREIGN KEY (id_producto)  REFERENCES producto(id),
  CONSTRAINT fk_inv_suc  FOREIGN KEY (id_sucursal)  REFERENCES sucursal(id)
);

CREATE TABLE venta (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  fecha           DATETIME DEFAULT CURRENT_TIMESTAMP,
  total           DECIMAL(10,2) NOT NULL,
  estado          VARCHAR(20) DEFAULT 'COMPLETADA',  -- COMPLETADA | PENDIENTE | CANCELADA | DEVUELTA
  id_cliente      BIGINT,
  id_empleado     BIGINT,
  id_sucursal     BIGINT,
  CONSTRAINT fk_venta_cli  FOREIGN KEY (id_cliente)  REFERENCES cliente(id),
  CONSTRAINT fk_venta_emp  FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
  CONSTRAINT fk_venta_suc  FOREIGN KEY (id_sucursal) REFERENCES sucursal(id)
);

CREATE TABLE detalle_venta (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_venta        BIGINT NOT NULL,
  id_producto     BIGINT NOT NULL,
  cantidad        INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal        DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_det_venta FOREIGN KEY (id_venta)    REFERENCES venta(id),
  CONSTRAINT fk_det_prod  FOREIGN KEY (id_producto)  REFERENCES producto(id)
);

CREATE TABLE pago (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_venta        BIGINT NOT NULL,
  metodo_pago     VARCHAR(50),   -- EFECTIVO | TARJETA | TRANSFERENCIA
  monto           DECIMAL(10,2) NOT NULL,
  fecha_pago      DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pago_venta FOREIGN KEY (id_venta) REFERENCES venta(id)
);

-- =============================================
-- 4. DATOS DE PRUEBA
-- =============================================

-- Sucursales
INSERT INTO sucursal (nombre, direccion, telefono, encargado) VALUES
  ('Sucursal Central', 'Av. Cuscatlán, San Salvador', '2200-0001', 'Roberto Méndez'),
  ('Sucursal Norte',   'Blvd. Constitución, Soyapango', '2200-0002', 'Ana Flores'),
  ('Sucursal Sur',     'Carretera al Puerto, Zaragoza',  '2200-0003', 'Luis Hernández');

-- Categorías
INSERT INTO categoria (nombre) VALUES
  ('Lácteos'),
  ('Carnes'),
  ('Frutas y Verduras'),
  ('Bebidas'),
  ('Panadería'),
  ('Limpieza'),
  ('Snacks');

-- Proveedores
INSERT INTO proveedor (nombre, contacto, telefono, email, direccion) VALUES
  ('Distribuidora Nacional', 'Carlos López',   '2222-1111', 'contacto@distnal.com', 'San Salvador'),
  ('Lácteos del Valle',      'María García',   '2233-4455', 'info@lacteosv.com',    'Santa Ana'),
  ('Frutas Frescas SV',      'Pedro Martínez', '2244-5566', 'pedidos@frutassv.com', 'La Libertad');

-- =============================================
-- Empleados
-- Todos tienen password: admin123
-- Hash BCrypt generado con BCryptPasswordEncoder (strength 10)
-- =============================================
INSERT INTO empleado (nombre, username, password, cargo, id_sucursal) VALUES
  ('Administrador', 'admin',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'ADMIN', 1),
  ('Juan Cajero', 'cajero1',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'CAJERO', 1),
  ('Ana Cajero', 'cajero2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'CAJERO', 2);

-- =============================================
-- Clientes
-- Todos tienen password: cliente123
-- (mismo hash que admin123 — cámbialo si quieres contraseña diferente)
-- =============================================
INSERT INTO cliente (nombre, email, telefono, direccion, password) VALUES
  ('María González', 'maria@email.com', '7111-2222', 'Col. Escalón, San Salvador',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
  ('Carlos Rivas',   'carlos@email.com','7333-4444', 'Res. Miraflores, San Salvador',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Productos
INSERT INTO producto (nombre, precio, stock, id_categoria, id_proveedor) VALUES
  ('Leche entera 1L',      1.25, 150, 1, 2),
  ('Queso fresco 500g',    2.50,  80, 1, 2),
  ('Pollo entero kg',      3.50,  60, 2, 1),
  ('Carne molida kg',      4.00,  50, 2, 1),
  ('Tomate kg',            0.80, 200, 3, 3),
  ('Lechuga',              0.60, 100, 3, 3),
  ('Coca-Cola 2L',         1.50, 120, 4, 1),
  ('Agua pura 500ml',      0.50, 300, 4, 1),
  ('Pan francés unidad',   0.15, 500, 5, 1),
  ('Detergente 1kg',       2.80,  70, 6, 1),
  ('Papas fritas 100g',    0.90, 200, 7, 1),
  ('Yogurt 200ml',         0.75, 120, 1, 2);

-- Inventario por sucursal
INSERT INTO inventario (id_producto, id_sucursal, stock, stock_minimo, ultima_actualizacion) VALUES
  (1,  1, 50,  10, NOW()),
  (2,  1, 30,   5, NOW()),
  (3,  1, 20,   5, NOW()),
  (4,  1, 15,   5, NOW()),
  (7,  1, 40,  10, NOW()),
  (8,  1,100,  20, NOW()),
  (1,  2, 45,  10, NOW()),
  (5,  2, 80,  10, NOW()),
  (6,  2, 40,  10, NOW()),
  (9,  2,200,  50, NOW()),
  (10, 2, 25,   5, NOW()),
  (11, 2, 60,  10, NOW());

-- =============================================
-- CREDENCIALES DE PRUEBA
-- =============================================
-- EMPLEADOS (usar en login-form.html → formulario empleado/admin):
--   username: admin    password: admin123   cargo: ADMIN
--   username: cajero1  password: admin123   cargo: CAJERO
--   username: cajero2  password: admin123   cargo: CAJERO
--
-- CLIENTES (usar en login-form.html → formulario cliente):
--   email: maria@email.com   password: admin123
--   email: carlos@email.com  password: admin123
-- =============================================
