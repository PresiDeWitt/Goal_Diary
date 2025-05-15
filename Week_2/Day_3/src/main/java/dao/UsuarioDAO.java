package dao;

import config.DatabaseConnection;
import model.UsuarioDay_2;
import validator.UsuarioValidator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar las operaciones de base de datos relacionadas con los usuarios
 */
public class UsuarioDAO {

    /**
     * Guarda un usuario en la base de datos sin validarlo
     * @param usuario Usuario a guardar
     * @return true si el usuario fue guardado correctamente, false en caso contrario
     */
    public boolean guardarUsuarioSinValidar(UsuarioDay_2 usuario) {
        // Lo guardamos en la base de datos sin validación
        String sql = "INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getEmail());

            // Manejar posible fecha nula
            if (usuario.getFechaNacimiento() != null) {
                stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar usuario en la base de datos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda un usuario en la base de datos después de validarlo
     * @param usuario Usuario a guardar
     * @return true si el usuario fue guardado correctamente, false en caso contrario
     */
    public boolean guardarUsuario(UsuarioDay_2 usuario) {
        // Primero validamos el usuario (pero solo informamos, no bloqueamos la inserción)
        boolean esValido = UsuarioValidator.validarUsuario(usuario);

        // Guardamos el usuario sin importar si es válido o no
        return guardarUsuarioSinValidar(usuario);
    }

    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de usuarios
     */
    public List<UsuarioDay_2> obtenerTodosLosUsuarios() {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");

                // Manejar posible fecha nula
                LocalDate fechaNacimiento = null;
                Date fechaSQL = rs.getDate("fecha_nacimiento");
                if (fechaSQL != null) {
                    fechaNacimiento = fechaSQL.toLocalDate();
                }

                UsuarioDay_2 usuario = new UsuarioDay_2(id, nombre, email, fechaNacimiento);
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios de la base de datos: " + e.getMessage());
        }

        return usuarios;
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public UsuarioDay_2 obtenerUsuarioPorId(int id) {
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");

                    // Manejar posible fecha nula
                    LocalDate fechaNacimiento = null;
                    Date fechaSQL = rs.getDate("fecha_nacimiento");
                    if (fechaSQL != null) {
                        fechaNacimiento = fechaSQL.toLocalDate();
                    }

                    return new UsuarioDay_2(id, nombre, email, fechaNacimiento);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Actualiza un usuario existente después de validarlo
     * @param usuario Usuario a actualizar
     * @return true si el usuario fue actualizado correctamente, false en caso contrario
     */
    public boolean actualizarUsuario(UsuarioDay_2 usuario) {
        // Validamos el usuario, pero solo reportamos el resultado
        UsuarioValidator.validarUsuario(usuario);

        String sql = "UPDATE usuarios SET nombre = ?, email = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());

            // Manejar posible fecha nula
            if (usuario.getFechaNacimiento() != null) {
                stmt.setDate(3, Date.valueOf(usuario.getFechaNacimiento()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setInt(4, usuario.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     * @return true si el usuario fue eliminado correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método para crear la tabla de usuarios si no existe
     *
     * @return true si la tabla fue creada o ya existía, false en caso de error
     */
    public static boolean crearTablaUsuarios() {
        // Primero eliminar la tabla si existe
        String dropSql = "DROP TABLE IF EXISTS usuarios";

        // Luego crear la tabla con todas las columnas necesarias
        String createSql = "CREATE TABLE usuarios (" +
                "id INT PRIMARY KEY," +
                "nombre VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "fecha_nacimiento DATE," +
                "fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(dropSql);
            stmt.execute(createSql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al recrear la tabla: " + e.getMessage());
            return false;
        }
    }

    public static void agregarColumnaFechaNacimiento() {
        String checkColumnSql = "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() " +
                "AND table_name = 'usuarios' " +
                "AND column_name = 'fecha_nacimiento'";

        // Primero añadir la columna permitiendo valores nulos
        String alterTableSql = "ALTER TABLE usuarios ADD COLUMN fecha_nacimiento DATE NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkColumnSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                // Añadir columna permitiendo nulos
                stmt.executeUpdate(alterTableSql);
                System.out.println("Columna 'fecha_nacimiento' añadida correctamente.");
            } else {
                System.out.println("Columna 'fecha_nacimiento' ya existe.");
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar/añadir columna fecha_nacimiento: " + e.getMessage());
        }
    }
}