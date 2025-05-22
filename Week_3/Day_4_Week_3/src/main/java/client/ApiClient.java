package client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Cliente HTTP para realizar peticiones a APIs con autenticación
 */
public class ApiClient {
    private final OkHttpClient client;
    private final AuthConfig authConfig;

    public ApiClient(AuthConfig authConfig) {
        this.authConfig = authConfig;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public Response executeRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }
}