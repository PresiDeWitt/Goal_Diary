package exception;

/**
 * Excepción para límite de tasa excedido
 */
public class ApiRateLimitException extends ApiAuthException {
    private final int retryAfter;

    public ApiRateLimitException(String message, int statusCode, int retryAfter) {
        super(message, statusCode);
        this.retryAfter = retryAfter;
    }

    public int getRetryAfter() {
        return retryAfter;
    }
}