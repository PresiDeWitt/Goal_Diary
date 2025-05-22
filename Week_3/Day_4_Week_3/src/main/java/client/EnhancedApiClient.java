package client;

import exception.ApiAuthException;
import exception.ApiRateLimitException;
import model.ApiResponse;
import okhttp3.*;
import util.HeaderUtils;
import util.ResponseHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * EJERCICIO EXTRA 2: Cliente API centralizado y reutilizable
 * Centraliza la lógica de autenticación para reutilizar en múltiples APIs
 */
public class EnhancedApiClient {

    private final OkHttpClient httpClient;
    private AuthConfig authConfig;
    private final ResponseHandler responseHandler;

    // Configuración de timeouts
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_WRITE_TIMEOUT = 30;

    /**
     * Constructor con configuración de autenticación
     */
    public EnhancedApiClient(AuthConfig authConfig) {
        this.authConfig = authConfig;
        this.responseHandler = new ResponseHandler();
        this.httpClient = createHttpClient();
    }

    /**
     * Constructor con configuración personalizada de timeouts
     */
    public EnhancedApiClient(AuthConfig authConfig, int connectTimeout, int readTimeout, int writeTimeout) {
        this.authConfig = authConfig;
        this.responseHandler = new ResponseHandler();
        this.httpClient = createHttpClient(connectTimeout, readTimeout, writeTimeout);
    }

    /**
     * Crea el cliente HTTP con configuración estándar
     */
    private OkHttpClient createHttpClient() {
        return createHttpClient(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    /**
     * Crea el cliente HTTP con configuración personalizada
     */
    private OkHttpClient createHttpClient(int connectTimeout, int readTimeout, int writeTimeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * Realiza una petición GET con autenticación automática
     */
    public ApiResponse get(String url) throws IOException, ApiAuthException, ApiRateLimitException {
        Request request = createRequestBuilder(url)
                .get()
                .build();

        return executeRequest(request);
    }

    /**
     * Realiza una petición POST con autenticación automática
     */
    public ApiResponse post(String url, String jsonBody) throws IOException, ApiAuthException, ApiRateLimitException {
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = createRequestBuilder(url)
                .post(body)
                .build();

        return executeRequest(request);
    }

    /**
     * Realiza una petición PUT con autenticación automática
     */
    public ApiResponse put(String url, String jsonBody) throws IOException, ApiAuthException, ApiRateLimitException {
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        Request request = createRequestBuilder(url)
                .put(body)
                .build();

        return executeRequest(request);
    }

    /**
     * Realiza una petición DELETE con autenticación automática
     */
    public ApiResponse delete(String url) throws IOException, ApiAuthException, ApiRateLimitException {
        Request request = createRequestBuilder(url)
                .delete()
                .build();

        return executeRequest(request);
    }

    /**
     * Crea un builder de petición con headers de autenticación automáticos
     */
    private Request.Builder createRequestBuilder(String url) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        // Agregar headers de autenticación automáticamente
        addAuthenticationHeaders(builder);

        // Agregar headers estándar
        HeaderUtils.addStandardHeaders(builder, authConfig.getClientVersion());

        return builder;
    }

    /**
     * Agrega headers de autenticación basados en la configuración
     */
    private void addAuthenticationHeaders(Request.Builder builder) {
        // Bearer Token
        if (authConfig.getBearerToken() != null && !authConfig.getBearerToken().trim().isEmpty()) {
            builder.addHeader("Authorization", HeaderUtils.createBearerAuthHeader(authConfig.getBearerToken()));
        }

        // API Key
        if (authConfig.getApiKey() != null && HeaderUtils.isValidApiKey(authConfig.getApiKey())) {
            builder.addHeader("X-API-Key", authConfig.getApiKey());
        }

        // Headers adicionales
        if (authConfig.getClientVersion() != null) {
            builder.addHeader("X-Client-Version", authConfig.getClientVersion());
        }
    }

    /**
     * Ejecuta una petición personalizada con manejo de errores
     */
    public ApiResponse executeRequest(Request request) throws IOException, ApiAuthException, ApiRateLimitException {
        try (Response rawResponse = httpClient.newCall(request).execute()) {
            return responseHandler.handleResponse(rawResponse);
        }
    }

    /**
     * Ejecuta una petición con reintentos automáticos para rate limiting
     */
    public ApiResponse executeRequestWithRetry(Request request, int maxRetries)
            throws IOException, ApiAuthException {

        int attempts = 0;

        while (attempts <= maxRetries) {
            try {
                return executeRequest(request);

            } catch (ApiRateLimitException e) {
                attempts++;

                if (attempts > maxRetries) {
                    System.out.println("Máximo número de intentos alcanzado (" + maxRetries + ")");
                    throw e;
                }

                int waitTime = e.getRetryAfter();
                System.out.println("Rate limit alcanzado. Esperando " + waitTime + " segundos antes del intento " + (attempts + 1) + "...");

                try {
                    Thread.sleep(waitTime * 1000L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Operación interrumpida durante espera de rate limit", ie);
                }
            }
        }

        throw new IOException("No se pudo completar la petición después de " + maxRetries + " intentos");
    }

    /**
     * Actualiza la configuración de autenticación (útil para renovación de tokens)
     */
    public void updateAuthConfig(AuthConfig newAuthConfig) {
        this.authConfig = newAuthConfig;
        System.out.println("Configuración de autenticación actualizada");
    }

    /**
     * Actualiza solo el Bearer Token
     */
    public void updateBearerToken(String newToken) {
        AuthConfig newConfig = new AuthConfig(newToken, authConfig.getApiKey(), authConfig.getClientVersion());
        updateAuthConfig(newConfig);
    }

    /**
     * Actualiza solo la API Key
     */
    public void updateApiKey(String newApiKey) {
        AuthConfig newConfig = new AuthConfig(authConfig.getBearerToken(), newApiKey, authConfig.getClientVersion());
        updateAuthConfig(newConfig);
    }

    public ApiResponse makeRequest(String url) {
        try {
            Request request = createRequestBuilder(url).get().build();
            Response response = httpClient.newCall(request).execute();
            return new ApiResponse(response.code(), response.body().string());
        } catch (IOException e) {
            return new ApiResponse(500, "Error al realizar GET: " + e.getMessage());
        }
    }
    public ApiResponse makePostRequest(String url, String jsonBody) {
        try {
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = createRequestBuilder(url).post(body).build();
            Response response = ApiClient.newCall(request).execute();

            return new ApiResponse(response.code(), response.body().string());

        } catch (IOException e) {
            return new ApiResponse(500, "Error al realizar POST: " + e.getMessage());
        }
    }
    public ApiResponse makePutRequest(String url, String jsonBody) {
        try {
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = createRequestBuilder(url).put(body).build();
            Response response = ApiClient.newCall(request).execute();

            return new ApiResponse(response.code(), response.body().string());

        } catch (IOException e) {
            return new ApiResponse(500, "Error al realizar PUT: " + e.getMessage());
        }
    }
    public ApiResponse makeDeleteRequest(String url) {
        try {
            Request request = createRequestBuilder(url).delete().build();
            Response response = ApiClient.newCall(request).execute();

            return new ApiResponse(response.code(), response.body().string());

        } catch (IOException e) {
            return new ApiResponse(500, "Error al realizar DELETE: " + e.getMessage());
        }
    }
    public ApiResponse makeCustomRequest(String method, String url, String bodyContent) {
        try {
            RequestBody body = null;
            if (bodyContent != null) {
                body = RequestBody.create(bodyContent, MediaType.get("application/json; charset=utf-8"));
            }

            Request.Builder builder = createRequestBuilder(url);

            switch (method) {
                case "GET":
                    builder.get();
                    break;
                case "POST":
                    builder.post(body);
                    break;
                case "PUT":
                    builder.put(body);
                    break;
                case "DELETE":
                    builder.delete(body != null ? body : RequestBody.create("", null));
                    break;
                default:
                    return new ApiResponse(400, "Método HTTP no soportado: " + method);
            }

            Request request = builder.build();
            Response response = ApiClient.newCall(request).execute();
            return new ApiResponse(response.code(), response.body().string());

        } catch (IOException e) {
            return new ApiResponse(500, "Error en método personalizado: " + e.getMessage());
        }
    }

    public String getAuthenticationType() {
        if (authConfig.getBearerToken() != null && authConfig.getApiKey() != null) {
            return "Bearer Token + API Key";
        } else if (authConfig.getBearerToken() != null) {
            return "Bearer Token";
        } else if (authConfig.getApiKey() != null) {
            return "API Key";
        } else {
            return "Sin autenticación";
        }
    }

    public void updateVersion(String version) {
        AuthConfig newConfig = new AuthConfig(authConfig.getBearerToken(), authConfig.getApiKey(), version);
        updateAuthConfig(newConfig);
    }


    public void updateCredentials(String token, String apiKey) {
        AuthConfig newConfig = new AuthConfig(token, apiKey, authConfig.getClientVersion());
        updateAuthConfig(newConfig);
    }


    /**
     * Método de conveniencia para APIs REST comunes
     */
    public static class ApiBuilder {
        private final EnhancedApiClient client;
        private final String baseUrl;

        public ApiBuilder(EnhancedApiClient client, String baseUrl) {
            this.client = client;
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        }

        public ApiResponse getResource(String endpoint) throws IOException, ApiAuthException, ApiRateLimitException {
            return client.get(baseUrl + endpoint);
        }

        public ApiResponse createResource(String endpoint, String jsonData) throws IOException, ApiAuthException, ApiRateLimitException {
            return client.post(baseUrl + endpoint, jsonData);
        }

        public ApiResponse updateResource(String endpoint, String jsonData) throws IOException, ApiAuthException, ApiRateLimitException {
            return client.put(baseUrl + endpoint, jsonData);
        }

        public ApiResponse deleteResource(String endpoint) throws IOException, ApiAuthException, ApiRateLimitException {
            return client.delete(baseUrl + endpoint);
        }
    }

    /**
     * Crea un builder para una API específica
     */
    public ApiBuilder forApi(String baseUrl) {
        return new ApiBuilder(this, baseUrl);
    }

    /**
     * Verifica si la configuración actual es válida
     */
    public boolean hasValidAuth() {
        return (authConfig.getBearerToken() != null && !authConfig.getBearerToken().trim().isEmpty()) ||
                HeaderUtils.isValidApiKey(authConfig.getApiKey());
    }

    /**
     * Obtiene información de configuración actual (de forma segura)
     */
    public String getAuthInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Cliente API Configuración:\n");

        if (authConfig.getBearerToken() != null && !authConfig.getBearerToken().trim().isEmpty()) {
            info.append("  Bearer Token: ").append(HeaderUtils.maskSensitiveHeader("Bearer " + authConfig.getBearerToken())).append("\n");
        }

        if (HeaderUtils.isValidApiKey(authConfig.getApiKey())) {
            info.append("  API Key: ").append(HeaderUtils.maskSensitiveHeader(authConfig.getApiKey())).append("\n");
        }

        if (authConfig.getClientVersion() != null) {
            info.append("  Versión: ").append(authConfig.getClientVersion()).append("\n");
        }

        info.append("  Auth válida: ").append(hasValidAuth() ? "VALIDO" : "INVALIDO");

        return info.toString();
    }

    /**
     * Cierra el cliente y libera recursos
     */
    public void close() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }

    // Getters
    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }
}