package app;


import procesor.EnhancedETLProcessor;
import util.ETLLogger;

public class EnhancedETLApplication {
    public static void main(String[] args) {
        System.out.println("Enhanced ETL Application - Multi-Source to SQLite");
        System.out.println("Features: Multi-Source + Incremental Load + Logging");
        System.out.println("=" .repeat(55));

        ETLLogger logger = ETLLogger.getInstance();

        try {
            logger.info("Aplicación ETL mejorada iniciada");

            EnhancedETLProcessor processor = new EnhancedETLProcessor();
            processor.executeETL();

            logger.info("Aplicación ETL mejorada completada exitosamente");

        } catch (Exception e) {
            String errorMsg = "Error fatal en la aplicación: " + e.getMessage();
            System.err.println(errorMsg);
            logger.error(errorMsg, e);
            e.printStackTrace();
        }

        System.out.println("\nAplicación terminada.");
        System.out.println("Revisa el directorio 'logs/' para detalles del procesamiento.");
    }
}