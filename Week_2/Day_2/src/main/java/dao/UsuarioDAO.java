package dao;

import config.DatabaseConnection;
import model.UsuarioDay_2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public List<UsuarioDay_2> getAllUsuarios() {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String query = "SELECT id, nombre, email FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UsuarioDay_2 usuario = new UsuarioDay_2();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public UsuarioDay_2 getUsuarioById(int id) {
        String query = "SELECT id, nombre, email FROM usuarios WHERE id = ?";
        UsuarioDay_2 usuario = null;

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDay_2();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return usuario;
    }

    @Override
    public int insertUsuario(UsuarioDay_2 usuario) {
        String query = "INSERT INTO usuarios(nombre, email) VALUES(?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return generatedId;
    }

    @Override
    public int updateUsuario(UsuarioDay_2 usuario) {
        String query = "UPDATE usuarios SET nombre = ?, email = ? WHERE id = ?";
        int affectedRows = 0;

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getId());

            affectedRows = stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return affectedRows;
    }

    @Override
    public boolean deleteUsuario(int id) {
        String query = "DELETE FROM usuarios WHERE id = ?";
        boolean deleted = false;

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            deleted = stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return deleted;
    }
}