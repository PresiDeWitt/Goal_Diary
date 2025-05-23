// EnhancedDataExtractor.java - Extractor de API mejorado con logging
package extractor;

import exception.ETLException;
import model.Post;
import util.ETLLogger;

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

public class EnhancedDataExtractor extends BaseDataExtractor {
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";
    private final HttpClient httpClient;
    private final ETLLogger logger;
    public EnhancedDataExtractor() {
        super("JSONPlaceholder API");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.logger = ETLLogger.getInstance();
    }

    @Override
    public List<Post> extractData() throws ETLException {
        try {
            logger.info("Iniciando extracción desde: " + API_URL);
            System.out.println("Iniciando extracción de datos desde: " + API_URL);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorMsg = "Error HTTP: " + response.statusCode();
                logger.error(errorMsg);
                throw new ETLException(errorMsg);
            }

            List<Post> posts = parseJsonResponse(response.body());
            logger.info("Extracción API completada. " + posts.size() + " posts obtenidos.");
            System.out.println("Extracción completada. " + posts.size() + " posts obtenidos.");

            return posts;

        } catch (IOException | InterruptedException e) {
            logger.error("Error de conectividad: " + e.getMessage());
            throw new ETLException("Error de conectividad al extraer datos", e);
        }
    }

    private List<Post> parseJsonResponse(String jsonResponse) throws ETLException {
        List<Post> posts = new ArrayList<>();

        try {
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
            logger.error("Error parseando JSON: " + e.getMessage());
            throw new ETLException("Error al parsear la respuesta JSON", e);
        }

        return posts;
    }

    private Post parsePost(String postJson) {
        try {
            Post post = new Post();
            post.setId(extractIntField(postJson, "id"));
            post.setUserId(extractIntField(postJson, "userId"));
            post.setTitle(extractStringField(postJson, "title"));
            post.setBody(extractStringField(postJson, "body"));
            return post;
        } catch (Exception e) {
            logger.warning("Error parseando post: " + e.getMessage());
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

    @Override
    public void close() {
        logger.info("Cerrando extractor de API");
    }
}
