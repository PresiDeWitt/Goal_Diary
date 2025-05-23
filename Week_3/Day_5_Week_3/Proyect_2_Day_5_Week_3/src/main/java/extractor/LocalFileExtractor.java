// LocalFileExtractor.java - Extractor de archivos locales
package extractor;

import exception.ETLException;
import model.Post;
import util.ETLLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalFileExtractor extends BaseDataExtractor {
    private final String filePath;
    private final ETLLogger logger;

    public LocalFileExtractor(String filePath) {
        super("Local JSON File");
        this.filePath = filePath;
        this.logger = ETLLogger.getInstance();
    }

    @Override
    public List<Post> extractData() throws ETLException {
        try {
            logger.info("Iniciando extracción desde archivo: " + filePath);
            System.out.println("Extrayendo datos desde archivo local: " + filePath);

            Path path = Paths.get(filePath);

            // Crear archivo con datos de ejemplo si no existe
            if (!Files.exists(path)) {
                createSampleFile(path);
            }

            String content = Files.readString(path);
            List<Post> posts = parseJsonContent(content);

            logger.info("Extracción archivo completada. " + posts.size() + " posts obtenidos.");
            System.out.println("Extracción desde archivo completada. " + posts.size() + " posts adicionales.");

            return posts;

        } catch (IOException e) {
            logger.error("Error leyendo archivo: " + e.getMessage());
            throw new ETLException("Error al leer archivo local", e);
        }
    }

    private void createSampleFile(Path path) throws IOException {
        String sampleData = """
            [
              {
                "id": 101,
                "userId": 11,
                "title": "Post adicional desde archivo local",
                "body": "Este es contenido adicional que viene de un archivo JSON local para demostrar múltiples fuentes de datos."
              },
              {
                "id": 102,
                "userId": 12,
                "title": "Segundo post local",
                "body": "Otro ejemplo de post que viene del sistema de archivos local."
              },
              {
                "id": 103,
                "userId": 13,
                "title": "Tercer post de prueba",
                "body": "Un tercer ejemplo para tener más variedad en los datos locales."
              }
            ]
            """;

        Files.createDirectories(path.getParent());
        Files.writeString(path, sampleData);
        System.out.println("Archivo de ejemplo creado: " + path);
    }

    private List<Post> parseJsonContent(String content) throws ETLException {
        List<Post> posts = new ArrayList<>();

        try {
            Pattern postPattern = Pattern.compile("\\{[^{}]*\\}");
            Matcher postMatcher = postPattern.matcher(content);

            while (postMatcher.find()) {
                String postJson = postMatcher.group();
                Post post = parsePost(postJson);
                if (post != null) {
                    posts.add(post);
                }
            }

        } catch (Exception e) {
            logger.error("Error parseando archivo JSON: " + e.getMessage());
            throw new ETLException("Error al parsear contenido del archivo", e);
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
            logger.warning("Error parseando post del archivo: " + e.getMessage());
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
        logger.info("Cerrando extractor de archivo");
    }
}
