// MainApp.java
package Day_5_Week_2.src.main.java.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import Day_5_Week_2.src.main.java.manager.DatabaseManager;
import Day_5_Week_2.src.main.java.dao.UsuarioDAO;

public class MainApp {
    public static void main(String[] args) {
        Connection connOrigen = null;
        Connection connDestino = null;

        try {
            // Conexión a la base de datos origen
            DatabaseManager dbManagerOrigen = DatabaseManager.crearConexion("mysql_prueba_transacciones");
            connOrigen = dbManagerOrigen.obtenerConexion();

            // Conexión a la base de datos destino
            DatabaseManager dbManagerDestino = DatabaseManager.crearConexion("mysql_prueba_transacciones_usuarios_destino");
            connDestino = dbManagerDestino.obtenerConexion();

            // Crear tabla en destino si no existe
            crearTablaUsuarios(connDestino);

            // Insertar datos de prueba en origen
            insertarDatosPrueba(connOrigen);

            UsuarioDAO usuarioDAO = new UsuarioDAO(connOrigen, connDestino);

            // Transferir usuarios de origen a destino
            boolean resultado = usuarioDAO.transferirUsuariosTransaccional();

            System.out.println("Operación: " + (resultado ? "ÉXITOSA" : "ABORTADA"));

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            cerrarConexion(connOrigen);
            cerrarConexion(connDestino);
        }
    }

    private static void cerrarConexion(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { System.err.println("Error al cerrar conexión: " + e.getMessage()); }
        }
    }

    private static void crearTablaUsuarios(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT PRIMARY KEY, " +
                    "nombre VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(100))");
        }
    }

    private static void insertarDatosPrueba(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Limpiar tabla si existe
            stmt.executeUpdate("DROP TABLE IF EXISTS usuarios");
            stmt.executeUpdate("CREATE TABLE usuarios (" +
                    "id INT PRIMARY KEY, " +
                    "nombre VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(100))");

            // Insertar datos de prueba
            stmt.executeUpdate("INSERT INTO usuarios VALUES (1, 'Ana López', 'ana@example.com', 'pass123')");
            stmt.executeUpdate("INSERT INTO usuarios VALUES (2, 'Luis Pérez', 'luis@gmail.com', 'pass123')");
            stmt.executeUpdate("INSERT INTO usuarios VALUES (3, 'María García', 'maria@mail.com', 'pass123')");
            // Usuario con posible error para probar rollback
            stmt.executeUpdate("INSERT INTO usuarios VALUES (4, 'Carlos Martínez', 'carlos@empresa.com', 'clave456')");
            stmt.executeUpdate("INSERT INTO usuarios VALUES (5, 'Sofía Rodríguez', 'sofia.r@dominio.com', 'sofiaPass')");
            stmt.executeUpdate("INSERT INTO usuarios VALUES (6, 'Álvaro Gil', 'alvaro.g@mail.com', 'alvaroGIL')");
        }
    }
}