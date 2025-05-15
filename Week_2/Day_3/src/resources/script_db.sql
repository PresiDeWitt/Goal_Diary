-- Insertar usuarios válidos
INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES
                                                               (1, 'Lucía', 'lucia@example.com', '1992-04-10'),
                                                               (2, 'Andrés', 'andres@example.com', '1988-09-22'),
                                                               (3, 'Valeria', 'valeria@example.com', '2000-01-15');

-- Insertar usuarios no válidos (email malformado o menores de edad)
INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES
                                                               (4, 'Jorge', 'jorge@invalid', '1990-12-12'),          -- Email inválido
                                                               (5, 'Martina', 'martina@example.com', '2011-07-03'),  -- Menor de edad
                                                               (6, 'Lola', 'lola#correo.com', '1985-02-28');          -- Email malformado