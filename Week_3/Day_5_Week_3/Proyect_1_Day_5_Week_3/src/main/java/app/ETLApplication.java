package app;

import processor.ETLProcessor;

// ETLApplication.java - Punto de entrada de la aplicación
public class ETLApplication {
    public static void main(String[] args) {
        System.out.println("Mini ETL Application - JSONPlaceholder to SQLite");
        System.out.println("=".repeat(42));

        try {
            ETLProcessor processor = new ETLProcessor();
            processor.executeETL();

        } catch (Exception e) {
            System.err.println("Error fatal en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nAplicación terminada.");
    }
}