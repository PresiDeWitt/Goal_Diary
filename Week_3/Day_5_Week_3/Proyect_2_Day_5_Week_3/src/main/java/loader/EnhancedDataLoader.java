package loader;

import exception.ETLException;
import model.Post;
import util.ETLLogger;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnhancedDataLoader extends DataLoader {
    private final ETLLogger logger;

    public EnhancedDataLoader() {
        super();
        this.logger = ETLLogger.getInstance();
    }

    @Override
    public void initialize() throws ETLException {
        try {
            super.initialize();
            createProcessingTable();
            logger.info("Base de datos mejorada inicializada correctamente");
        } catch (ETLException e) {
            logger.error("Error inicializando base de datos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Crea tabla para tracking de procesamiento
     */
    private void createProcessingTable() throws ETLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS etl_processing_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                post_id INTEGER NOT NULL,
                source_name TEXT,
                processed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(post_id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            logger.info("Tabla de tracking creada/verificada");
        } catch (SQLException e) {
            throw new ETLException("Error creando tabla de tracking", e);
        }
    }

    /**
     * Carga datos con persistencia incremental
     */
    public void loadDataIncremental(List<Post> posts, String sourceName) throws ETLException {
        if (posts == null || posts.isEmpty()) {
            System.out.println("No hay datos para cargar.");
            return;
        }

        logger.info("Iniciando carga incremental de " + posts.size() + " posts desde " + sourceName);
        System.out.println("Iniciando carga incremental...");

        // Obtener IDs ya procesados
        Set<Integer> existingIds = getExistingPostIds();

        // Filtrar posts nuevos
        List<Post> newPosts = posts.stream()
                .filter(post -> !existingIds.contains(post.getId()))
                .toList();

        if (newPosts.isEmpty()) {
            System.out.println("No hay posts nuevos para insertar. Todos ya existen.");
            logger.info("No hay posts nuevos - todos ya procesados");
            return;
        }

        System.out.println("Posts nuevos a insertar: " + newPosts.size() +
                " (se omitieron " + (posts.size() - newPosts.size()) + " duplicados)");

        String insertPostSQL = """
            INSERT INTO posts (id, user_id, title, body, category, extraction_date, is_long_title)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        String insertLogSQL = """
            INSERT OR IGNORE INTO etl_processing_log (post_id, source_name)
            VALUES (?, ?)
        """;

        try (PreparedStatement postStmt = connection.prepareStatement(insertPostSQL);
             PreparedStatement logStmt = connection.prepareStatement(insertLogSQL)) {

            int batchSize = 0;
            for (Post post : newPosts) {
                // Insertar post
                postStmt.setInt(1, post.getId());
                postStmt.setInt(2, post.getUserId());
                postStmt.setString(3, post.getTitle());
                postStmt.setString(4, post.getBody());
                postStmt.setString(5, post.getCategory());
                postStmt.setString(6, post.getExtractionDate());
                postStmt.setBoolean(7, post.isLongTitle());
                postStmt.addBatch();

                // Insertar log
                logStmt.setInt(1, post.getId());
                logStmt.setString(2, sourceName);
                logStmt.addBatch();

                batchSize++;

                if (batchSize % 50 == 0) {
                    postStmt.executeBatch();
                    logStmt.executeBatch();
                }
            }

            postStmt.executeBatch();
            logStmt.executeBatch();
            connection.commit();

            String message = "Carga incremental completada. " + newPosts.size() + " posts nuevos insertados.";
            System.out.println(message);
            logger.info(message);

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Error en rollback: " + rollbackEx.getMessage());
            }
            logger.error("Error en carga incremental: " + e.getMessage());
            throw new ETLException("Error al insertar datos incrementalmente", e);
        }
    }

    private Set<Integer> getExistingPostIds() throws ETLException {
        Set<Integer> existingIds = new HashSet<>();
        String query = "SELECT post_id FROM etl_processing_log";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                existingIds.add(rs.getInt("post_id"));
            }

        } catch (SQLException e) {
            throw new ETLException("Error obteniendo IDs existentes", e);
        }

        return existingIds;
    }

    @Override
    public void validateData() throws ETLException {
        try {
            super.validateData();

            // Validaciones adicionales
            String sourceStatsSQL = """
                SELECT source_name, COUNT(*) as count, 
                       MIN(processed_at) as first_processed,
                       MAX(processed_at) as last_processed
                FROM etl_processing_log 
                GROUP BY source_name
            """;

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sourceStatsSQL)) {

                System.out.println("\n=== ESTADÍSTICAS POR FUENTE ===");
                while (rs.next()) {
                    System.out.printf("Fuente: %s | Posts: %d | Primero: %s | Último: %s%n",
                            rs.getString("source_name"),
                            rs.getInt("count"),
                            rs.getString("first_processed"),
                            rs.getString("last_processed"));
                }
            }

            logger.info("Validación extendida completada");

        } catch (SQLException e) {
            logger.error("Error en validación extendida: " + e.getMessage());
            throw new ETLException("Error en validación extendida", e);
        }
    }
}