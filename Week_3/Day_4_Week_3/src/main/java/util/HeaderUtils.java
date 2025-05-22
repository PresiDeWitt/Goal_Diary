package util;

import okhttp3.Request;
import okhttp3.Headers;

/**
 * Utilidades para manejo de headers
 */
public class HeaderUtils {

    /**
     * Enmascara valores sensibles de headers para logging seguro
     */
    public static String maskSensitiveHeader(String headerValue) {
        if (headerValue == null || headerValue.isEmpty()) {
            return "[vacío]";
        }

        if (headerValue.length() <= 8) {
            return "***[oculto]***";
        }

        // Para Bearer tokens, mostrar solo "Bearer " + primeros caracteres
        if (headerValue.startsWith("Bearer ")) {
            String token = headerValue.substring(7); // Remover "Bearer "
            if (token.length() <= 6) {
                return "Bearer ***[oculto]***";
            }
            return "Bearer " + token.substring(0, 6) + "***[oculto]***";
        }

        // Para otros headers, mostrar primeros caracteres
        return headerValue.substring(0, Math.min(8, headerValue.length())) + "***[oculto]***";
    }

    /**
     * Imprime los headers de una petición de forma segura
     */
    public static void printRequestHeaders(Request request) {
        System.out.println("\nHeaders enviados:");

        if (request.headers().size() == 0) {
            System.out.println("  [Sin headers personalizados]");
            return;
        }

        request.headers().names().forEach(headerName -> {
            String headerValue = request.header(headerName);

            // Enmascarar headers sensibles
            if (isSensitiveHeader(headerName)) {
                headerValue = maskSensitiveHeader(headerValue);
            }

            System.out.println("  " + headerName + ": " + headerValue);
        });
    }

    /**
     * Imprime los headers de respuesta importantes
     */
    public static void printResponseHeaders(Headers headers) {
        System.out.println("\nHeaders de respuesta relevantes:");

        // Headers importantes para mostrar
        String[] importantHeaders = {
                "Content-Type", "Content-Length", "Server", "Date",
                "Cache-Control", "X-RateLimit-Remaining", "X-RateLimit-Reset",
                "Retry-After", "WWW-Authenticate"
        };

        boolean foundAny = false;
        for (String headerName : importantHeaders) {
            String value = headers.get(headerName);
            if (value != null) {
                System.out.println("  " + headerName + ": " + value);
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("  [Sin headers relevantes]");
        }
    }

    /**
     * Determina si un header contiene información sensible
     */
    private static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) return false;

        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") ||
                lowerName.contains("key") ||
                lowerName.contains("token") ||
                lowerName.contains("secret") ||
                lowerName.contains("password") ||
                lowerName.contains("credential");
    }

    /**
     * Crea un header de Authorization con Bearer token
     */
    public static String createBearerAuthHeader(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacío");
        }
        return "Bearer " + token.trim();
    }

    /**
     * Valida que un header de API Key sea válido
     */
    public static boolean isValidApiKey(String apiKey) {
        return apiKey != null &&
                !apiKey.trim().isEmpty() &&
                apiKey.length() >= 8; // Longitud mínima razonable
    }

    /**
     * Crea headers estándar para el cliente
     */
    public static void addStandardHeaders(Request.Builder requestBuilder, String clientVersion) {
        requestBuilder
                .addHeader("User-Agent", "Security-Demo-Client/" +
                        (clientVersion != null ? clientVersion : "1.0.0"))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json");
    }
}