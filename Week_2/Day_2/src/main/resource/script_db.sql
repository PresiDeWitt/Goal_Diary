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