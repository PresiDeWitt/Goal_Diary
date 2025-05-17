package dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDAO {

    private Connection conn;

    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserta una lista de usuarios en la base de datos utilizando transacciones
     * @param listaUsuarios Lista de usuarios a insertar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean insertarUsuariosTransaccional(List<Usuario> listaUsuarios) {
        PreparedStatement pstmt = null;
        boolean exito = false;
        long inicio = System.currentTimeMillis();

        System.out.println("Iniciando carga de " + listaUsuarios.size() + " usuarios...");

        try {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO usuarios (id, nombre, email, password) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            for (Usuario u : listaUsuarios) {
                try {
                    pstmt.setInt(1, u.getId());
                    pstmt.setString(2, u.getNombre());
                    pstmt.setString(3, u.getEmail());
                    pstmt.setString(4, u.getPassword());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Error en usuario con ID " + u.getId() + ": " + e.getMessage().split("for key")[0]);
                    throw e; // Forzar rollback
                }
            }

            conn.commit();
            System.out.println("Transacción completada exitosamente.");
            exito = true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Rollback ejecutado. Ningún usuario fue insertado.");
                }
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        long fin = System.currentTimeMillis();
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");
        return exito;
    }
}
