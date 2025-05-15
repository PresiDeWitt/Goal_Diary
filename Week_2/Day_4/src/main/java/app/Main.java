package app;

import static config.DatabaseConfig.DEFAULT_DB_PASSWORD;
import config.DatabaseConfig;
import migration.Migracion;

public class Main {
    public static void main(String[] args) {

        // Configuración para la base de datos origen (puede ser MySQL u Oracle)
        DatabaseConfig configOrigen = new DatabaseConfig(
                "jdbc:mysql://localhost:3306/empresa_origen", // Cambiar si es Oracle
                "root",
                DEFAULT_DB_PASSWORD
        );
        //TODO: Cambiar la URL de la base de datos destino Oracle

        // Configuración para la base de datos destino (MySQL)
        DatabaseConfig configDestino = new DatabaseConfig(
                "jdbc:oracle:thin:172.16.90.11:1521:XEDPB1",
                "CIMDATA",
                "ATADMIC"
        );

        // Ejecutar la migración
        Migracion migracion = new Migracion(configOrigen, configDestino);
        migracion.ejecutarMigracion();

        System.out.println("Proceso de migración completado.");
    }
}