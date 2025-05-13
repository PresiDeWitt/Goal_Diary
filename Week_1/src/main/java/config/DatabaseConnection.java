package Week_1.src.main.java.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que maneja la conexión a la base de datos
 */
public class DatabaseConnection {

    // Configura estos valores según tu entorno
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/jdbcdemo";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "password"; // Cambia la contraseña según tu configuración

    /**
     * Establece una conexión con la base de datos
     * @return Objeto Connection conectado a la base de datos
     * @throws SQLException Si ocurre un error en la conexión
     */
    public Connection getConnection() throws SQLException {
        try {
            // Asegura que el driver esté cargado (opcional en versiones recientes de Java)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establece la conexión
            return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
            throw new SQLException("Driver JDBC no encontrado", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Prueba la conexión a la base de datos
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
            return false;
        }
    }
}