package client;
/**
 * Configuración de autenticación para el cliente API
 */
public class AuthConfig {
    private String bearerToken;
    private String apiKey;
    private String clientVersion;

    // Constructor, getters y setters
    public AuthConfig(String bearerToken, String apiKey, String clientVersion) {
        this.bearerToken = bearerToken;
        this.apiKey = apiKey;
        this.clientVersion = clientVersion;
    }

    // Getters
    public String getBearerToken() { return bearerToken; }
    public String getApiKey() { return apiKey; }
    public String getClientVersion() { return clientVersion; }
}