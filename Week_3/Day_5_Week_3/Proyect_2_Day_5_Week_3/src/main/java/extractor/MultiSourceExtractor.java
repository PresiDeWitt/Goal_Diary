package extractor;

import exception.ETLException;
import model.Post;
import util.ETLLogger;

import java.util.ArrayList;
import java.util.List;

public class MultiSourceExtractor extends BaseDataExtractor {
    private final List<BaseDataExtractor> extractors;
    private final ETLLogger logger;

    public MultiSourceExtractor() {
        super("Multi-Source");
        this.extractors = new ArrayList<>();
        this.logger = ETLLogger.getInstance();

        // Agregar las fuentes de datos
        extractors.add(new EnhancedDataExtractor());
        extractors.add(new extractor.LocalFileExtractor("data/additional_posts.json"));
    }

    @Override
    public List<Post> extractData() throws ETLException {
        List<Post> allPosts = new ArrayList<>();

        logger.info("Iniciando extracción multi-fuente de " + extractors.size() + " fuentes");
        System.out.println("=== EXTRACCIÓN MULTI-FUENTE ===");

        for (BaseDataExtractor extractor : extractors) {
            try {
                System.out.println("Procesando fuente: " + extractor.getSourceName());
                List<Post> posts = extractor.extractData();
                allPosts.addAll(posts);

                logger.info("Fuente " + extractor.getSourceName() + ": " + posts.size() + " posts");

            } catch (ETLException e) {
                logger.error("Error en fuente " + extractor.getSourceName() + ": " + e.getMessage());
                System.err.println("Advertencia: Error en " + extractor.getSourceName() +
                        ", continuando con otras fuentes...");
            }
        }

        System.out.println("Total combinado: " + allPosts.size() + " posts de todas las fuentes");
        logger.info("Extracción multi-fuente completada. Total: " + allPosts.size() + " posts");

        return allPosts;
    }

    @Override
    public void close() {
        for (BaseDataExtractor extractor : extractors) {
            extractor.close();
        }
        logger.info("Todos los extractores cerrados");
    }
}
