package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {


    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbDriver;

    public DatabaseConfig(String tipoBD) {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo database.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar las propiedades de la base de datos", e);
        }

        this.dbUrl = props.getProperty(tipoBD + ".url");
        this.dbUser = props.getProperty(tipoBD + ".user");
        this.dbPassword = props.getProperty(tipoBD + ".password");
        this.dbDriver = props.getProperty(tipoBD + ".driver");

        if (dbUrl == null || dbUser == null || dbPassword == null || dbDriver == null) {
            throw new RuntimeException("Faltan parámetros para la base de datos: " + tipoBD);
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbDriver() {
        return dbDriver;
    }
}
