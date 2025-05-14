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

    static class Estudiante {
        private String nombre;
        private List<Double> notas;
        private double promedio;
        private double desviacionEstandar;

        public Estudiante(String nombre) {
            this.nombre = nombre;
            this.notas = new ArrayList<>();
        }

        public void agregarNota(double nota) {
            notas.add(nota);
        }

        public String getNombre() {
            return nombre;
        }

        public List<Double> getNotas() {
            return notas;
        }

        public double getPromedio() {
            return promedio;
        }

        public double getDesviacionEstandar() {
            return desviacionEstandar;
        }

        /**
         * Calcula el promedio de las notas del estudiante
         */
        public void calcularPromedio() {
            if (notas.isEmpty()) {
                promedio = 0.0;
                return;
            }

            double suma = 0.0;
            for (Double nota : notas) {
                suma += nota;
            }
            promedio = suma / notas.size();
        }

        /**
         * Calcula la desviación estándar de las notas del estudiante
         * fórmula: sqrt(sum((xi - promedio)^2) / n)
         */
        public void calcularDesviacionEstandar() {
            if (notas.size() <= 1) {
                desviacionEstandar = 0.0;
                return;
            }

            // Primero calculamos el promedio si no ha sido calculado
            if (promedio == 0.0) {
                calcularPromedio();
            }

            double sumaDiferenciasCuadradas = 0.0;
            for (Double nota : notas) {
                double diferencia = nota - promedio;
                sumaDiferenciasCuadradas += Math.pow(diferencia, 2);
            }

            desviacionEstandar = Math.sqrt(sumaDiferenciasCuadradas / notas.size());
        }

        @Override
        public String toString() {
            return nombre + ";" + String.format("%.2f", promedio) + ";" + String.format("%.2f", desviacionEstandar);
        }
    }

    /**
     * Lee el archivo de entrada, procesa los datos y escribe los resultados en el archivo de salida
     *
     * @param archivoEntrada Ruta del archivo de entrada
     * @param archivoSalida Ruta del archivo de salida
     * @throws IOException Si ocurre un error al leer o escribir los archivos
     */
    public static void procesarArchivo(String archivoEntrada, String archivoSalida) throws IOException {
        List<Estudiante> estudiantes = leerArchivoEstudiantes(archivoEntrada);

        // Calcular estadísticas para cada estudiante
        for (Estudiante estudiante : estudiantes) {
            estudiante.calcularPromedio();
            estudiante.calcularDesviacionEstandar();
        }

        // Escribir resultados en el archivo de salida
        escribirArchivoResultados(estudiantes, archivoSalida);

        // Mostrar resultados en consola
        mostrarResultados(estudiantes);
    }

    /**
     * Lee el archivo de entrada y crea una lista de objetos Estudiante
     * Implementación con String.split() en lugar de StringTokenizer
     *
     * @param archivoEntrada Ruta del archivo de entrada
     * @return Lista de estudiantes con sus notas
     * @throws IOException Si ocurre un error al leer el archivo
     */
    private static List<Estudiante> leerArchivoEstudiantes(String archivoEntrada) throws IOException {
        List<Estudiante> estudiantes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue; // Ignorar líneas vacías
                }

                // Usando String.split() para separar los datos
                String[] datos = linea.split(";");

                if (datos.length >= 1) {
                    String nombre = datos[0].trim();
                    Estudiante estudiante = new Estudiante(nombre);

                    // Leer todas las notas (empezando desde el índice 1)
                    for (int i = 1; i < datos.length; i++) {
                        try {
                            double nota = Double.parseDouble(datos[i].trim());
                            estudiante.agregarNota(nota);
                        } catch (NumberFormatException e) {
                            System.err.println("Error al convertir nota para estudiante " + nombre + ": " + e.getMessage());
                        }
                    }

                    estudiantes.add(estudiante);
                }
            }
        }

        return estudiantes;
    }

    /**
     * Escribe los resultados en el archivo de salida
     *
     * @param estudiantes Lista de estudiantes con sus estadísticas calculadas
     * @param archivoSalida Ruta del archivo de salida
     * @throws IOException Si ocurre un error al escribir el archivo
     */
    private static void escribirArchivoResultados(List<Estudiante> estudiantes, String archivoSalida) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoSalida))) {
            // Escribir encabezado
            pw.println("Nombre;Promedio;Desviación Estándar");

            // Escribir datos de cada estudiante
            for (Estudiante estudiante : estudiantes) {
                pw.println(estudiante.toString());
            }
        }
    }

    /**
     * Muestra los resultados en la consola
     *
     * @param estudiantes Lista de estudiantes con sus estadísticas calculadas
     */
    private static void mostrarResultados(List<Estudiante> estudiantes) {
        System.out.println("==== RESULTADOS DEL PROCESAMIENTO (usando String.split) ====");
        System.out.println("Nombre | Promedio | Desviación Estándar");
        System.out.println("-----------------------------------------");

        for (Estudiante estudiante : estudiantes) {
            System.out.printf("%-20s | %-8.2f | %-8.2f%n",
                    estudiante.getNombre(),
                    estudiante.getPromedio(),
                    estudiante.getDesviacionEstandar());
        }

        System.out.println("-----------------------------------------");
        System.out.println("Total de estudiantes procesados: " + estudiantes.size());
    }

    /**
     * Método principal para probar la funcionalidad
     */
    public static void main(String[] args) {
        String archivoEntrada = "datos_estudiantes.txt";
        String archivoSalida = "resultados_estudiantes_split.txt";

        try {
            procesarArchivo(archivoEntrada, archivoSalida);
            System.out.println("\nEl archivo de resultados ha sido generado exitosamente: " + archivoSalida);
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}