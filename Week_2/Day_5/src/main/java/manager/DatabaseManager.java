package manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import config.DatabaseConfig;

public class DatabaseManager extends DatabaseConfig {

    public DatabaseManager(String tipoBD) {
        super(tipoBD); // Hereda la configuración del día 2
    }

    public Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(getDbDriver());
            return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado", e);
        }
    }

    public static DatabaseManager crearConexion(String tipoBD) {
        return new DatabaseManager(tipoBD);
    }
}