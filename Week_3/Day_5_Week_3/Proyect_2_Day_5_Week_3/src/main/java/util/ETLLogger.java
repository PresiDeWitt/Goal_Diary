
// ETLLogger.java - Singleton para logging
package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ETLLogger {
    private static ETLLogger instance;
    private PrintWriter logWriter;
    private final DateTimeFormatter formatter;

    private ETLLogger() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            // Crear directorio de logs si no existe
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("logs"));

            String logFileName = "logs/etl_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";

            this.logWriter = new PrintWriter(new FileWriter(logFileName, true));

            info("=== INICIO DE SESIÓN ETL ===");

        } catch (IOException e) {
            System.err.println("Error inicializando logger: " + e.getMessage());
        }
    }

    public static synchronized ETLLogger getInstance() {
        if (instance == null) {
            instance = new ETLLogger();
        }
        return instance;
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warning(String message) {
        log("WARNING", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public void error(String message, Exception e) {
        log("ERROR", message + " - " + e.getMessage());
        if (logWriter != null) {
            e.printStackTrace(logWriter);
            logWriter.flush();
        }
    }

    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);

        if (logWriter != null) {
            logWriter.println(logEntry);
            logWriter.flush();
        }

        // También mostrar en consola para errores críticos
        if ("ERROR".equals(level)) {
            System.err.println(logEntry);
        }
    }

    public void close() {
        if (logWriter != null) {
            info("=== FIN DE SESIÓN ETL ===");
            logWriter.close();
        }
    }
}