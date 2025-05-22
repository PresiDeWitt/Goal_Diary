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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulador de diferentes tipos de tokens inválidos para analizar respuestas del servidor
 */
public class TokenSimulator {

    private final ResponseHandler responseHandler;
    private final Map<String, String> invalidTokens;

    public TokenSimulator() {
        this.responseHandler = new ResponseHandler();
        this.invalidTokens = initializeInvalidTokens();
    }

    private Map<String, String> initializeInvalidTokens() {
        Map<String, String> tokens = new HashMap<>();

        // Diferentes tipos de tokens inválidos para probar
        tokens.put("TOKEN_EXPIRADO", "expired-token-12345");
        tokens.put("TOKEN_MALFORMADO", "invalid-format-xyz");
        tokens.put("TOKEN_VACIO", "");
        tokens.put("TOKEN_NULO", null);
        tokens.put("TOKEN_MUY_CORTO", "abc");
        tokens.put("TOKEN_SIN_BEARER", "token-sin-prefijo-bearer");
        tokens.put("TOKEN_CON_ESPACIOS", " token con espacios ");
        tokens.put("TOKEN_CON_CARACTERES_ESPECIALES", "token@#$%^&*()");

        return tokens;
    }

    public void simulateInvalidTokenScenarios() {
        System.out.println("EJERCICIO EXTRA 1: Simulación de Tokens Inválidos\n");
        System.out.println("=".repeat(60));

        // URLs para probar diferentes escenarios
        List<String> testUrls = Arrays.asList(
                "https://httpbin.org/status/401",  // Simula 401 Unauthorized
                "https://httpbin.org/bearer",      // Endpoint que requiere Bearer token
                "https://api.github.com/user"      // API real que requiere autenticación
        );

        for (Map.Entry<String, String> tokenEntry : invalidTokens.entrySet()) {
            String tokenType = tokenEntry.getKey();
            String tokenValue = tokenEntry.getValue();

            System.out.println("\n" + "-".repeat(40));
            System.out.println("PROBANDO: " + tokenType);
            System.out.println("Token: " + (tokenValue != null ?
                    HeaderUtils.maskSensitiveHeader(tokenValue) : "[NULL]"));
            System.out.println("-".repeat(40));

            // Probar con la primera URL (httpbin para 401)
            testTokenWithUrl(tokenValue, testUrls.get(0), tokenType);
        }

        // Análisis de patrones de respuesta
        analyzeServerResponsePatterns();
    }

    private void testTokenWithUrl(String token, String url, String tokenType) {
        try {
            // Crear cliente temporal con token inválido
            AuthConfig tempConfig = new AuthConfig(token, "temp-key", "1.0.0");
            ApiClient tempClient = new ApiClient(tempConfig);

            // Construir petición
            Request.Builder requestBuilder = new Request.Builder().url(url);

            // Manejar diferentes casos de token
            if (token != null && !token.trim().isEmpty()) {
                if (token.startsWith("Bearer ")) {
                    requestBuilder.addHeader("Authorization", token);
                } else {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                }
            } else if (token != null && token.isEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer ");
            }
            // Si token es null, no agregamos header

            Request request = requestBuilder.build();

            System.out.println("Enviando petición...");
            HeaderUtils.printRequestHeaders(request);

            // Ejecutar petición
            Response rawResponse = tempClient.executeRequest(request);
            ApiResponse response = responseHandler.handleResponse(rawResponse);

            // Analizar respuesta
            analyzeResponse(response, tokenType);

        } catch (ApiRateLimitException e) {
            System.out.println("  CAPTURADO - Rate Limit: " + e.getMessage());
            System.out.println("  Reintentar en: " + e.getRetryAfter() + " segundos");
        } catch (ApiAuthException e) {
            System.out.println("  CAPTURADO - Auth Error: " + e.getMessage());
            System.out.println("  Código HTTP: " + e.getStatusCode());
            analyzeAuthError(e, tokenType);
        } catch (IOException e) {
            System.out.println("  ERROR DE CONEXIÓN: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  ERROR INESPERADO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private void analyzeResponse(ApiResponse response, String tokenType) {
        System.out.println("\n  ANÁLISIS DE RESPUESTA:");
        System.out.println("  Status Code: " + response.getCode());
        System.out.println("  Status Message: " + response.getMessage());

        // Analizar headers relevantes
        if (response.getHeaders().get("WWW-Authenticate") != null) {
            System.out.println("  WWW-Authenticate: " + response.getHeaders().get("WWW-Authenticate"));
        }

        if (response.getHeaders().get("X-RateLimit-Remaining") != null) {
            System.out.println("  Rate Limit Remaining: " + response.getHeaders().get("X-RateLimit-Remaining"));
        }

        // Analizar body de respuesta
        String body = response.getBody();
        if (body != null && !body.isEmpty()) {
            System.out.println("  Body contiene:");
            if (body.toLowerCase().contains("unauthorized")) {
                System.out.println("    - Mensaje de 'unauthorized'  ");
            }
            if (body.toLowerCase().contains("invalid")) {
                System.out.println("    - Mensaje de 'invalid'  ");
            }
            if (body.toLowerCase().contains("expired")) {
                System.out.println("    - Mensaje de 'expired'  ");
            }
            if (body.toLowerCase().contains("token")) {
                System.out.println("    - Referencia a 'token'  ");
            }
        }

        // Recomendación basada en el tipo de token
        provideTokenRecommendation(tokenType, response.getCode());
    }

    private void analyzeAuthError(ApiAuthException e, String tokenType) {
        System.out.println("\n  ANÁLISIS DEL ERROR DE AUTENTICACIÓN:");
        System.out.println("  Mensaje: " + e.getMessage());
        System.out.println("  Código: " + e.getStatusCode());

        // Categorizar el tipo de error
        String errorCategory = categorizeAuthError(e.getMessage(), tokenType);
        System.out.println("  Categoría: " + errorCategory);

        // Sugerencias de solución
        provideSolutionSuggestions(tokenType, e.getStatusCode());
    }

    private String categorizeAuthError(String message, String tokenType) {
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("expired") || lowerMessage.contains("expirado")) {
            return "TOKEN_EXPIRADO";
        } else if (lowerMessage.contains("invalid") || lowerMessage.contains("inválido")) {
            return "TOKEN_INVÁLIDO";
        } else if (lowerMessage.contains("forbidden") || lowerMessage.contains("prohibido")) {
            return "PERMISOS_INSUFICIENTES";
        } else if (lowerMessage.contains("unauthorized") || lowerMessage.contains("no autorizado")) {
            return "NO_AUTORIZADO";
        }

        return "ERROR_GENÉRICO";
    }

    private void provideTokenRecommendation(String tokenType, int statusCode) {
        System.out.println("\n  RECOMENDACIÓN:");

        switch (tokenType) {
            case "TOKEN_EXPIRADO":
                System.out.println("  - Implementar renovación automática de tokens");
                System.out.println("  - Usar refresh tokens cuando sea posible");
                break;
            case "TOKEN_MALFORMADO":
                System.out.println("  - Validar formato del token antes de enviar");
                System.out.println("  - Verificar que sigue el estándar JWT si aplica");
                break;
            case "TOKEN_VACIO":
            case "TOKEN_NULO":
                System.out.println("  - Implementar validación de token antes de peticiones");
                System.out.println("  - Manejar casos donde el token no está disponible");
                break;
            case "TOKEN_MUY_CORTO":
                System.out.println("  - Validar longitud mínima del token");
                System.out.println("  - Verificar proceso de generación de tokens");
                break;
            default:
                System.out.println("  - Revisar formato y contenido del token");
                System.out.println("  - Verificar configuración de autenticación");
                break;
        }
    }

    private void provideSolutionSuggestions(String tokenType, int statusCode) {
        System.out.println("\n  SUGERENCIAS DE SOLUCIÓN:");

        switch (statusCode) {
            case 401:
                System.out.println("  1. Verificar que el token esté presente y bien formado");
                System.out.println("  2. Comprobar si el token ha expirado");
                System.out.println("  3. Intentar renovar el token de autenticación");
                break;
            case 403:
                System.out.println("  1. Verificar que el token tenga los permisos necesarios");
                System.out.println("  2. Comprobar los scopes del token");
                System.out.println("  3. Contactar al administrador para permisos adicionales");
                break;
            case 429:
                System.out.println("  1. Implementar backoff exponencial");
                System.out.println("  2. Respetar headers Retry-After");
                System.out.println("  3. Reducir frecuencia de peticiones");
                break;
            default:
                System.out.println("  1. Revisar documentación de la API");
                System.out.println("  2. Verificar configuración de autenticación");
                break;
        }
    }

    private void analyzeServerResponsePatterns() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ANÁLISIS DE PATRONES DE RESPUESTA DEL SERVIDOR");
        System.out.println("=".repeat(60));

        System.out.println("\n  PATRONES IDENTIFICADOS:");
        System.out.println("1. Tokens nulos/vacíos → 401 Unauthorized");
        System.out.println("2. Tokens malformados → 401 Unauthorized con mensaje específico");
        System.out.println("3. Tokens válidos pero expirados → 401 con hint de expiración");
        System.out.println("4. Tokens válidos sin permisos → 403 Forbidden");

        System.out.println("\n ️ MEJORES PRÁCTICAS IDENTIFICADAS:");
        System.out.println("- Validar tokens antes de enviar peticiones");
        System.out.println("- Implementar manejo específico por tipo de error");
        System.out.println("- Usar renovación automática de tokens");
        System.out.println("- Implementar retry logic con backoff");
        System.out.println("- Loggear errores de forma segura (sin exponer tokens)");

        System.out.println("\n  RIESGOS DE SEGURIDAD IDENTIFICADOS:");
        System.out.println("- Exposición de tokens en logs");
        System.out.println("- Envío de tokens en URLs (deben ir en headers)");
        System.out.println("- Falta de validación antes de peticiones");
        System.out.println("- No manejar adecuadamente tokens expirados");
    }
}