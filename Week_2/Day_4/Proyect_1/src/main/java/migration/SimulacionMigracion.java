package migration;
import config.DatabaseConnection;
import model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que simula el proceso de migración con datos de prueba
 * y manejo específico de duplicados, utilizando DatabaseConnection
 */
public class SimulacionMigracion {

    // Identificador para la base de datos destino en database.properties
    private static final String BD_DESTINO = "mysql_destino";

    public static void main(String[] args) {
        // Contadores para el reporte
        int usuariosProcesados = 0;
        int usuariosInsertados = 0;
        int usuariosDuplicados = 0;
        int usuariosInvalidos = 0;
        int erroresInesperados = 0;

        Connection conexionDestino = null;

        try {
            // 1. Establecer conexión con la base de datos destino
            conexionDestino = DatabaseConnection.getConnection(BD_DESTINO);
            System.out.println("Conexión exitosa a la base de datos destino");

            // 2. Crear lista de usuarios de prueba
            List<Usuario> usuarios = crearUsuariosPrueba();
            usuariosProcesados = usuarios.size();

            // 3. Validar usuarios
            for (Usuario usuario : usuarios) {
                validarUsuario(usuario);
                if (!usuario.isEsValido()) {
                    usuariosInvalidos++;
                }
            }

            // 4. Insertar primero algunos usuarios que luego serán duplicados
            // Esto simula que ya existen en la base de datos
            insertarUsuariosPreexistentes(conexionDestino);

            // 5. Insertar usuarios válidos en la base de datos destino
            for (Usuario usuario : usuarios) {
                if (usuario.isEsValido()) {
                    try {
                        insertarUsuario(conexionDestino, usuario);
                        usuariosInsertados++;
                        System.out.println("Usuario insertado: " + usuario);
                    } catch (SQLIntegrityConstraintViolationException e) {
                        // Manejo de duplicados (código 1062 en MySQL)
                        if (e.getErrorCode() == 1062) {
                            usuariosDuplicados++;
                            System.out.println("Usuario con ID " + usuario.getId() +
                                    " no insertado: ya existe en la base de datos.");
                        } else {
                            erroresInesperados++;
                            System.err.println("Error al insertar usuario " + usuario.getId() +
                                    ": " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        erroresInesperados++;
                        System.err.println("Error al insertar usuario " + usuario.getId() +
                                ": " + e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión con la base de datos: " + e.getMessage());
            erroresInesperados++;
        } finally {
            // 6. Cerrar las conexiones
            DatabaseConnection.closeConnection(conexionDestino);
        }

        // 7. Generar reporte final
        System.out.println("\n=== REPORTE DE MIGRACIÓN ===");
        System.out.println("Total de usuarios procesados: " + usuariosProcesados);
        System.out.println("Usuarios insertados: " + usuariosInsertados);
        System.out.println("Usuarios duplicados: " + usuariosDuplicados);
        System.out.println("Usuarios inválidos: " + usuariosInvalidos);
        System.out.println("Errores inesperados: " + erroresInesperados);
    }

    /**
     * Crea una lista de usuarios de prueba
     */
    private static List<Usuario> crearUsuariosPrueba() {
        List<Usuario> usuarios = new ArrayList<>();

        // Crear varios usuarios de prueba, algunos válidos y otros no
        usuarios.add(new Usuario(1, "Juan Pérez", "juan@example.com", LocalDate.of(1990, 5, 15)));
        usuarios.add(new Usuario(2, "María López", "maria@example.com", LocalDate.of(1985, 8, 22)));
        usuarios.add(new Usuario(3, "Carlos Gómez", "carlos@example.com", LocalDate.of(1995, 3, 10)));
        usuarios.add(new Usuario(4, "Ana Martínez", "", LocalDate.of(1988, 12, 5))); // Email inválido
        usuarios.add(new Usuario(5, "", "pedro@example.com", LocalDate.of(1992, 7, 18))); // Nombre inválido
        usuarios.add(new Usuario(6, "Laura Sánchez", "laura@example.com", LocalDate.of(2000, 1, 30)));
        usuarios.add(new Usuario(7, "Roberto Fernández", "roberto@example.com", LocalDate.of(1983, 9, 12)));

        System.out.println("Se crearon " + usuarios.size() + " usuarios de prueba");
        return usuarios;
    }

    /**
     * Inserta usuarios que ya existen en la base de datos para simular duplicados
     */
    private static void insertarUsuariosPreexistentes(Connection conexion) throws SQLException {
        // Primero limpiamos la tabla para evitar problemas con pruebas anteriores
        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate("DELETE FROM usuarios");
            System.out.println("Tabla de usuarios limpiada para la simulación");
        }

        // Insertamos los usuarios 1 y 3 como preexistentes para simular duplicados
        String sql = "INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Usuario 1
            stmt.setInt(1, 1);
            stmt.setString(2, "Juan Pérez (Existente)");
            stmt.setString(3, "juan@example.com");
            stmt.setDate(4, Date.valueOf(LocalDate.of(1990, 5, 15)));
            stmt.executeUpdate();

            // Usuario 3
            stmt.setInt(1, 3);
            stmt.setString(2, "Carlos Gómez (Existente)");
            stmt.setString(3, "carlos@example.com");
            stmt.setDate(4, Date.valueOf(LocalDate.of(1995, 3, 10)));
            stmt.executeUpdate();

            System.out.println("Se insertaron 2 usuarios preexistentes (IDs: 1, 3)");
        }
    }

    /**
     * Valida un usuario según las reglas de negocio
     */
    private static void validarUsuario(Usuario usuario) {
        // Validación de nombre no vacío
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            usuario.setEsValido(false);
            usuario.setMensajeError("Nombre no válido");
            System.out.println("Usuario ID " + usuario.getId() + " inválido: Nombre no válido");
            return;
        }

        // Validación de email no vacío y con formato adecuado (simplificado)
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() || !usuario.getEmail().contains("@")) {
            usuario.setEsValido(false);
            usuario.setMensajeError("Email no válido");
            System.out.println("Usuario ID " + usuario.getId() + " inválido: Email no válido");
            return;
        }

        // Validación de fecha de nacimiento no nula
        if (usuario.getFechaNacimiento() == null) {
            usuario.setEsValido(false);
            usuario.setMensajeError("Fecha de nacimiento no válida");
            System.out.println("Usuario ID " + usuario.getId() + " inválido: Fecha de nacimiento no válida");
            return;
        }
    }

    /**
     * Inserta un usuario en la base de datos destino
     */
    private static void insertarUsuario(Connection conexion, Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getEmail());
            stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));

            stmt.executeUpdate();
        }
    }
}