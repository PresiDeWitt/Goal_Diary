package processor;


import exception.ETLException;
import extractor.DataExtractor;
import loader.DataLoader;
import model.Post;
import transformer.DataTransformer;

import java.util.List;

// ETLProcessor.java - Orquestador principal del proceso ETL
public class ETLProcessor {
    private final DataExtractor extractor;
    private final DataTransformer transformer;
    private final DataLoader loader;

    public ETLProcessor() {
        this.extractor = new DataExtractor();
        this.transformer = new DataTransformer();
        this.loader = new DataLoader();
    }

    /**
     * Ejecuta el proceso ETL completo
     */
    public void executeETL() {
        System.out.println("==========================================");
        System.out.println("INICIANDO PROCESO ETL");
        System.out.println("==========================================");

        long startTime = System.currentTimeMillis();

        try {
            // Inicializar base de datos
            loader.initialize();

            // EXTRACT: Extraer datos de la API
            System.out.println("\n1. EXTRACCIÓN (Extract)");
            System.out.println("-----------------------");
            List<Post> rawPosts = extractor.extractData();

            if (rawPosts.isEmpty()) {
                System.out.println("No se obtuvieron datos de la API. Proceso terminado.");
                return;
            }

            // TRANSFORM: Transformar los datos
            System.out.println("\n2. TRANSFORMACIÓN (Transform)");
            System.out.println("-----------------------------");
            List<Post> transformedPosts = transformer.transformData(rawPosts);

            // LOAD: Cargar datos en la base de datos
            System.out.println("\n3. CARGA (Load)");
            System.out.println("----------------");
            loader.loadData(transformedPosts);

            // Validar y mostrar resultados
            System.out.println("\n4. VALIDACIÓN Y RESULTADOS");
            System.out.println("--------------------------");
            loader.validateData();
            loader.showSampleData(5);

            long endTime = System.currentTimeMillis();
            System.out.println("\n==========================================");
            System.out.printf("PROCESO ETL COMPLETADO EXITOSAMENTE en %d ms%n",
                    (endTime - startTime));
            System.out.println("==========================================");

        } catch (ETLException e) {
            System.err.println("ERROR EN PROCESO ETL: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
        } finally {
            // Cerrar recursos
            extractor.close();
            loader.close();
        }
    }
}