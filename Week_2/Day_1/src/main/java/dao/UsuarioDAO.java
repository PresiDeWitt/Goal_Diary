package dao;


import config.DatabaseConnection;
import model.UsuarioDay_1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public List<UsuarioDay_1> getAllUsuarios() {
        List<UsuarioDay_1> usuarios = new ArrayList<>();
        String query = "SELECT id, nombre, email FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UsuarioDay_1 usuario = new UsuarioDay_1();
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
}
