-- =============================================================
-- Qapac - Script de Datos de Ejemplo
-- =============================================================
USE Qapac;

-- =============================================================
-- 🔹 1. Usuarios
-- =============================================================
INSERT INTO Usuario (correo_electronico, contrasena) VALUES
('admin@qapac.com', 'admin123'),
('cliente1@gmail.com', 'pass123'),
('cliente2@gmail.com', 'pass456'),
('empresa1@transperu.com', 'transperu123'),
('empresa2@andina.com', 'andina123');

-- =============================================================
-- 🔹 2. Clientes
-- =============================================================
INSERT INTO Cliente (nombres, apellidos, domicilio, dni, telefono, id_usuario) VALUES
('Luis', 'Ramírez Soto', 'Av. Los Olivos 123', '12345678', '987654321', 2),
('María', 'Torres López', 'Jr. Lima 456', '87654321', '987111222', 3);

-- =============================================================
-- 🔹 3. Empresas
-- =============================================================
INSERT INTO Empresa (nombre, RUC, razon_social, id_usuario) VALUES
('TransPerú S.A.', '20123456789', 'Transporte Interprovincial TransPerú S.A.', 4),
('AndinaBus S.R.L.', '20987654321', 'Empresa de Transportes AndinaBus S.R.L.', 5);

-- =============================================================
-- 🔹 4. Teléfonos de Empresa
-- =============================================================
INSERT INTO TelefonoEmpresa (telefono, id_empresa) VALUES
('012345678', 1),
('019876543', 2);

-- =============================================================
-- 🔹 5. Sucursales
-- =============================================================
INSERT INTO Sucursal (nombre, departamento, provincia, direccion) VALUES
('Terminal Lima', 'Lima', 'Lima', 'Av. Javier Prado 1500'),
('Terminal Cusco', 'Cusco', 'Cusco', 'Av. Sol 850'),
('Terminal Arequipa', 'Arequipa', 'Arequipa', 'Av. Ejército 300');

-- =============================================================
-- 🔹 6. Rutas
-- =============================================================
INSERT INTO Ruta (id_sucursal_origen, id_sucursal_destino, id_empresa, precio, estado) VALUES
(1, 2, 1, 120.00, 'activa'),
(1, 3, 2, 90.00, 'activa');

-- =============================================================
-- 🔹 7. Buses
-- =============================================================
INSERT INTO Bus (matricula, capacidad, estado, id_empresa) VALUES
('ABC-123', 40, 'disponible', 1),
('XYZ-789', 36, 'disponible', 2);

-- =============================================================
-- 🔹 8. Asientos
-- =============================================================
INSERT INTO Asiento (codigo, id_bus, disponibilidad) VALUES
('A1', 1, 'disponible'),
('A2', 1, 'disponible'),
('A1', 2, 'disponible'),
('A2', 2, 'ocupado');

-- =============================================================
-- 🔹 9. Viajes
-- =============================================================
INSERT INTO Viaje (id_ruta, id_bus, fecha_partida, fecha_llegada, hora_partida, hora_llegada) VALUES
(1, 1, '2025-11-05', '2025-11-06', '08:00:00', '06:00:00'),
(2, 2, '2025-11-10', '2025-11-10', '09:00:00', '18:00:00');

-- =============================================================
-- 🔹 10. Empleados
-- =============================================================
INSERT INTO Empleado (dni, telefono, domicilio, nombres, apellidos, anios_experiencia, fecha_nacimiento, disponibilidad) VALUES
('11112222', '987000111', 'Av. Grau 500', 'Carlos', 'Mendoza', 5, '1985-03-10', 'disponible'),
('22223333', '987000222', 'Jr. Callao 600', 'Ana', 'Cáceres', 3, '1990-07-22', 'disponible'),
('33334444', '987000333', 'Av. Tacna 700', 'José', 'Quispe', 2, '1992-11-11', 'disponible');

-- =============================================================
-- 🔹 11. Brevete
-- =============================================================
INSERT INTO Brevete (fecha_emision, fecha_vencimiento, numero) VALUES
('2020-01-01', '2026-01-01', 'B123456'),
('2021-05-10', '2027-05-10', 'C987654');

-- =============================================================
-- 🔹 12. Choferes
-- =============================================================
INSERT INTO Chofer (id_brevete, id_empleado) VALUES
(1, 1),
(2, 2);

-- =============================================================
-- 🔹 13. Azafato
-- =============================================================
INSERT INTO Azafato (id_empleado) VALUES
(3);

-- =============================================================
-- 🔹 14. Asignación de empleados a viajes
-- =============================================================
INSERT INTO AsignacionEmpleado (id_viaje, id_empleado) VALUES
(1, 1),
(1, 3),
(2, 2);

-- =============================================================
-- 🔹 15. Tipos de pago
-- =============================================================
INSERT INTO TipoPago (nombre) VALUES
('Tarjeta'),
('Cartera online'),
('Transferencia');

-- =============================================================
-- 🔹 16. Métodos de pago
-- =============================================================
INSERT INTO MetodoPago (nombre, id_tipo_pago, descripcion, estado, comision) VALUES
('Visa', 1, 'Pago con tarjeta Visa', 'activo', 2.50),
('MasterCard', 1, 'Pago con tarjeta MasterCard', 'activo', 2.75),
('Yape', 2, 'Pago por billetera móvil Yape', 'activo', 0.00),
('Plin', 2, 'Pago por billetera móvil Plin', 'activo', 0.00),
('Interbank', 3, 'Transferencia desde cuenta Interbank', 'activo', 0.00),
('BCP', 3, 'Transferencia desde cuenta BCP', 'activo', 0.00);

-- =============================================================
-- 🔹 17. Tipos de tarjeta
-- =============================================================
INSERT INTO TipoTarjeta (nombre) VALUES
('Crédito'),
('Débito');

-- =============================================================
-- 🔹 18. Tarjetas
-- =============================================================
INSERT INTO Tarjeta (id_cliente, id_metodo_pago, id_tipo_tarjeta, numero, fecha_caducidad, cvv) VALUES
(1, 1, 1, '4111111111111111', '2028-05-01', '123'),
(2, 2, 2, '5500000000000004', '2027-10-01', '456');

-- =============================================================
-- 🔹 19. Pasajes
-- =============================================================
INSERT INTO Pasaje (id_cliente, id_viaje, id_asiento, estado) VALUES
(1, 1, 1, 'pagado'),
(2, 2, 3, 'pendiente');

-- =============================================================
-- 🔹 20. Ventas (solo pasajes 'pagado')
-- =============================================================
INSERT INTO Venta (id_metodo_pago, id_tarjeta, fecha, hora) VALUES
(1, 1, '2025-10-26', '14:30:00');

-- =============================================================
-- 🔹 21. Detalle de venta
-- =============================================================
INSERT INTO DetalleVenta (id_venta, id_pasaje) VALUES
(1, 1);

-- =============================================================
-- 🔹 22. Administrador
-- =============================================================
INSERT INTO Administrador (id_usuario) VALUES
(1);
