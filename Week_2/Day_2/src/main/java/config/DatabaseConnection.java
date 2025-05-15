package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static {
        try {
            Class.forName(DatabaseConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Failed to load JDBC driver: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(new DatabaseConfig());
    }

    public static Connection getConnection(DatabaseConfig config) throws SQLException {
        if (config == null) {
            throw new IllegalArgumentException("DatabaseConfig cannot be null");
        }

        try {
            return DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUser(),
                    config.getDbPassword()
            );
        } catch (SQLException e) {
            throw new SQLException("Failed to establish database connection: " + e.getMessage(), e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error while closing connection: " + e.getMessage());
            }
        }
    }
}