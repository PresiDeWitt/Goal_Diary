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
    // Agregar setters
    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}