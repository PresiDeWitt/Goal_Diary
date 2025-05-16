package app;

import config.DatabaseConfig;
import migration.Migracion;

public class Main {
    public static void main(String[] args) {
        // Configuración para Oracle (origen)
        DatabaseConfig configOrigen = new DatabaseConfig("oracle");

        // Configuración para MySQL (destino) desde .properties
        DatabaseConfig configDestino = new DatabaseConfig("mysql");

        // Iniciar el proceso de migración
        System.out.println("Iniciando proceso de migración de usuarios...");
        Migracion migracion = new Migracion(configOrigen, configDestino);
        migracion.ejecutarMigracion();
        System.out.println("Proceso de migración completado.");
    }
}
