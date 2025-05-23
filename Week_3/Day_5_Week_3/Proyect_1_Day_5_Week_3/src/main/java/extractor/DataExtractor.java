package extractor;

import exception.ETLException;
import model.Post;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// DataExtractor.java - Responsable de extraer datos de la API
public class DataExtractor {
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";
    private final HttpClient httpClient;

    public DataExtractor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Extrae datos de la API JSONPlaceholder
     * @return Lista de objetos Post extraídos de la API
     * @throws ETLException Si hay problemas en la extracción
     */
    public List<Post> extractData() throws ETLException {
        try {
            System.out.println("Iniciando extracción de datos desde: " + API_URL);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ETLException("Error HTTP: " + response.statusCode() +
                        " al consultar la API");
            }

            List<Post> posts = parseJsonResponse(response.body());
            System.out.println("Extracción completada. " + posts.size() + " posts obtenidos.");

            return posts;

        } catch (IOException | InterruptedException e) {
            throw new ETLException("Error de conectividad al extraer datos", e);
        }
    }

    /**
     * Parsea la respuesta JSON de manera simple (sin librerías externas)
     */
    private List<Post> parseJsonResponse(String jsonResponse) throws ETLException {
        List<Post> posts = new ArrayList<>();

        try {
            // Patrón para extraer objetos JSON individuales
            Pattern postPattern = Pattern.compile("\\{[^{}]*\\}");
            Matcher postMatcher = postPattern.matcher(jsonResponse);

            while (postMatcher.find()) {
                String postJson = postMatcher.group();
                Post post = parsePost(postJson);
                if (post != null) {
                    posts.add(post);
                }
            }

        } catch (Exception e) {
            throw new ETLException("Error al parsear la respuesta JSON", e);
        }

        return posts;
    }

    /**
     * Parsea un post individual desde JSON
     */
    private Post parsePost(String postJson) {
        try {
            Post post = new Post();

            // Extraer campos usando expresiones regulares simples
            post.setId(extractIntField(postJson, "id"));
            post.setUserId(extractIntField(postJson, "userId"));
            post.setTitle(extractStringField(postJson, "title"));
            post.setBody(extractStringField(postJson, "body"));

            return post;

        } catch (Exception e) {
            System.err.println("Error parseando post: " + e.getMessage());
            return null;
        }
    }

    private int extractIntField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private String extractStringField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }

    public void close() {
        // HttpClient se cierra automáticamente
    }
}
