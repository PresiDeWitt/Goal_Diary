package demo;

import client.ApiClient;
import client.AuthConfig;
import exception.ApiAuthException;
import exception.ApiRateLimitException;
import model.ApiResponse;
import okhttp3.Request;
import okhttp3.Response;
import util.HeaderUtils;
import util.ResponseHandler;

import java.io.IOException;

/**
 * Clase principal que ejecuta las demostraciones de seguridad de forma robusta
 */
public class SecurityDemoRunner {

    private final ApiClient apiClient;
    private final ResponseHandler responseHandler;

    public SecurityDemoRunner(AuthConfig authConfig) {
        this.apiClient = new ApiClient(authConfig);
        this.responseHandler = new ResponseHandler();
    }

    public void runAllDemos() {
        System.out.println("DEMO: Seguridad Básica en APIs\n");

        testBearerTokenAuth();
        System.out.println("\n" + "=".repeat(50) + "\n");

        testApiKeyAuth();
        System.out.println("\n" + "=".repeat(50) + "\n");

        testAuthenticationErrors();
        System.out.println("\n" + "=".repeat(50) + "\n");

        testPublicApiWithSimulatedAuth();
        System.out.println("\n" + "=".repeat(50) + "\n");
        //Mostrar tokenRefesh
        AuthenticationDemo.demonstrateTokenRefresh();
    }

    private void testBearerTokenAuth() {
        System.out.println("TEST 1: Autenticación con Bearer Token\n");

        try {
            // Construir petición con Bearer Token
            Request request = new Request.Builder()
                    .url("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
                    .addHeader("Authorization", "Bearer " + apiClient.getAuthConfig().getBearerToken())
                    .addHeader("User-Agent", "Demo-Client/" + apiClient.getAuthConfig().getClientVersion())
                    .build();

            System.out.println("Realizando petición a CoinGecko con Bearer Token simulado...");
            HeaderUtils.printRequestHeaders(request);

            // Ejecutar petición de forma segura
            Response rawResponse = apiClient.executeRequest(request);
            ApiResponse response = responseHandler.handleResponse(rawResponse);

            System.out.println("\nRespuesta recibida:");
            System.out.println("Status: " + response.getCode() + " " + response.getMessage());

            // Mostrar body de forma controlada
            String body = response.getBody();
            if (body != null && body.length() > 500) {
                body = body.substring(0, 500) + "... [truncado]";
            }
            System.out.println("Body: " + (body != null ? body : "[sin contenido]"));

        } catch (ApiRateLimitException e) {
            // IMPORTANTE: ApiRateLimitException debe ir ANTES que ApiAuthException
            System.out.println("ERROR DE LÍMITE DE TASA: " + e.getMessage() + " (Reintentar en " + e.getRetryAfter() + " segundos)");
        } catch (ApiAuthException e) {
            System.out.println("ERROR DE AUTENTICACIÓN: " + e.getMessage() + " (Código: " + e.getStatusCode() + ")");
        } catch (IOException e) {
            System.out.println("ERROR DE CONEXIÓN: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR INESPERADO: " + e.getMessage());
        }
    }

    private void testApiKeyAuth() {
        System.out.println("TEST 2: Autenticación con API Key\n");

        try {
            // Construir petición con API Key
            Request request = new Request.Builder()
                    .url("https://api.github.com/user")
                    .addHeader("X-API-Key", apiClient.getAuthConfig().getApiKey())
                    .addHeader("User-Agent", "Demo-Client/" + apiClient.getAuthConfig().getClientVersion())
                    .build();

            System.out.println("Realizando petición a GitHub API con API Key simulada...");
            HeaderUtils.printRequestHeaders(request);

            // Ejecutar petición de forma segura
            Response rawResponse = apiClient.executeRequest(request);
            ApiResponse response = responseHandler.handleResponse(rawResponse);

            System.out.println("\nRespuesta recibida:");
            System.out.println("Status: " + response.getCode() + " " + response.getMessage());

            // Solo mostrar parte del body para no saturar la consola
            String body = response.getBody();
            if (body != null && body.length() > 200) {
                body = body.substring(0, 200) + "... [truncado]";
            }
            System.out.println("Body: " + (body != null ? body : "[sin contenido]"));

        } catch (ApiRateLimitException e) {
            System.out.println("ERROR DE LÍMITE DE TASA: " + e.getMessage() + " (Reintentar en " + e.getRetryAfter() + " segundos)");
        } catch (ApiAuthException e) {
            System.out.println("ERROR DE AUTENTICACIÓN: " + e.getMessage() + " (Código: " + e.getStatusCode() + ")");
        } catch (IOException e) {
            System.out.println("ERROR DE CONEXIÓN: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR INESPERADO: " + e.getMessage());
        }
    }

    private void testAuthenticationErrors() {
        System.out.println("TEST 3: Simulación de Errores de Autenticación\n");

        // Test con error 401
        testSpecificError("401", "https://httpbin.org/status/401", "Bearer token-invalido-123", "Authorization");

        // Test con error 403
        testSpecificError("403", "https://httpbin.org/status/403", "clave-sin-permisos", "X-API-Key");

        // Test con error 429
        testSpecificError("429", "https://httpbin.org/status/429", "Bearer " + apiClient.getAuthConfig().getBearerToken(), "Authorization");
    }

    private void testSpecificError(String errorType, String url, String headerValue, String headerName) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(headerName, headerValue)
                    .build();

            System.out.println("Simulando error " + errorType + " (" + getErrorDescription(errorType) + ")...");
            HeaderUtils.printRequestHeaders(request);

            Response rawResponse = apiClient.executeRequest(request);
            ApiResponse response = responseHandler.handleResponse(rawResponse);
            System.out.println("Respuesta: " + response.getCode() + " " + response.getMessage());

        } catch (ApiRateLimitException e) {
            // IMPORTANTE: ApiRateLimitException debe ir ANTES que ApiAuthException
            System.out.println(" ¡CAPTURADO! - " + e.getMessage() + " (Reintentar en " + e.getRetryAfter() + " segundos)");
        } catch (ApiAuthException e) {
            System.out.println(" ¡CAPTURADO! - " + e.getMessage() + " (Código: " + e.getStatusCode() + ")");
        } catch (IOException e) {
            System.out.println("ERROR DE CONEXIÓN: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        System.out.println(); // Línea en blanco para separar tests
    }

    private String getErrorDescription(String errorCode) {
        switch (errorCode) {
            case "401": return "Unauthorized";
            case "403": return "Forbidden";
            case "429": return "Rate Limit";
            default: return "Unknown";
        }
    }

    private void testPublicApiWithSimulatedAuth() {
        System.out.println("TEST 4: API Pública con Headers de Autenticación Simulados\n");

        try {
            // Usar una API pública pero enviar headers de autenticación simulados
            Request request = new Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/posts/1")
                    .addHeader("Authorization", "Bearer " + apiClient.getAuthConfig().getBearerToken())
                    .addHeader("X-API-Key", apiClient.getAuthConfig().getApiKey())
                    .addHeader("X-Client-Version", apiClient.getAuthConfig().getClientVersion())
                    .addHeader("User-Agent", "Security-Demo-Client/1.0")
                    .build();

            System.out.println("Petición a JSONPlaceholder con headers de autenticación simulados...");
            HeaderUtils.printRequestHeaders(request);

            Response rawResponse = apiClient.executeRequest(request);
            ApiResponse response = responseHandler.handleResponse(rawResponse);

            System.out.println("\nRespuesta recibida:");
            System.out.println("Status: " + response.getCode() + " " + response.getMessage());
            System.out.println("Headers de respuesta importantes:");

            // Mostrar algunos headers relevantes de forma segura
            try {
                if (response.getHeaders().get("Content-Type") != null) {
                    System.out.println("  Content-Type: " + response.getHeaders().get("Content-Type"));
                }
                if (response.getHeaders().get("Server") != null) {
                    System.out.println("  Server: " + response.getHeaders().get("Server"));
                }
                if (response.getHeaders().get("Content-Length") != null) {
                    System.out.println("  Content-Length: " + response.getHeaders().get("Content-Length"));
                }
            } catch (Exception e) {
                System.out.println("  [Error leyendo headers: " + e.getMessage() + "]");
            }

            // Mostrar body de forma controlada
            String body = response.getBody();
            if (body != null && body.length() > 300) {
                body = body.substring(0, 300) + "... [truncado]";
            }
            System.out.println("Body: " + (body != null ? body : "[sin contenido]"));

        } catch (ApiRateLimitException e) {
            System.out.println("ERROR DE LÍMITE DE TASA: " + e.getMessage() + " (Reintentar en " + e.getRetryAfter() + " segundos)");
        } catch (ApiAuthException e) {
            System.out.println("ERROR DE AUTENTICACIÓN: " + e.getMessage() + " (Código: " + e.getStatusCode() + ")");
        } catch (IOException e) {
            System.out.println("ERROR DE CONEXIÓN: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR INESPERADO: " + e.getMessage());
        }
    }
}