package transformer;



import model.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// DataTransformer.java - Responsable de transformar los datos extraídos
public class DataTransformer {
    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MAX_BODY_LENGTH = 200;
    private static final int LONG_TITLE_THRESHOLD = 30;

    /**
     * Aplica transformaciones a la lista de posts
     * @param posts Lista de posts a transformar
     * @return Lista de posts transformados
     */
    public List<Post> transformData(List<Post> posts) {
        System.out.println("Iniciando transformación de datos...");

        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for (Post post : posts) {
            // Transformación 1: Convertir título a mayúsculas
            if (post.getTitle() != null) {
                post.setTitle(post.getTitle().toUpperCase());
            }

            // Transformación 2: Truncar título si es muy largo
            if (post.getTitle() != null && post.getTitle().length() > MAX_TITLE_LENGTH) {
                post.setTitle(post.getTitle().substring(0, MAX_TITLE_LENGTH) + "...");
            }

            // Transformación 3: Truncar cuerpo del post
            if (post.getBody() != null && post.getBody().length() > MAX_BODY_LENGTH) {
                post.setBody(post.getBody().substring(0, MAX_BODY_LENGTH) + "...");
            }

            // Transformación 4: Agregar categoría estática basada en userId
            post.setCategory(assignCategory(post.getUserId()));

            // Transformación 5: Agregar fecha de extracción
            post.setExtractionDate(currentDateTime);

            // Transformación 6: Campo booleano calculado - determinar si el título es largo
            post.setLongTitle(post.getTitle() != null &&
                    post.getTitle().length() > LONG_TITLE_THRESHOLD);

            // Transformación 7: Limpiar caracteres especiales del cuerpo
            if (post.getBody() != null) {
                post.setBody(cleanText(post.getBody()));
            }
        }

        System.out.println("Transformación completada. " + posts.size() + " posts transformados.");
        return posts;
    }

    /**
     * Asigna una categoría basada en el userId
     */
    private String assignCategory(int userId) {
        if (userId <= 3) {
            return "TECNOLOGIA";
        } else if (userId <= 6) {
            return "NEGOCIOS";
        } else if (userId <= 8) {
            return "ENTRETENIMIENTO";
        } else {
            return "GENERAL";
        }
    }

    /**
     * Limpia el texto removiendo caracteres especiales problemáticos
     */
    private String cleanText(String text) {
        if (text == null) return null;

        return text.replaceAll("[\\r\\n]+", " ")  // Reemplazar saltos de línea
                .replaceAll("\\s+", " ")        // Normalizar espacios
                .trim();                        // Eliminar espacios al inicio/fin
    }
}
