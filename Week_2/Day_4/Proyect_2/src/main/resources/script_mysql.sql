-- Script para crear la base de datos y las tablas necesarias para el proyecto 2
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS empresa_destino_proyecto_2;
-- Seleccionar la base de datos
USE empresa_destino_proyecto_2;
-- Crear la tabla de usuarios
CREATE TABLE usuarios (
                          id INT PRIMARY KEY,
                          nombre VARCHAR(100),
                          email VARCHAR(100),
                          fecha_nacimiento DATE
);
ALTER TABLE usuarios
    ADD COLUMN tipo_documento VARCHAR(50);

