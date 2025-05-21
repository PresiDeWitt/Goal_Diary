package services;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpRequester {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

    public static Response hacerPeticion() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("userId", "1")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Java OkHttp Client")
                .header("X-Curso-Java", "ETL2025")
                .build();

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            System.err.println("Error al realizar la petición HTTP: " + e.getMessage());
            return null;
        }
    }
}
