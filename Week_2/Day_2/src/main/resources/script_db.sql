CREATE DATABASE tl_database;
USE tl_database;

CREATE TABLE usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL
);

CREATE TABLE resultados_estudiantes (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
                                        promedio DOUBLE NOT NULL,
                                        desviacion DOUBLE NOT NULL
);
INSERT INTO usuarios (id, nombre, email) VALUES
                                             (1, 'Ana Martínez', 'ana.martinez@email.com'),
                                             (2, 'Luis Pérez', 'luis.perez@email.com'),
                                             (3, 'María López', 'maria.lopez@email.com'),
                                             (4, 'Carlos Gómez', 'carlos.gomez@email.com'),
                                             (5, 'Lucía Fernández', 'lucia.fernandez@email.com'),
                                             (6, 'Pedro Sánchez', 'pedro.sanchez@email.com'),
                                             (7, 'Elena Ruiz', 'elena.ruiz@email.com'),
                                             (8, 'David Romero', 'david.romero@email.com'),
                                             (9, 'Sofía Castro', 'sofia.castro@email.com'),
                                             (10, 'Javier Ortega', 'javier.ortega@email.com')
;