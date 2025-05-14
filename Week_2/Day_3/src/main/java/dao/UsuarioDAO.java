package dao;

import config.DatabaseConnection;
import model.Usuario;
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
     * Guarda un usuario en la base de datos después de validarlo
     * @param usuario Usuario a guardar
     * @return true si el usuario fue guardado correctamente, false en caso contrario
     */
    public boolean guardarUsuario(Usuario usuario) {
        // Primero validamos el usuario
        if (!UsuarioValidator.validarUsuario(usuario)) {
            return false;
        }

        // Si es válido, lo guardamos en la base de datos
        String sql = "INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getEmail());
            stmt.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar usuario en la base de datos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento").toLocalDate();

                Usuario usuario = new Usuario(id, nombre, email, fechaNacimiento);
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
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");
                    LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento").toLocalDate();

                    return new Usuario(id, nombre, email, fechaNacimiento);
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
    public boolean actualizarUsuario(Usuario usuario) {
        // Validamos el usuario primero
        if (!UsuarioValidator.validarUsuario(usuario)) {
            return false;
        }

        String sql = "UPDATE usuarios SET nombre = ?, email = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setDate(3, Date.valueOf(usuario.getFechaNacimiento()));
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
     * @return true si la tabla fue creada o ya existía, false en caso de error
     */
    public boolean crearTablaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT PRIMARY KEY," +
                "nombre VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "fecha_nacimiento DATE NOT NULL" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al crear tabla de usuarios: " + e.getMessage());
            return false;
        }
    }
}