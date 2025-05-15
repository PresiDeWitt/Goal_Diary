package app;

import static config.DatabaseConfig.DEFAULT_DB_PASSWORD;
import config.DatabaseConfig;
import migration.Migracion;

public class Main {
    public static void main(String[] args) {
        // Configuración CORREGIDA (origen Oracle -> destino MySQL)
        DatabaseConfig configOrigen = new DatabaseConfig(
                "jdbc:oracle:thin:@//172.16.90.11:1521/XEPDB1",
                "CIMDATA",
                "ATADMIC"
        );

        DatabaseConfig configDestino = new DatabaseConfig(
                "jdbc:mysql://localhost:3306/empresa_origen",
                "root",
                DEFAULT_DB_PASSWORD
        );

        Migracion migracion = new Migracion(configOrigen, configDestino);
        migracion.ejecutarMigracion();
        System.out.println("Proceso de migración completado.");
    }
}