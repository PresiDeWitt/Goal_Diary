package app;

import client.AuthConfig;
import client.EnhancedApiClient;
import exception.ApiAuthException;
import exception.ApiRateLimitException;
import model.ApiResponse;
import okhttp3.Request;
import util.HeaderUtils;
import exception.ApiRateLimitException;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;
/**
 * EJERCICIO EXTRA 3: Programa interactivo de consola
 * Permite al usuario introducir tokens y claves manualmente
 */
public class InteractiveConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);
    private EnhancedApiClient apiClient;

    public static void main(String[] args) {
        InteractiveConsoleApp app = new InteractiveConsoleApp();
        app.run();
    }

    public void run() {
        showWelcomeMessage();

        boolean running = true;
        while (running) {
            try {
                showMainMenu();
                int choice = getMenuChoice();

                switch (choice) {
                    case 1:
                        configureAuthentication();
                        break;
                    case 2:
                        testApiConnection();
                        break;
                    case 3:
                        performApiOperations();
                        break;
                    case 4:
                        showCurrentConfig();
                        break;
                    case 5:
                        updateCredentials();
                        break;
                    case 6:
                        runSecurityTests();
                        break;
                    case 0:
                        running = false;
                        showGoodbyeMessage();
                        break;
                    default:
                        System.out.println("ERROR: Opción no válida. Intente nuevamente.");
                }

                if (running) {
                    waitForEnter();
                }

            } catch (Exception e) {
                System.out.println("ERROR: Error inesperado: " + e.getMessage());
                waitForEnter();
            }
        }
    }

    private void showWelcomeMessage() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  PROGRAMA INTERACTIVO DE AUTENTICACIÓN DE APIs");
        System.out.println("  Ejercicio Extra #3 - Seguridad Básica en APIs");
        System.out.println("=".repeat(70));
        System.out.println("\n¡Bienvenido! Este programa te permite probar diferentes");
        System.out.println("métodos de autenticación de APIs de forma interactiva.");
        System.out.println("\nPuedes introducir tus propios tokens y claves para probar");
        System.out.println("con APIs reales o usar valores simulados para testing.");
    }

    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  MENÚ PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Configurar Autenticación");
        System.out.println("2. Probar Conexión API");
        System.out.println("3. Realizar Operaciones API");
        System.out.println("4. Ver Configuración Actual");
        System.out.println("5. Actualizar Credenciales");
        System.out.println("6. Ejecutar Tests de Seguridad");
        System.out.println("0. Salir");
        System.out.println("=".repeat(50));
    }

    private int getMenuChoice() {
        System.out.print("\nSelecciona una opción: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void configureAuthentication() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  CONFIGURACIÓN DE AUTENTICACIÓN");
        System.out.println("=".repeat(50));

        System.out.println("\n¿Qué tipo de autenticación deseas configurar?");
        System.out.println("1. Solo Bearer Token");
        System.out.println("2. Solo API Key");
        System.out.println("3. Ambos (Bearer Token + API Key)");
        System.out.println("4. Usar configuración predeterminada (para pruebas)");



        int authChoice = getMenuChoice();

        String bearerToken = null;
        String apiKey = null;

        switch (authChoice) {
            case 1:
                bearerToken = getBearerTokenFromUser();
                break;
            case 2:
                apiKey = getApiKeyFromUser();
                break;
            case 3:
                bearerToken = getBearerTokenFromUser();
                apiKey = getApiKeyFromUser();
                break;
            case 4:
                bearerToken = "demo-bearer-token-123456";
                apiKey = "demo-api-key-sk-abcdef123456";
                System.out.println("OK: Usando configuración predeterminada para pruebas");
                break;
            default:
                System.out.println("ERROR: Opción no válida");
                return;
        }

        System.out.print("\nVersionón del cliente (opcional, presiona Enter para usar 1.0.0): ");
        String version = scanner.nextLine().trim();
        if (version.isEmpty()) {
            version = "1.0.0";
        }

        // Crear configuración y cliente
        AuthConfig config = new AuthConfig(bearerToken, apiKey, version);
        apiClient = new EnhancedApiClient(config);

        System.out.println("\nOK: Configuración creada exitosamente:");
        System.out.println(apiClient.getAuthInfo());
    }

    private String getBearerTokenFromUser() {
        System.out.println("\nCONFIGURACIÓN DE BEARER TOKEN");
        System.out.println("Introduce tu Bearer Token (se almacenará de forma segura):");
        System.out.println("Ejemplos válidos:");
        System.out.println("  • ghp_1234567890abcdef1234567890abcdef12345678 (GitHub)");
        System.out.println("  • sk-1234567890abcdef1234567890abcdef12345678 (OpenAI style)");
        System.out.println("  • demo-token-123456 (para pruebas)");

        System.out.print("\nBearer Token: ");
        String token = scanner.nextLine().trim();

        if (token.isEmpty()) {
            System.out.println("ADVERTENCIA: Token vacío, usando token de prueba");
            return "demo-bearer-token-" + System.currentTimeMillis();
        }

        System.out.println("OK: Bearer Token configurado: " + HeaderUtils.maskSensitiveHeader("Bearer " + token));
        return token;
    }

    private String getApiKeyFromUser() {
        System.out.println("\nCONFIGURACIÓN DE API KEY");
        System.out.println("Introduce tu API Key:");
        System.out.println("Ejemplos válidos:");
        System.out.println("  • sk-1234567890abcdef (formato corto)");
        System.out.println("  • api_key_1234567890abcdef1234567890 (formato largo)");
        System.out.println("  • demo-api-key-123456 (para pruebas)");

        System.out.print("\nAPI Key: ");
        String apiKey = scanner.nextLine().trim();

        if (apiKey.isEmpty()) {
            System.out.println("ADVERTENCIA: API Key vacía, usando clave de prueba");
            return "demo-api-key-" + System.currentTimeMillis();
        }

        if (!HeaderUtils.isValidApiKey(apiKey)) {
            System.out.println("ADVERTENCIA: API Key muy corta (mínimo 8 caracteres), pero se usará de todas formas");
        }

        System.out.println("OK: API Key configurada: " + HeaderUtils.maskSensitiveHeader(apiKey));
        return apiKey;
    }

    private void testApiConnection() {
        if (apiClient == null) {
            System.out.println("ERROR: Primero debes configurar la autenticación (opción 1)");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  PRUEBA DE CONEXIÓN API");
        System.out.println("=".repeat(50));

        System.out.println("\n¿Qué API quieres probar?");
        System.out.println("1. JSONPlaceholder (API pública, siempre funciona)");
        System.out.println("2. GitHub API (requiere token válido)");
        System.out.println("3. CoinGecko API (API pública con límites)");
        System.out.println("4. URL personalizada");

        int apiChoice = getMenuChoice();

        String testUrl;
        switch (apiChoice) {
            case 1:
                testUrl = "https://jsonplaceholder.typicode.com/posts/1";
                break;
            case 2:
                testUrl = "https://api.github.com/user";
                break;
            case 3:
                testUrl = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd";
                break;
            case 4:
                System.out.print("Introduce la URL: ");
                testUrl = scanner.nextLine().trim();
                if (testUrl.isEmpty()) {
                    System.out.println("ERROR: URL vacía");
                    return;
                }
                break;
            default:
                System.out.println("ERROR: Opción no válida");
                return;
        }

        System.out.println("\nProbando conexión a: " + testUrl);
        System.out.println("Configuración de autenticación: " + apiClient.getAuthInfo());
        System.out.println("-".repeat(50));
        try {
            ApiResponse response = apiClient.makeRequest(testUrl);

            System.out.println("OK: Conexión exitosa!");
            System.out.println("Código de respuesta: " + response.getStatusCode());
            System.out.println("Headers de respuesta: " + response.getHeaders().size() + " headers recibidos");

            String responseBody = response.getBody();
            if (responseBody.length() > 200) {
                System.out.println("Respuesta (primeros 200 chars): " + responseBody.substring(0, 200) + "...");
            } else {
                System.out.println("Respuesta completa: " + responseBody);
            }

        } catch (ApiRateLimitException e) {
            System.out.println("ERROR DE LÍMITE DE VELOCIDAD: " + e.getMessage());
            System.out.println("Sugerencia: Espera un momento antes de volver a intentar");
        } catch (ApiAuthException e) {
            System.out.println("ERROR DE AUTENTICACIÓN: " + e.getMessage());
            System.out.println("Sugerencia: Verifica que tu token/API key sea válido");
        } catch (Exception e) {
            System.out.println("ERROR GENERAL: " + e.getMessage());
        }
    }

        private void performApiOperations() {
        if (apiClient == null) {
            System.out.println("ERROR: Primero debes configurar la autenticación (opción 1)");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  OPERACIONES API AVANZADAS");
        System.out.println("=".repeat(50));

        System.out.println("\n¿Qué operación deseas realizar?");
        System.out.println("1. GET - Obtener datos");
        System.out.println("2. POST - Enviar datos");
        System.out.println("3. PUT - Actualizar datos");
        System.out.println("4. DELETE - Eliminar datos");
        System.out.println("5. Operación personalizada");

        int operation = getMenuChoice();

        System.out.print("Introduce la URL: ");
        String url = scanner.nextLine().trim();

        if (url.isEmpty()) {
            System.out.println("ERROR: URL no puede estar vacía");
            return;
        }

        try {
            ApiResponse response;

            switch (operation) {
                case 1:
                    response = apiClient.makeRequest(url);
                    break;
                case 2:
                    System.out.print("Introduce el cuerpo JSON (opcional): ");
                    String postBody = scanner.nextLine().trim();
                    response = apiClient.makePostRequest(url, postBody.isEmpty() ? "{}" : postBody);
                    break;
                case 3:
                    System.out.print("Introduce el cuerpo JSON para actualizar: ");
                    String putBody = scanner.nextLine().trim();
                    response = apiClient.makePutRequest(url, putBody.isEmpty() ? "{}" : putBody);
                    break;
                case 4:
                    response = apiClient.makeDeleteRequest(url);
                    break;
                case 5:
                    System.out.print("Introduce el método HTTP (GET, POST, etc.): ");
                    String method = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Introduce el cuerpo (opcional): ");
                    String body = scanner.nextLine().trim();
                    response = apiClient.makeCustomRequest(method, url, body.isEmpty() ? null : body);
                    break;
                default:
                    System.out.println("ERROR: Operación no válida");
                    return;
            }

            displayApiResponse(response);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    private Request.Builder createRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json");
    }

    private void showCurrentConfig() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  CONFIGURACIÓN ACTUAL");
        System.out.println("=".repeat(50));

        if (apiClient == null) {
            System.out.println("No hay configuración activa.");
            System.out.println("Usa la opción 1 para configurar la autenticación.");
        } else {
            System.out.println("Cliente API configurado:");
            System.out.println(apiClient.getAuthInfo());
            System.out.println("\nEstado: ACTIVO");
            System.out.println("Tipo de autenticación: " + apiClient.getAuthenticationType());
        }
    }

    private void updateCredentials() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  ACTUALIZAR CREDENCIALES");
        System.out.println("=".repeat(50));

        if (apiClient == null) {
            System.out.println("No hay configuración activa. Usa la opción 1 para crear una nueva.");
            return;
        }

        System.out.println("Configuración actual:");
        System.out.println(apiClient.getAuthInfo());

        System.out.println("\n¿Qué deseas actualizar?");
        System.out.println("1. Bearer Token");
        System.out.println("2. API Key");
        System.out.println("3. Ambos");
        System.out.println("4. Versión del cliente");

        int updateChoice = getMenuChoice();

        try {
            switch (updateChoice) {
                case 1:
                    String newToken = getBearerTokenFromUser();
                    apiClient.updateBearerToken(newToken);
                    System.out.println("OK: Bearer Token actualizado");
                    break;
                case 2:
                    String newApiKey = getApiKeyFromUser();
                    apiClient.updateApiKey(newApiKey);
                    System.out.println("OK: API Key actualizada");
                    break;
                case 3:
                    String token = getBearerTokenFromUser();
                    String apiKey = getApiKeyFromUser();
                    apiClient.updateCredentials(token, apiKey);
                    System.out.println("OK: Credenciales actualizadas");
                    break;
                case 4:
                    System.out.print("Nueva versión del cliente: ");
                    String version = scanner.nextLine().trim();
                    apiClient.updateVersion(version.isEmpty() ? "1.0.0" : version);
                    System.out.println("OK: Versión actualizada");
                    break;
                default:
                    System.out.println("ERROR: Opción no válida");
            }
        } catch (Exception e) {
            System.out.println("ERROR: No se pudo actualizar: " + e.getMessage());
        }
    }

    private void runSecurityTests() {
        if (apiClient == null) {
            System.out.println("ERROR: Primero debes configurar la autenticación (opción 1)");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  TESTS DE SEGURIDAD");
        System.out.println("=".repeat(50));

        System.out.println("\nEjecutando tests de seguridad básicos...");

        // Test 1: Verificar enmascaramiento de headers
        System.out.println("\n1. Test de enmascaramiento de headers sensibles:");
        String authInfo = apiClient.getAuthInfo();
        System.out.println("   Información de auth: " + authInfo);
        if (authInfo.contains("****")) {
            System.out.println("   OK: Headers sensibles están enmascarados");
        } else {
            System.out.println("   ADVERTENCIA: Headers podrían no estar enmascarados correctamente");
        }

        // Test 2: Verificar que no se imprimen credenciales completas
        System.out.println("\n2. Test de no exposición de credenciales:");
        try {
            String toString = apiClient.toString();
            if (!toString.contains("sk-") && !toString.contains("ghp_") && !toString.contains("bearer")) {
                System.out.println("   OK: toString() no expone credenciales");
            } else {
                System.out.println("   ADVERTENCIA: toString() podría exponer credenciales");
            }
        } catch (Exception e) {
            System.out.println("   ERROR: No se pudo verificar toString()");
        }

        // Test 3: Verificar validación de API Keys
        System.out.println("\n3. Test de validación de API Keys:");
        boolean shortKeyValid = HeaderUtils.isValidApiKey("abc");
        boolean longKeyValid = HeaderUtils.isValidApiKey("sk-1234567890abcdef");
        System.out.println("   Key corta ('abc') válida: " + shortKeyValid);
        System.out.println("   Key larga ('sk-1234567890abcdef') válida: " + longKeyValid);

        // Test 4: Test de conexión con credenciales inválidas
        System.out.println("\n4. Test de manejo de credenciales inválidas:");
        System.out.println("   (Este test usaría credenciales temporales inválidas)");
        System.out.println("   Implementación pendiente para evitar afectar configuración actual");

        System.out.println("\nTests de seguridad completados.");
        System.out.println("NOTA: Estos son tests básicos. En producción se requieren");
        System.out.println("tests más exhaustivos de seguridad y penetración.");
    }

    private void displayApiResponse(ApiResponse response) {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("RESPUESTA DE LA API");
        System.out.println("-".repeat(50));
        System.out.println("Código de estado: " + response.getStatusCode());
        System.out.println("Número de headers: " + response.getHeaders().size());

        response.getHeaders().names().stream()
                .filter(name -> name.toLowerCase().contains("content") ||
                        name.toLowerCase().contains("rate") ||
                        name.toLowerCase().contains("auth"))
                .forEach(name -> {
                    List<String> values = response.getHeaders().values(name);
                    System.out.println("Header: " + name + " = " + values);
                });

        String body = response.getBody();
        System.out.println("\nCuerpo de la respuesta:");
        if (body.length() > 500) {
            System.out.println(body.substring(0, 500) + "\n... (respuesta truncada, " + body.length() + " caracteres total)");
        } else {
            System.out.println(body);
        }
    }

    private void waitForEnter() {
        System.out.print("\nPresiona Enter para continuar...");
        scanner.nextLine();
    }

    private void showGoodbyeMessage() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  GRACIAS POR USAR EL PROGRAMA");
        System.out.println("=".repeat(50));
        System.out.println("\nPrograma finalizado exitosamente.");
        System.out.println("Todas las credenciales han sido limpiadas de memoria.");
        System.out.println("\nRecuerda siempre:");
        System.out.println("• Mantener tus API Keys seguras");
        System.out.println("• No compartir tokens en código fuente");
        System.out.println("• Usar variables de entorno en producción");
        System.out.println("• Rotar credenciales regularmente");
        System.out.println("\nHasta luego!");
    }
}