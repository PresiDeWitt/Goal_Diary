CREATE DATABASE tl_database;
USE tl_database;

CREATE TABLE resultados_estudiantes (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
                                        promedio DOUBLE NOT NULL,
                                        desviacion DOUBLE NOT NULL
);

CREATE TABLE estudiantes (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(100) NOT NULL,
                             promedio DOUBLE NOT NULL,
                             desviacion_estandar DOUBLE NOT NULL,
                             fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);