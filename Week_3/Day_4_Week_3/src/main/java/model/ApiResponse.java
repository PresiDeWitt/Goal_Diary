package model;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Modelo para representar respuestas de API
 */
public class ApiResponse {
    private final int code;
    private final String message;
    private final Headers headers;
    private final String body;

    public ApiResponse(Response response, String body) {
        this.code = response.code();
        this.message = response.message();
        this.headers = response.headers();
        this.body = body;
    }

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Headers getHeaders() { return headers; }
    public String getBody() { return body; }
}