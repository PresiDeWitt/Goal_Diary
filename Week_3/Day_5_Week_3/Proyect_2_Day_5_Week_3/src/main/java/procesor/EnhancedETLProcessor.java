package procesor;

import exception.ETLException;
import extractor.MultiSourceExtractor;
import loader.EnhancedDataLoader;
import model.Post;
import processor.ETLProcessor;
import transformer.DataTransformer;
import util.ETLLogger;

import java.util.List;

public class EnhancedETLProcessor extends ETLProcessor {
    private final MultiSourceExtractor extractor;
    private final DataTransformer transformer;
    private final EnhancedDataLoader loader;
    private final ETLLogger logger;

    public EnhancedETLProcessor() {
        this.extractor = new MultiSourceExtractor();
        this.transformer = new DataTransformer();
        this.loader = new EnhancedDataLoader();
        this.logger = ETLLogger.getInstance();
    }

    @Override
    public void executeETL() {
        System.out.println("==========================================");
        System.out.println("INICIANDO PROCESO ETL MEJORADO");
        System.out.println("==========================================");

        logger.info("Iniciando proceso ETL mejorado");
        long startTime = System.currentTimeMillis();

        try {
            // Inicializar base de datos
            loader.initialize();

            // EXTRACT: Extraer datos de múltiples fuentes
            System.out.println("\n1. EXTRACCIÓN MULTI-FUENTE (Extract)");
            System.out.println("------------------------------------");
            List<Post> rawPosts = extractor.extractData();

            if (rawPosts.isEmpty()) {
                System.out.println("No se obtuvieron datos. Proceso terminado.");
                logger.warning("No se obtuvieron datos de ninguna fuente");
                return;
            }

            // TRANSFORM: Transformar los datos
            System.out.println("\n2. TRANSFORMACIÓN (Transform)");
            System.out.println("-----------------------------");
            List<Post> transformedPosts = transformer.transformData(rawPosts);

            // LOAD: Carga incremental en la base de datos
            System.out.println("\n3. CARGA INCREMENTAL (Load)");
            System.out.println("---------------------------");
            loader.loadDataIncremental(transformedPosts, "Multi-Source");

            // Validar y mostrar resultados
            System.out.println("\n4. VALIDACIÓN Y RESULTADOS");
            System.out.println("--------------------------");
            loader.validateData();
            loader.showSampleData(5);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            String successMessage = String.format("PROCESO ETL MEJORADO COMPLETADO EXITOSAMENTE en %d ms", duration);
            System.out.println("\n==========================================");
            System.out.println(successMessage);
            System.out.println("==========================================");

            logger.info(successMessage);

        } catch (ETLException e) {
            String errorMessage = "ERROR EN PROCESO ETL MEJORADO: " + e.getMessage();
            System.err.println(errorMessage);
            logger.error(errorMessage, e);

            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
        } finally {
            // Cerrar recursos
            extractor.close();
            loader.close();
            logger.close();
        }
    }
}
