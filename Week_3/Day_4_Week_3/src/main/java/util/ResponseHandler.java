package util;

import exception.ApiAuthException;
import exception.ApiRateLimitException;
import model.ApiResponse;
import okhttp3.Response;

import java.io.IOException;

/**
 * Maneja respuestas HTTP y errores de forma robusta
 */
public class ResponseHandler {

    public ApiResponse handleResponse(Response response) throws IOException {
        String body = "";

        // Manejar el body de forma segura
        try {
            if (response.body() != null) {
                body = response.body().string();
            }
        } catch (IOException e) {
            System.out.println("Advertencia: No se pudo leer el cuerpo de la respuesta: " + e.getMessage());
            body = "[Error leyendo respuesta]";
        }

        ApiResponse apiResponse = new ApiResponse(response, body);

        // Solo lanzar excepciones para códigos de error específicos
        switch (response.code()) {
            case 401:
                handleUnauthorizedError(apiResponse);
                break;
            case 403:
                handleForbiddenError(apiResponse);
                break;
            case 429:
                handleRateLimitError(apiResponse);
                break;
            default:
                // Para códigos de éxito (200-299) y otros errores,
                // simplemente devolver la respuesta sin lanzar excepción
                break;
        }

        return apiResponse;
    }

    private void handleUnauthorizedError(ApiResponse response) {
        String message = "Autenticación fallida - Token inválido o expirado";

        // Intentar obtener más detalles del cuerpo de la respuesta
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            String bodyLower = response.getBody().toLowerCase();
            if (bodyLower.contains("expired")) {
                message = "Token expirado - Es necesario renovar la autenticación";
            } else if (bodyLower.contains("invalid")) {
                message = "Token inválido - Verificar credenciales";
            } else if (bodyLower.contains("unauthorized")) {
                message = "No autorizado - Verificar token de autenticación";
            }
        }

        throw new ApiAuthException(message, response.getCode());
    }

    private void handleForbiddenError(ApiResponse response) {
        String message = "Acceso prohibido - Permisos insuficientes";

        // Intentar obtener más detalles del cuerpo de la respuesta
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            String bodyLower = response.getBody().toLowerCase();
            if (bodyLower.contains("permission")) {
                message = "Permisos insuficientes para acceder al recurso";
            } else if (bodyLower.contains("scope")) {
                message = "Token sin el scope necesario para esta operación";
            } else if (bodyLower.contains("forbidden")) {
                message = "Acceso denegado - Verificar permisos";
            }
        }

        throw new ApiAuthException(message, response.getCode());
    }

    private void handleRateLimitError(ApiResponse response) {
        // Intentar obtener el header Retry-After de forma segura
        int retryAfter = 60; // Valor por defecto

        try {
            String retryAfterHeader = response.getHeaders().get("Retry-After");

            if (retryAfterHeader != null && !retryAfterHeader.trim().isEmpty()) {
                retryAfter = Integer.parseInt(retryAfterHeader.trim());

                // Validar que el valor sea razonable (entre 1 segundo y 1 hora)
                if (retryAfter < 1) {
                    retryAfter = 1;
                } else if (retryAfter > 3600) {
                    retryAfter = 3600;
                }
            }
        } catch (NumberFormatException e) {
            // Si no se puede parsear, usar el valor por defecto
            System.out.println("Advertencia: Header Retry-After no válido, usando 60 segundos por defecto");
        } catch (Exception e) {
            // Cualquier otro error al obtener el header
            System.out.println("Advertencia: Error obteniendo Retry-After header: " + e.getMessage());
        }

        String message = "Límite de tasa excedido - Demasiadas peticiones";
        throw new ApiRateLimitException(message, response.getCode(), retryAfter);
    }

    /**
     * Metodo auxiliar para verificar si una respuesta es exitosa
     */
    public boolean isSuccessful(ApiResponse response) {
        return response.getCode() >= 200 && response.getCode() < 300;
    }

    /**
     * Metodo auxiliar para verificar si una respuesta es un error de autenticación
     */
    public boolean isAuthError(ApiResponse response) {
        return response.getCode() == 401 || response.getCode() == 403;
    }

    /**
     * Metodo auxiliar para verificar si una respuesta es un error de límite de tasa
     */
    public boolean isRateLimitError(ApiResponse response) {
        return response.getCode() == 429;
    }

    /**
     * Metodo auxiliar para verificar si una respuesta es un error del servidor
     */
    public boolean isServerError(ApiResponse response) {
        return response.getCode() >= 500 && response.getCode() < 600;
    }

    /**
     * Metodo auxiliar para verificar si una respuesta es un error del cliente
     */
    public boolean isClientError(ApiResponse response) {
        return response.getCode() >= 400 && response.getCode() < 500;
    }
}