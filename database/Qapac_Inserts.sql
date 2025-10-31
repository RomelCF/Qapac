USE Qapac;

-- ==============================
-- 🔹 1. Usuarios
-- ==============================
INSERT INTO Usuario (correo_electronico, contrasena) VALUES
('admin@qapac.com', 'admin123'),
('cliente1@gmail.com', 'pass123'),
('cliente2@gmail.com', 'pass456'),
('empresa1@transperu.com', 'transperu123'),
('empresa2@andina.com', 'andina123');

-- ==============================
-- 🔹 2. Clientes
-- ==============================
INSERT INTO Cliente (nombres, apellidos, domicilio, dni, telefono, id_usuario) VALUES
('Luis', 'Ramírez Soto', 'Av. Los Olivos 123', '12345678', '987654321', 2),
('María', 'Torres López', 'Jr. Lima 456', '87654321', '987111222', 3);

-- ==============================
-- 🔹 3. Empresas
-- ==============================
INSERT INTO Empresa (nombre, RUC, razon_social, id_usuario) VALUES
('TransPerú S.A.', '20123456789', 'Transporte Interprovincial TransPerú S.A.', 4),
('AndinaBus S.R.L.', '20987654321', 'Empresa de Transportes AndinaBus S.R.L.', 5);

-- ==============================
-- 🔹 4. Teléfonos de Empresa
-- ==============================
INSERT INTO TelefonoEmpresa (telefono, id_empresa) VALUES
('012345678', 1),
('019876543', 2);

-- ==============================
-- 🔹 5. Sucursales
-- ==============================
INSERT INTO Sucursal (nombre, departamento, provincia, direccion) VALUES
('Terminal Lima', 'Lima', 'Lima', 'Av. Javier Prado 1500'),
('Terminal Cusco', 'Cusco', 'Cusco', 'Av. Sol 850'),
('Terminal Arequipa', 'Arequipa', 'Arequipa', 'Av. Ejército 300');

-- ==============================
-- 🔹 6. Rutas
-- ==============================
INSERT INTO Ruta (id_sucursal_origen, id_sucursal_destino, id_empresa, precio, estado) VALUES
(1, 2, 1, 120.00, 'activa'),
(1, 3, 2, 90.00, 'activa');

-- ==============================
-- 🔹 7. Buses
-- ==============================
INSERT INTO Bus (matricula, capacidad, estado, id_empresa) VALUES
('ABC-123', 40, 'disponible', 1),
('XYZ-789', 36, 'disponible', 2);

-- ==============================
-- 🔹 8. Asientos - Bus 1 (40 asientos)
-- ==============================
INSERT INTO Asiento (codigo, id_bus, disponibilidad) VALUES
-- Piso 1
('A1', 1, 'ocupado'),      -- Reservado por pasaje 1
('A2', 1, 'disponible'),
('A3', 1, 'disponible'),
('A4', 1, 'disponible'),
('B1', 1, 'disponible'),
('B2', 1, 'disponible'),
('B3', 1, 'disponible'),
('B4', 1, 'disponible'),
('C1', 1, 'disponible'),
('C2', 1, 'disponible'),
('C3', 1, 'disponible'),
('C4', 1, 'disponible'),
('D1', 1, 'disponible'),
('D2', 1, 'disponible'),
('D3', 1, 'disponible'),
('D4', 1, 'disponible'),
('E1', 1, 'disponible'),
('E2', 1, 'disponible'),
('E3', 1, 'disponible'),
('E4', 1, 'disponible'),
-- Piso 2
('F1', 1, 'disponible'),
('F2', 1, 'disponible'),
('F3', 1, 'disponible'),
('F4', 1, 'disponible'),
('G1', 1, 'disponible'),
('G2', 1, 'disponible'),
('G3', 1, 'disponible'),
('G4', 1, 'disponible'),
('H1', 1, 'disponible'),
('H2', 1, 'disponible'),
('H3', 1, 'disponible'),
('H4', 1, 'disponible'),
('I1', 1, 'disponible'),
('I2', 1, 'disponible'),
('I3', 1, 'disponible'),
('I4', 1, 'disponible'),
('J1', 1, 'disponible'),
('J2', 1, 'disponible'),
('J3', 1, 'disponible'),
('J4', 1, 'disponible');

-- ==============================
-- 🔹 9. Asientos - Bus 2 (36 asientos)
-- ==============================
INSERT INTO Asiento (codigo, id_bus, disponibilidad) VALUES
-- Piso 1
('A1', 2, 'ocupado'),      -- Reservado por pasaje 2
('A2', 2, 'disponible'),
('A3', 2, 'disponible'),
('A4', 2, 'disponible'),
('B1', 2, 'disponible'),
('B2', 2, 'disponible'),
('B3', 2, 'disponible'),
('B4', 2, 'disponible'),
('C1', 2, 'disponible'),
('C2', 2, 'disponible'),
('C3', 2, 'disponible'),
('C4', 2, 'disponible'),
('D1', 2, 'disponible'),
('D2', 2, 'disponible'),
('D3', 2, 'disponible'),
('D4', 2, 'disponible'),
('E1', 2, 'disponible'),
('E2', 2, 'disponible'),
-- Piso 2
('F1', 2, 'disponible'),
('F2', 2, 'disponible'),
('F3', 2, 'disponible'),
('F4', 2, 'disponible'),
('G1', 2, 'disponible'),
('G2', 2, 'disponible'),
('G3', 2, 'disponible'),
('G4', 2, 'disponible'),
('H1', 2, 'disponible'),
('H2', 2, 'disponible'),
('H3', 2, 'disponible'),
('H4', 2, 'disponible'),
('I1', 2, 'disponible'),
('I2', 2, 'disponible'),
('I3', 2, 'disponible'),
('I4', 2, 'disponible'),
('J1', 2, 'disponible'),
('J2', 2, 'disponible');

-- ==============================
-- 🔹 10. Viajes
-- ==============================
INSERT INTO Viaje (id_ruta, id_bus, fecha_partida, fecha_llegada, hora_partida, hora_llegada) VALUES
(1, 1, '2025-11-05', '2025-11-06', '08:00:00', '18:00:00'),
(2, 2, '2025-11-10', '2025-11-10', '09:00:00', '18:00:00');

-- ==============================
-- 🔹 11. Empleados (CORREGIDO - disponibilidad según asignación)
-- ==============================
INSERT INTO Empleado (dni, telefono, domicilio, nombres, apellidos, anios_experiencia, fecha_nacimiento, disponibilidad) VALUES
('11112222', '987000111', 'Av. Grau 500', 'Carlos', 'Mendoza', 5, '1985-03-10', 'no disponible'),      -- Asignado a viaje 1
('22223333', '987000222', 'Jr. Callao 600', 'Ana', 'Cáceres', 3, '1990-07-22', 'no disponible'),       -- Asignado a viaje 2
('33334444', '987000333', 'Av. Tacna 700', 'José', 'Quispe', 2, '1992-11-11', 'no disponible'),        -- Asignado a viaje 1
('44445555', '987000444', 'Jr. Arequipa 800', 'Pedro', 'Flores', 1, '1995-05-15', 'no disponible'),    -- Asignado a viaje 2
('55556666', '987000555', 'Av. Venezuela 900', 'Laura', 'Rojas', 4, '1988-08-20', 'disponible'),       -- No asignado
('66667777', '987000666', 'Jr. Cusco 1000', 'Miguel', 'Vargas', 6, '1983-12-05', 'disponible');        -- No asignado

-- ==============================
-- 🔹 12. Brevete
-- ==============================
INSERT INTO Brevete (fecha_emision, fecha_vencimiento, numero) VALUES
('2020-01-01', '2026-01-01', 'B123456'),
('2021-05-10', '2027-05-10', 'C987654'),
('2019-03-15', '2025-03-15', 'D111222'),
('2022-07-20', '2028-07-20', 'E333444');

-- ==============================
-- 🔹 13. Choferes
-- ==============================
INSERT INTO Chofer (id_brevete, id_empleado) VALUES
(1, 1),  -- Carlos - Asignado a viaje 1
(2, 2),  -- Ana - Asignado a viaje 2
(3, 5),  -- Laura - Disponible
(4, 6);  -- Miguel - Disponible

-- ==============================
-- 🔹 14. Azafato
-- ==============================
INSERT INTO Azafato (id_empleado) VALUES
(3),  -- José - Asignado a viaje 1
(4);  -- Pedro - Asignado a viaje 2

-- ==============================
-- 🔹 15. Asignación de empleados a viajes
-- ==============================
INSERT INTO AsignacionEmpleado (id_viaje, id_empleado) VALUES
(1, 1),  -- Viaje 1: Chofer Carlos
(1, 3),  -- Viaje 1: Azafato José
(2, 2),  -- Viaje 2: Chofer Ana
(2, 4);  -- Viaje 2: Azafato Pedro

-- ==============================
-- 🔹 16. Tipos de pago
-- ==============================
INSERT INTO TipoPago (nombre) VALUES
('Tarjeta'),
('Cartera online'),
('Transferencia');

-- ==============================
-- 🔹 17. Métodos de pago
-- ==============================
INSERT INTO MetodoPago (nombre, id_tipo_pago, descripcion, estado, comision) VALUES
('Visa', 1, 'Pago con tarjeta Visa', 'activo', 2.50),
('MasterCard', 1, 'Pago con tarjeta MasterCard', 'activo', 2.75),
('Yape', 2, 'Pago por billetera móvil Yape', 'activo', 0.00),
('Plin', 2, 'Pago por billetera móvil Plin', 'activo', 0.00),
('Interbank', 3, 'Transferencia desde cuenta Interbank', 'activo', 0.00),
('BCP', 3, 'Transferencia desde cuenta BCP', 'activo', 0.00);

-- ==============================
-- 🔹 18. Tipos de tarjeta
-- ==============================
INSERT INTO TipoTarjeta (nombre) VALUES
('Crédito'),
('Débito');

-- ==============================
-- 🔹 19. Tarjetas
-- ==============================
INSERT INTO Tarjeta (id_cliente, id_metodo_pago, id_tipo_tarjeta, numero, fecha_caducidad, cvv) VALUES
(1, 1, 1, '4111111111111111', '2028-05-01', '123'),
(2, 2, 2, '5500000000000004', '2027-10-01', '456');

-- ==============================
-- 🔹 20. Pasajes
-- ==============================
INSERT INTO Pasaje (id_cliente, id_viaje, id_asiento, estado) VALUES
(1, 1, 1, 'pagado'),     -- Asiento A1 del bus 1 (id_asiento = 1)
(2, 2, 41, 'pendiente'); -- Asiento A1 del bus 2 (id_asiento = 41)

-- ==============================
-- 🔹 21. Ventas (solo pasajes 'pagado')
-- ==============================
INSERT INTO Venta (id_metodo_pago, id_tarjeta, fecha, hora) VALUES
(1, 1, '2025-10-26', '14:30:00');

-- ==============================
-- 🔹 22. Detalle de venta (solo para pasajes pagados)
-- ==============================
INSERT INTO DetalleVenta (id_venta, id_pasaje) VALUES
(1, 1);

-- ==============================
-- 🔹 23. Administrador
-- ==============================
INSERT INTO Administrador (id_usuario) VALUES
(1);