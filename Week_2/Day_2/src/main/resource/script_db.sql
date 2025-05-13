-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS empresa_origen;

-- Usar la base de datos
USE empresa_origen;

-- Crear la tabla usuarios si no existe
CREATE TABLE IF NOT EXISTS usuarios (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
                                        email VARCHAR(100) NOT NULL
);

-- Insertar algunos datos de ejemplo
INSERT INTO usuarios (nombre, email) VALUES
                                         ('Juan Pérez', 'juan@ejemplo.com'),
                                         ('María García', 'maria@ejemplo.com'),
                                         ('Carlos López', 'carlos@ejemplo.com'),
                                         ('Ana Martínez', 'ana@ejemplo.com');