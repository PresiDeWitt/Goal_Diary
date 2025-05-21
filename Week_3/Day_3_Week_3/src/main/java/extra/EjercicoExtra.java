package extra;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EjercicoExtra {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

    public static void realizarPeticionConGson() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("userId", "1")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Java OkHttp Client")
                .header("X-Curso-Java", "ETL2025")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Error HTTP: " + response.code() + " - " + response.message());
                return;
            }

            System.out.println("Código de estado: " + response.code());
            System.out.println("\nENCABEZADOS DE RESPUESTA:");
            System.out.println("Content-Type: " + response.header("Content-Type"));
            System.out.println("Server: " + response.header("Server"));

            String responseBody = response.body().string();

            Gson gson = new Gson();
            Type postListType = new TypeToken<List<extra.Post>>(){}.getType();
            List<extra.Post> posts = gson.fromJson(responseBody, postListType);

            System.out.println("\nPOSTS OBTENIDOS (" + posts.size() + " posts):");
            for (extra.Post post : posts) {
                System.out.println("\n----- Post ID: " + post.getId() + " -----");
                System.out.println("Usuario: " + post.getUserId());
                System.out.println("Título: " + post.getTitle());
                System.out.println("Contenido: " + post.getBody());
            }

        } catch (IOException e) {
            System.err.println("Error al realizar la petición HTTP: " + e.getMessage());
        }
    }
}
