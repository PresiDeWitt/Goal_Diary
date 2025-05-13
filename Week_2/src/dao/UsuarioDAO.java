package Week_2.src.dao;


import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz IUsuarioDAO que se conecta a la base de datos
 * y realiza operaciones sobre la tabla usuarios.
 */
public class UsuarioDAO implements IUsuarioDAO {

    // Configuración de la conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/empresa_origen";
    private static final String USER = "root";
    private static final String PASSWORD = "Alejandro2004#";

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return Objeto Connection para acceder a la base de datos
     * @throws SQLException Si ocurre un error al conectar con la base de datos
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT id, nombre, email FROM usuarios";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
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