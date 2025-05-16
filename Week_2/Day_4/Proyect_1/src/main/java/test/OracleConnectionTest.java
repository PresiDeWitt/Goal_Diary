package test;

import config.DatabaseConfig;
import services.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleConnectionTest {
    public static void main(String[] args) {
        DatabaseConfig configOracle = new DatabaseConfig("oracle");

        Connection conn = null;
        try {
            // Intento de conexión
            System.out.println("Conectando a Oracle...");
            conn = DatabaseManager.conectar(configOracle);
            System.out.println("Conexión exitosa a Oracle");

            // Listar todas las tablas accesibles
            System.out.println("\nListando tablas disponibles:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT owner, table_name FROM all_tables WHERE owner = 'CIMDATA'")) {
                boolean foundTables = false;
                while (rs.next()) {
                    foundTables = true;
                    System.out.println(rs.getString("owner") + "." + rs.getString("table_name"));
                }
                if (!foundTables) {
                    System.out.println("No se encontraron tablas para el usuario CIMDATA");
                }
            }

            // Intentar consultar la estructura de la tabla
            System.out.println("\nBuscando tabla USUARIOS o variaciones:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT table_name FROM all_tables WHERE owner = 'CIMDATA' AND UPPER(table_name) LIKE '%USUARIO%'")) {
                boolean foundUsuarios = false;
                while (rs.next()) {
                    foundUsuarios = true;
                    String tableName = rs.getString("table_name");
                    System.out.println("Tabla encontrada: " + tableName);

                    // Mostrar columnas de la tabla
                    System.out.println("  Columnas:");
                    try (Statement colStmt = conn.createStatement();
                         ResultSet colRs = colStmt.executeQuery(
                                 "SELECT column_name, data_type FROM all_tab_columns " +
                                         "WHERE owner = 'CIMDATA' AND table_name = '" + tableName + "'")) {
                        while (colRs.next()) {
                            System.out.println("    " + colRs.getString("column_name") + " (" +
                                    colRs.getString("data_type") + ")");
                        }
                    }
                }
                if (!foundUsuarios) {
                    System.out.println("No se encontró ninguna tabla con 'USUARIO' en el nombre");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar o consultar la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseManager.cerrarConexion(conn);
            }
        }
    }
}