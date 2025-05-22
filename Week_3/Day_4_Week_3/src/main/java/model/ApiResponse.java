package model;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Modelo para representar respuestas de API
 */
public class ApiResponse {
    private final int statusCode;
    private final String message;
    private final Headers headers;
    private final String body;

    public ApiResponse(int statusCode, String body) {
        this(statusCode, "", Headers.of(), body);
    }

    public ApiResponse(int statusCode, String message, Headers headers, String body) {
        this.statusCode = statusCode;
        this.message = message;
        this.headers = headers;
        this.body = body;
    }
    public ApiResponse(Response response, String body) {
        this.statusCode = response.code();
        this.message = response.message();
        this.headers = response.headers();
        this.body = body;
    }


    public static ApiResponse fromOkHttpResponse(Response response) {
        try {
            String bodyStr = response.body() != null ? response.body().string() : "";
            return new ApiResponse(
                    response.code(),
                    response.message(),
                    response.headers(),
                    bodyStr
            );
        } catch (Exception e) {
            return new ApiResponse(500, "Error leyendo cuerpo: " + e.getMessage(), Headers.of(), "");
        }
    }

    // Getters
    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getCode(){
        return statusCode;
    }
    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
