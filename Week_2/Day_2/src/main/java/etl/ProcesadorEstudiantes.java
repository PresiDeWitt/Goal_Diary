package etl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que procesa un archivo de estudiantes con notas,
 * calcula estadísticas y genera un archivo de resultados.
 */
public class ProcesadorEstudiantes {

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

    /**
     * Procesa el archivo de entrada y genera un archivo de salida con estadísticas
     *
     * @param archivoEntrada Ruta del archivo de entrada (formato: Nombre;nota1;nota2;...;nota10)
     * @param archivoSalida Ruta del archivo de salida (formato: Nombre;Promedio;Desviación estándar)
     */
    public void procesarArchivo(String archivoEntrada, String archivoSalida) {
        List<Estudiante> estudiantes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoEntrada))) {
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

                // Calcular estadísticas
                double promedio = calcularPromedio(notas);
                double desviacion = calcularDesviacionEstandar(notas, promedio);

                // Crear y agregar estudiante
                Estudiante estudiante = new Estudiante(nombre, promedio, desviacion);
                estudiantes.add(estudiante);

                System.out.println("Procesado: " + nombre + " - Promedio: " +
                        String.format("%.2f", promedio) + " - Desviación: " +
                        String.format("%.2f", desviacion));
            }

            // Escribir resultados en archivo de salida
            escribirResultados(estudiantes, archivoSalida);

        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }

    /**
     * Calcula el promedio de un array de notas
     */
    private double calcularPromedio(double[] notas) {
        if (notas.length == 0) return 0.0;

        double suma = 0.0;
        for (double nota : notas) {
            suma += nota;
        }

        return suma / notas.length;
    }

    /**
     * Calcula la desviación estándar de un array de notas
     */
    private double calcularDesviacionEstandar(double[] notas, double promedio) {
        if (notas.length <= 1) return 0.0;

        double sumaCuadrados = 0.0;
        for (double nota : notas) {
            sumaCuadrados += Math.pow(nota - promedio, 2);
        }

        return Math.sqrt(sumaCuadrados / notas.length);
    }

    /**
     * Escribe los resultados en un archivo de salida
     */
    private void escribirResultados(List<Estudiante> estudiantes, String archivoSalida) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida))) {
            // Escribir encabezado
            writer.write("Nombre;Promedio;Desviación estándar");
            writer.newLine();

            // Escribir datos de cada estudiante
            for (Estudiante estudiante : estudiantes) {
                writer.write(estudiante.toString());
                writer.newLine();
            }

            System.out.println("Archivo de resultados generado exitosamente: " + archivoSalida);

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de resultados: " + e.getMessage());
        }
    }

    /**
     * Método principal para probar la funcionalidad
     */
    public static void main(String[] args) {
        ProcesadorEstudiantes procesador = new ProcesadorEstudiantes();
        procesador.procesarArchivo("estudiantes.txt", "resultados_estudiantes.txt");
    }
}