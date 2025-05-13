package Week_2.Day_1.src.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static {
        try {
            Class.forName(DatabaseConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD
        );
    }
}
