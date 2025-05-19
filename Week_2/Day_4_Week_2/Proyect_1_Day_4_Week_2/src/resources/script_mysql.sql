-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS empresa_destino;
-- Seleccionar la base de datos
USE  empresa_destino;
-- Crear la tabla de usuarios
CREATE TABLE usuarios (
                          id INT PRIMARY KEY,
                          nombre VARCHAR(100),
                          email VARCHAR(100),
                          fecha_nacimiento DATE
);