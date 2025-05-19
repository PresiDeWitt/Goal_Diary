// UsuarioDAO.java
package Day_5_Week_2.src.main.java.dao;

import Day_5_Week_2.src.main.java.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection connOrigen;
    private Connection connDestino;

    public UsuarioDAO(Connection connOrigen, Connection connDestino) {
        this.connOrigen = connOrigen;
        this.connDestino = connDestino;
    }

    /**
     * Transfiere usuarios de la base de datos origen a la destino de forma transaccional
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean transferirUsuariosTransaccional() {
        long inicio = System.currentTimeMillis();
        boolean exito = false;

        try {
            // Desactivar autocommit en ambas conexiones
            connOrigen.setAutoCommit(false);
            connDestino.setAutoCommit(false);

            // Obtener usuarios de origen
            List<Usuario> usuarios = obtenerUsuariosOrigen();
            System.out.println("Iniciando transferencia de " + usuarios.size() + " usuarios...");

            // Insertar en destino
            insertarUsuariosDestino(usuarios);

            // Si va bien, hacer commit en ambas
            connOrigen.commit();
            connDestino.commit();
            System.out.println("Transacción completada exitosamente.");
            exito = true;

        } catch (SQLException e) {
            try {
                // Rollback en ambas conexiones si hay error
                if (connOrigen != null) connOrigen.rollback();
                if (connDestino != null) connDestino.rollback();
                System.out.println("Rollback ejecutado. Ningún usuario fue transferido. Error: " + e.getMessage());
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (connOrigen != null) connOrigen.setAutoCommit(true);
                if (connDestino != null) connDestino.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autocommit: " + e.getMessage());
            }
        }

        long fin = System.currentTimeMillis();
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");
        return exito;
    }

    private List<Usuario> obtenerUsuariosOrigen() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, password FROM usuarios";

        try (PreparedStatement pstmt = connOrigen.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        }
        return usuarios;
    }

    private void insertarUsuariosDestino(List<Usuario> usuarios) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nombre, email, password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connDestino.prepareStatement(sql)) {
            for (Usuario u : usuarios) {
                // Simular error para ID 4 (para probar rollback)
                if (u.getId() == 4) {
                    throw new SQLException("Error simulado para usuario con ID 4");
                }

                pstmt.setInt(1, u.getId());
                pstmt.setString(2, u.getNombre());
                pstmt.setString(3, u.getEmail());
                pstmt.setString(4, u.getPassword());
                pstmt.executeUpdate();
            }
        }
    }
}