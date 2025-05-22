package exception;
/**
 * Excepción para errores de autenticación
 */
public class ApiAuthException extends RuntimeException {
    private final int statusCode;

    public ApiAuthException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}