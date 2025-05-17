package app;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import manager.DatabaseManager;
import dao.UsuarioDAO;
import model.Usuario;

public class MainApp {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Conexión simplificada usando herencia
            DatabaseManager dbManager = DatabaseManager.crearConexion("mysql_prueba_transacciones");
            conn = dbManager.obtenerConexion();

            crearTablaUsuarios(conn);

            List<Usuario> usuarios = new ArrayList<>();
            usuarios.add(new Usuario(1, "Ana López", "ana@example.com", "pass123"));
            usuarios.add(new Usuario(2, "Luis Pérez", "luis@gmail.com", "pass123"));
            usuarios.add(new Usuario(3, "María García", "maria@mail.com", "pass123"));
            usuarios.add(new Usuario(4, "Carlos Martínez", "carlos@empresa.com", "clave456"));
            usuarios.add(new Usuario(5, "Sofía Rodríguez", "sofia.r@dominio.com", "sofiaPass"));
            usuarios.add(new Usuario(6, "Álvaro Gil", "alvaro.g@mail.com", "alvaroGIL"));


            UsuarioDAO usuarioDAO = new UsuarioDAO((conn));
            boolean resultado = usuarioDAO.insertarUsuariosTransaccional(usuarios);

            System.out.println("Operación: " + (resultado ? "ÉXITO" : "FALLÓ"));

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { System.err.println("Error al cerrar conexión: " + e.getMessage()); }
            }
        }
    }

    private static void crearTablaUsuarios(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // Luego crea la tabla con estructura mejorada si no existe
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT PRIMARY KEY, " +
                    "nombre VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(100))");
        }
    }
}