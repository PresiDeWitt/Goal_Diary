package test;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnectionTest {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Intento de conexión
            System.out.println("Conectando a MySQL...");
            conn = DatabaseConnection.getConnection("mysql_destino");
            if (conn != null) {
                System.out.println("Conexión exitosa a MySQL");

                // Obtener el nombre de la base de datos actual
                String dbName = conn.getCatalog();
                System.out.println("\nBase de datos actual: " + dbName);

                // Listar todas las tablas en la base de datos actual
                System.out.println("\nListando tablas disponibles:");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                    boolean foundTables = false;
                    while (rs.next()) {
                        foundTables = true;
                        System.out.println(rs.getString(1));
                    }
                    if (!foundTables) {
                        System.out.println("No se encontraron tablas en la base de datos");
                    }
                }

                // Intentar consultar la estructura de la tabla
                System.out.println("\nBuscando tabla USUARIOS o variaciones:");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SHOW TABLES LIKE '%usuario%'")) {
                    boolean foundUsuarios = false;
                    while (rs.next()) {
                        foundUsuarios = true;
                        String tableName = rs.getString(1);
                        System.out.println("Tabla encontrada: " + tableName);

                        // Mostrar columnas de la tabla
                        System.out.println("  Columnas:");
                        try (Statement colStmt = conn.createStatement();
                             ResultSet colRs = colStmt.executeQuery(
                                     "SHOW COLUMNS FROM " + tableName)) {
                            while (colRs.next()) {
                                System.out.println("    " + colRs.getString("Field") + " (" +
                                        colRs.getString("Type") + ")");
                            }
                        }
                    }
                    if (!foundUsuarios) {
                        System.out.println("No se encontró ninguna tabla con 'usuario' en el nombre");
                    }
                }

            } else {
                System.out.println("No se pudo establecer la conexión a la base de datos MySQL.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar o consultar la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseConnection.closeConnection(conn);
            }
        }
    }
}