package app;

import client.AuthConfig;
import demo.SecurityDemoRunner;

/**
 * Clase principal para ejecutar la demostración de seguridad en APIs
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("    ACTIVIDAD 14: SEGURIDAD BÁSICA EN APIs");
        System.out.println("=".repeat(60));

        // Configurar credenciales simuladas
        AuthConfig config = new AuthConfig(
                "mi-token-secreto-123456",      // Bearer Token simulado
                "sk-1234567890abcdef",          // API Key simulada
                "1.0.0"                         // Versión del cliente
        );

        // Mostrar información de configuración (de forma segura)
        System.out.println("\nConfiguración del cliente:");
        System.out.println("Bearer Token: " + maskCredential(config.getBearerToken()));
        System.out.println("API Key: " + maskCredential(config.getApiKey()));
        System.out.println("Versión: " + config.getClientVersion());

        System.out.println("\nINICIANDO DEMOSTRACIONES...\n");

        // Ejecutar todas las demostraciones
        SecurityDemoRunner runner = new SecurityDemoRunner(config);
        runner.runAllDemos();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("    DEMO COMPLETADA");
        System.out.println("=".repeat(60));

    }
    /**
     * Enmascara credenciales para mostrar de forma segura
     */
    private static String maskCredential(String credential) {
        if (credential == null || credential.length() <= 6) {
            return "***[oculto]***";
        }
        return credential.substring(0, 6) + "***[oculto]***";
    }
}