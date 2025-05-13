package Week_1.src.main.java.dao;

import Week_1.src.main.java.config.DatabaseConnection;
import Week_1.src.main.java.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de dao.UsuarioDAO que realiza operaciones CRUD con la base de datos
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private final DatabaseConnection dbConnection;

    public UsuarioDAOImpl() {
        this.dbConnection = new DatabaseConnection();
    }

    @Override
    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, edad FROM usuarios";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getInt("edad")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            throw e;
        }

        return usuarios;
    }

    private int getPrimerIdDisponible() throws SQLException {
        String sql = "SELECT id FROM usuarios ORDER BY id";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        }

        int id = 1;
        while (ids.contains(id)) {
            id++;
        }
        return id;
    }


    @Override
    public Usuario getUsuarioById(int id) throws SQLException {
        String sql = "SELECT id, nombre, email, edad FROM usuarios WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getInt("edad")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    @Override
    public int insertUsuario(Usuario usuario) throws SQLException {
        int nuevoId = getPrimerIdDisponible();
        String sql = "INSERT INTO usuarios (id, nombre, email, edad) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoId);
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setInt(4, usuario.getEdad());

            pstmt.executeUpdate();
            return nuevoId;

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public boolean updateUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, edad = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setInt(3, usuario.getEdad());
            pstmt.setInt(4, usuario.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw e;
        }
    }
}