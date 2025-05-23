package loader;

import exception.ETLException;
import model.Post;

import java.sql.*;
import java.util.List;

// DataLoader.java - Responsable de cargar datos en la base de datos
public class DataLoader {
    private static final String DB_URL = "jdbc:sqlite:etl_database.db";
    protected Connection connection; // Changed to protected for inheritance

    /**
     * Inicializa la conexión a la base de datos y crea las tablas necesarias
     */
    public void initialize() throws ETLException {
        try {
            // Registrar el driver de SQLite
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(false); // Usar transacciones

            createTable();
            System.out.println("Base de datos inicializada correctamente.");

        } catch (ClassNotFoundException e) {
            throw new ETLException("Driver SQLite no encontrado", e);
        } catch (SQLException e) {
            throw new ETLException("Error al conectar con la base de datos", e);
        }
    }

    /**
     * Crea la tabla posts si no existe
     */
    private void createTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS posts (
                id INTEGER PRIMARY KEY,
                user_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                body TEXT,
                category TEXT,
                extraction_date TEXT,
                is_long_title BOOLEAN,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Tabla 'posts' verificada/creada.");
        }
    }

    /**
     * Carga los datos transformados en la base de datos
     */
    public void loadData(List<Post> posts) throws ETLException {
        if (posts == null || posts.isEmpty()) {
            System.out.println("No hay datos para cargar.");
            return;
        }

        System.out.println("Iniciando carga de datos...");

        // Limpiar datos existentes
        clearExistingData();

        String insertSQL = """
            INSERT INTO posts (id, user_id, title, body, category, extraction_date, is_long_title)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            int batchSize = 0;
            for (Post post : posts) {
                pstmt.setInt(1, post.getId());
                pstmt.setInt(2, post.getUserId());
                pstmt.setString(3, post.getTitle());
                pstmt.setString(4, post.getBody());
                pstmt.setString(5, post.getCategory());
                pstmt.setString(6, post.getExtractionDate());
                pstmt.setBoolean(7, post.isLongTitle());

                pstmt.addBatch();
                batchSize++;

                // Ejecutar lote cada 50 registros
                if (batchSize % 50 == 0) {
                    pstmt.executeBatch();
                }
            }

            // Ejecutar registros restantes
            pstmt.executeBatch();
            connection.commit();

            System.out.println("Carga completada. " + posts.size() + " posts insertados.");

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error en rollback: " + rollbackEx.getMessage());
            }
            throw new ETLException("Error al insertar datos", e);
        }
    }

    /**
     * Limpia los datos existentes en la tabla
     */
    private void clearExistingData() throws ETLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM posts");
            System.out.println("Datos existentes eliminados.");
        } catch (SQLException e) {
            throw new ETLException("Error al limpiar datos existentes", e);
        }
    }

    /**
     * Verifica que los datos se hayan cargado correctamente
     */
    public void validateData() throws ETLException {
        String countSQL = "SELECT COUNT(*), AVG(LENGTH(title)), category, COUNT(*) as cat_count " +
                "FROM posts GROUP BY category";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM posts")) {

            if (rs.next()) {
                int totalRecords = rs.getInt("total");
                System.out.println("Validación: " + totalRecords + " registros en la base de datos.");
            }

            // Mostrar estadísticas por categoría
            try (ResultSet categoryRs = stmt.executeQuery(countSQL)) {
                System.out.println("Distribución por categoría:");
                while (categoryRs.next()) {
                    System.out.printf("- %s: %d posts (promedio título: %.1f caracteres)%n",
                            categoryRs.getString("category"),
                            categoryRs.getInt("cat_count"),
                            categoryRs.getDouble(2));
                }
            }

        } catch (SQLException e) {
            throw new ETLException("Error al validar datos", e);
        }
    }

    /**
     * Muestra una muestra de los datos cargados
     */
    public void showSampleData(int limit) throws ETLException {
        String sampleSQL = "SELECT * FROM posts LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sampleSQL)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n=== MUESTRA DE DATOS CARGADOS ===");
                while (rs.next()) {
                    System.out.printf("ID: %d | Usuario: %d | Categoría: %s%n",
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("category"));
                    System.out.printf("Título: %s%n",
                            rs.getString("title"));
                    System.out.printf("Título largo: %s | Fecha: %s%n",
                            rs.getBoolean("is_long_title") ? "Sí" : "No",
                            rs.getString("extraction_date"));
                    System.out.println("---");
                }
            }
        } catch (SQLException e) {
            throw new ETLException("Error al mostrar datos de muestra", e);
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a base de datos cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Protected getter for subclasses
    protected Connection getConnection() {
        return connection;
    }
}