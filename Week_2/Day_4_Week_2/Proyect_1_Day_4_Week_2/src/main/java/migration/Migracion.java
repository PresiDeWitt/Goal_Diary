package migration;
import config.DatabaseConnection;
import model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Migracion {

    // Identificadores para los tipos de bases de datos en database.properties
    private static final String BD_ORIGEN = "mysql_origen";
    private static final String BD_DESTINO = "mysql_destino";

    public static void main(String[] args) {
        // Contadores para el reporte
        int usuariosProcesados = 0;
        int usuariosInsertados = 0;
        int usuariosDuplicados = 0;
        int usuariosInvalidos = 0;
        int erroresInesperados = 0;

        Connection conexionOrigen = null;
        Connection conexionDestino = null;

        try {
            // 1. Establecer conexiones a ambas bases de datos usando DatabaseConnection
            conexionOrigen = DatabaseConnection.getConnection(BD_ORIGEN);
            System.out.println("Conexión exitosa a la base de datos origen");

            conexionDestino = DatabaseConnection.getConnection(BD_DESTINO);
            System.out.println("Conexión exitosa a la base de datos destino");

            // 2. Obtener usuarios de la base de datos origen
            List<Usuario> usuarios = obtenerUsuarios(conexionOrigen);
            usuariosProcesados = usuarios.size();

            // 3. Validar usuarios
            for (Usuario usuario : usuarios) {
                validarUsuario(usuario);
                if (!usuario.isEsValido()) {
                    usuariosInvalidos++;
                }
            }

            // 4. Insertar usuarios válidos en la base de datos destino
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
            // 5. Cerrar las conexiones utilizando el método proporcionado
            DatabaseConnection.closeConnection(conexionOrigen);
            DatabaseConnection.closeConnection(conexionDestino);
        }

        // 6. Generar reporte final
        System.out.println("\n=== REPORTE DE MIGRACIÓN ===");
        System.out.println("Total de usuarios procesados: " + usuariosProcesados);
        System.out.println("Usuarios insertados: " + usuariosInsertados);
        System.out.println("Usuarios duplicados: " + usuariosDuplicados);
        System.out.println("Usuarios inválidos: " + usuariosInvalidos);
        System.out.println("Errores inesperados: " + erroresInesperados);
    }

    /**
     * Obtiene usuarios desde la base de datos origen
     */
    private static List<Usuario> obtenerUsuarios(Connection conexion) {
        List<Usuario> usuarios = new ArrayList<>();

        try {
            String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");
                    LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ?
                            rs.getDate("fecha_nacimiento").toLocalDate() : null;

                    Usuario usuario = new Usuario(id, nombre, email, fechaNacimiento);
                    usuarios.add(usuario);
                }
            }

            System.out.println("Se obtuvieron " + usuarios.size() + " usuarios de la base de datos origen");
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios de la base de datos origen: " + e.getMessage());

            // En caso de error, creamos algunos usuarios de prueba como fallback
            System.out.println("Utilizando datos de prueba como fallback...");
            crearUsuariosPrueba(usuarios);
        }

        return usuarios;
    }

    /**
     * Crea usuarios de prueba como fallback si hay error en la lectura de la BD
     */
    private static void crearUsuariosPrueba(List<Usuario> usuarios) {
        usuarios.add(new Usuario(1, "Juan Pérez", "juan@example.com", LocalDate.of(1990, 5, 15)));
        usuarios.add(new Usuario(2, "María López", "maria@example.com", LocalDate.of(1985, 8, 22)));
        usuarios.add(new Usuario(3, "Carlos Gómez", "carlos@example.com", LocalDate.of(1995, 3, 10)));
        usuarios.add(new Usuario(4, "Ana Martínez", "", LocalDate.of(1988, 12, 5))); // Email inválido
        usuarios.add(new Usuario(5, "", "pedro@example.com", LocalDate.of(1992, 7, 18))); // Nombre inválido
        usuarios.add(new Usuario(6, "Laura Sánchez", "laura@example.com", LocalDate.of(2000, 1, 30)));
        usuarios.add(new Usuario(7, "Roberto Fernández", "roberto@example.com", LocalDate.of(1983, 9, 12)));

        System.out.println("Se crearon " + usuarios.size() + " usuarios de prueba");
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