-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS Qapac;
USE Qapac;

-- Tabla Usuario
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    logo LONGBLOB,
    INDEX idx_correo (correo_electronico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Cliente
CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    domicilio VARCHAR(255),
    dni CHAR(8) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_dni (dni),
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Empresa
CREATE TABLE Empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    RUC CHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(200) NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_ruc (RUC),
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla TelefonoEmpresa
CREATE TABLE TelefonoEmpresa (
    id_telefono_empresa INT AUTO_INCREMENT PRIMARY KEY,
    telefono VARCHAR(15) NOT NULL,
    id_empresa INT NOT NULL,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE,
    INDEX idx_empresa (id_empresa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Sucursal
CREATE TABLE Sucursal (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    INDEX idx_ubicacion (departamento, provincia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Ruta
CREATE TABLE Ruta (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    id_sucursal_origen INT NOT NULL,
    id_sucursal_destino INT NOT NULL,
    id_empresa INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    estado ENUM('activa', 'inactiva') DEFAULT 'activa',
    FOREIGN KEY (id_sucursal_origen) REFERENCES Sucursal(id_sucursal) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal_destino) REFERENCES Sucursal(id_sucursal) ON DELETE CASCADE,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE,
    INDEX idx_empresa (id_empresa),
    INDEX idx_origen (id_sucursal_origen),
    INDEX idx_destino (id_sucursal_destino),
    INDEX idx_estado (estado),
    CHECK (id_sucursal_origen != id_sucursal_destino)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Bus
CREATE TABLE Bus (
    id_bus INT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    estado ENUM('disponible', 'en_ruta', 'mantenimiento', 'inactivo') DEFAULT 'disponible',
    id_empresa INT NOT NULL,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE,
    INDEX idx_matricula (matricula),
    INDEX idx_empresa (id_empresa),
    INDEX idx_estado (estado),
    CHECK (capacidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Asiento 
CREATE TABLE Asiento (
    id_asiento INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL,
    id_bus INT NOT NULL,
    disponibilidad ENUM('disponible', 'ocupado', 'mantenimiento') DEFAULT 'disponible',
    FOREIGN KEY (id_bus) REFERENCES Bus(id_bus) ON DELETE CASCADE,
    INDEX idx_bus (id_bus),
    INDEX idx_disponibilidad (disponibilidad),
    UNIQUE KEY unique_asiento_bus (codigo, id_bus)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Viaje
CREATE TABLE Viaje (
    id_viaje INT AUTO_INCREMENT PRIMARY KEY,
    id_ruta INT NOT NULL,
    id_bus INT NOT NULL,
    fecha_partida DATE NOT NULL,
    fecha_llegada DATE NOT NULL,
    hora_partida TIME NOT NULL,
    hora_llegada TIME NOT NULL,
    FOREIGN KEY (id_ruta) REFERENCES Ruta(id_ruta) ON DELETE CASCADE,
    FOREIGN KEY (id_bus) REFERENCES Bus(id_bus) ON DELETE CASCADE,
    INDEX idx_ruta (id_ruta),
    INDEX idx_bus (id_bus),
    INDEX idx_fecha_partida (fecha_partida),
    CHECK (fecha_llegada >= fecha_partida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Empleado
CREATE TABLE Empleado (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    dni CHAR(8) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    domicilio VARCHAR(255),
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    anios_experiencia INT DEFAULT 0,
    fecha_nacimiento DATE NOT NULL,
    disponibilidad ENUM('disponible', 'no disponible') DEFAULT 'disponible',
    INDEX idx_dni (dni),
    INDEX idx_disponibilidad (disponibilidad),
    CHECK (anios_experiencia >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Brevete
CREATE TABLE Brevete (
    id_brevete INT AUTO_INCREMENT PRIMARY KEY,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    numero VARCHAR(20) NOT NULL UNIQUE,
    INDEX idx_numero (numero),
    CHECK (fecha_vencimiento > fecha_emision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Chofer
CREATE TABLE Chofer (
    id_chofer INT AUTO_INCREMENT PRIMARY KEY,
    id_brevete INT NOT NULL,
    id_empleado INT NOT NULL,
    FOREIGN KEY (id_brevete) REFERENCES Brevete(id_brevete) ON DELETE RESTRICT,
    FOREIGN KEY (id_empleado) REFERENCES Empleado(id_empleado) ON DELETE CASCADE,
    INDEX idx_brevete (id_brevete),
    INDEX idx_empleado (id_empleado),
    UNIQUE KEY unique_empleado (id_empleado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Azafato
CREATE TABLE Azafato (
    id_azafato INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL,
    FOREIGN KEY (id_empleado) REFERENCES Empleado(id_empleado) ON DELETE CASCADE,
    INDEX idx_empleado (id_empleado),
    UNIQUE KEY unique_empleado (id_empleado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla AsignacionEmpleado
CREATE TABLE AsignacionEmpleado (
    id_asignacion_bus_azafato INT AUTO_INCREMENT PRIMARY KEY,
    id_viaje INT NOT NULL,
    id_empleado INT NOT NULL,
    FOREIGN KEY (id_viaje) REFERENCES Viaje(id_viaje) ON DELETE CASCADE,
    FOREIGN KEY (id_empleado) REFERENCES Empleado(id_empleado) ON DELETE CASCADE,
    INDEX idx_viaje (id_viaje),
    INDEX idx_empleado (id_empleado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla TipoPago
CREATE TABLE TipoPago (
    id_tipo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla MetodoPago
CREATE TABLE MetodoPago (
    id_metodo_pago INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_tipo_pago INT NOT NULL,
    descripcion TEXT,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    comision DECIMAL(5, 2) DEFAULT 0.00,
    FOREIGN KEY (id_tipo_pago) REFERENCES TipoPago(id_tipo_pago) ON DELETE CASCADE,
    INDEX idx_tipo_pago (id_tipo_pago),
    INDEX idx_estado (estado),
    CHECK (comision >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla TipoTarjeta
CREATE TABLE TipoTarjeta (
    id_tipo_tarjeta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Tarjeta
CREATE TABLE Tarjeta (
    id_tarjeta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_metodo_pago INT NOT NULL,
    id_tipo_tarjeta INT NOT NULL,
    numero VARCHAR(16) NOT NULL,
    fecha_caducidad DATE NOT NULL,
    cvv CHAR(3) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_metodo_pago) REFERENCES MetodoPago(id_metodo_pago) ON DELETE RESTRICT,
    FOREIGN KEY (id_tipo_tarjeta) REFERENCES TipoTarjeta(id_tipo_tarjeta) ON DELETE RESTRICT,
    INDEX idx_cliente (id_cliente),
    INDEX idx_metodo_pago (id_metodo_pago),
    INDEX idx_tipo_tarjeta (id_tipo_tarjeta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Pasaje
CREATE TABLE Pasaje (
    id_pasaje INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_viaje INT NOT NULL,
    id_asiento INT NOT NULL,
    estado ENUM('pendiente', 'completado', 'pagado', 'cancelado') DEFAULT 'pendiente',
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_viaje) REFERENCES Viaje(id_viaje) ON DELETE CASCADE,
    FOREIGN KEY (id_asiento) REFERENCES Asiento(id_asiento) ON DELETE CASCADE,
    INDEX idx_cliente (id_cliente),
    INDEX idx_viaje (id_viaje),
    INDEX idx_asiento (id_asiento),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Venta
CREATE TABLE Venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_metodo_pago INT NOT NULL,
    id_tarjeta INT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_metodo_pago) REFERENCES MetodoPago(id_metodo_pago) ON DELETE RESTRICT,
    FOREIGN KEY (id_tarjeta) REFERENCES Tarjeta(id_tarjeta) ON DELETE SET NULL,
    INDEX idx_metodo_pago (id_metodo_pago),
    INDEX idx_tarjeta (id_tarjeta),
    INDEX idx_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE DetalleVenta (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_pasaje INT NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_pasaje) REFERENCES Pasaje(id_pasaje) ON DELETE CASCADE,
    INDEX idx_venta (id_venta),
    INDEX idx_pasaje (id_pasaje)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Administrador (
    id_administrador INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
