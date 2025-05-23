package exception;

// ETLException.java - Excepción personalizada para operaciones ETL
public class ETLException extends Exception {
    public ETLException(String message) {
        super(message);
    }

    public ETLException(String message, Throwable cause) {
        super(message, cause);
    }
}
