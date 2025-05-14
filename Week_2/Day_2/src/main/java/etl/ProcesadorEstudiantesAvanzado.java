package etl;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;

/**
 * Clase que procesa un archivo de estudiantes con notas, calcula estadísticas,
 * genera un archivo de resultados y persiste los datos en una base de datos.
 */
public class ProcesadorEstudiantesAvanzado {

    private static class Estudiante {
        private String nombre;
        private double promedio;
        private double desviacionEstandar;

        public Estudiante(String nombre, double promedio, double desviacionEstandar) {
            this.nombre = nombre;
            this.promedio = promedio;
            this.desviacionEstandar = desviacionEstandar;
        }

        @Override
        public String toString() {
            return nombre + ";" + String.format("%.2f", promedio) + ";" + String.format("%.2f", desviacionEstandar);
        }
    }

    public void procesarArchivoConPersistencia(String archivoEntrada, String archivoSalida) {

        List<Estudiante> estudiantes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                // Procesar cada línea del archivo
                String[] partes = linea.split(";");

                if (partes.length < 2) {
                    System.out.println("Línea con formato incorrecto: " + linea);
                    continue;
                }

                String nombre = partes[0];
                double[] notas = new double[partes.length - 1];

                // Convertir y almacenar las notas
                for (int i = 1; i < partes.length; i++) {
                    try {
                        notas[i - 1] = Double.parseDouble(partes[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir nota para " + nombre + ": " + partes[i]);
                        notas[i - 1] = 0.0; // Valor por defecto
                    }
                }

                // Calcular promedio y desviación estándar
                double promedio = calcularPromedio(notas);
                double desviacionEstandar = calcularDesviacionEstandar(notas, promedio);

                Estudiante estudiante = new Estudiante(nombre, promedio, desviacionEstandar);
                estudiantes.add(estudiante);
            }

            // Escribir resultados en el archivo de salida
            for (Estudiante estudiante : estudiantes) {
                writer.write(estudiante.toString());
                writer.newLine();
            }

            // Persistir resultados en la base de datos
            int guardados = persistirResultados(estudiantes);
            System.out.println("Datos persistidos en la base de datos: " + guardados + " registros");

        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }

    private double calcularDesviacionEstandar(double[] notas, double promedio) {
        double suma = 0.0;
        for (double nota : notas) {
            suma += Math.pow(nota - promedio, 2);
        }
        return Math.sqrt(suma / notas.length);
    }

    private double calcularPromedio(double[] notas) {
        if (notas.length == 0) return 0.0;

        double suma = 0.0;
        for (double nota : notas) {
            suma += nota;
        }

        return suma / notas.length;
    }
    /**
     * Persiste los resultados en la base de datos
     * @return Número de registros guardados exitosamente
     */
    private int persistirResultados(List<Estudiante> estudiantes) {
        int guardados = 0;

        // Crear la tabla si no existe
        crearTablaEstudiantes();

        // Insertar los datos usando DatabaseConnection
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO resultados_estudiantes(nombre, promedio, desviacion) VALUES(?, ?, ?)")) {

            conn.setAutoCommit(false);

            for (Estudiante estudiante : estudiantes) {
                stmt.setString(1, estudiante.nombre);
                stmt.setDouble(2, estudiante.promedio);
                stmt.setDouble(3, estudiante.desviacionEstandar);

                stmt.addBatch();
            }

            int[] resultados = stmt.executeBatch();
            conn.commit();

            for (int i : resultados) {
                if (i > 0) guardados++;
            }

            System.out.println("Datos persistidos en la base de datos: " + guardados + " registros");

        } catch (SQLException e) {
            System.err.println("Error al persistir en la base de datos: " + e.getMessage());
        }

        return guardados;
    }

    /**
     * Crea la tabla resultados_estudiantes si no existe
     */
    private void crearTablaEstudiantes() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS resultados_estudiantes ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(100) NOT NULL,"
                    + "promedio DOUBLE NOT NULL,"
                    + "desviacion DOUBLE NOT NULL"
                    + ")";

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("Error al crear la tabla: " + e.getMessage());
        }
    }

}