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
     * También persiste los datos en la base de datos
     *
     * @param archivoEntrada Ruta del archivo de entrada
     * @param archivoSalida Ruta del archivo de salida
     */
    public void procesarArchivo(String archivoEntrada,String archivoSalida) {
        try {
            List<Estudiante> estudiantes = leerArchivoEstudiantes(archivoEntrada);

            // Calcular estadísticas para cada estudiante
            for (Estudiante estudiante : estudiantes) {
                estudiante.calcularPromedio();
                estudiante.calcularDesviacionEstandar();
            }

            // Escribir resultados en el archivo de salida
            escribirArchivoResultados(estudiantes, archivoSalida);

            // Persistir datos en la base de datos
            persistirEstudiantesEnBD(estudiantes);

            // Mostrar resultados en consola
            mostrarResultados(estudiantes);

            System.out.println("\nEl archivo de resultados ha sido generado exitosamente: " + archivoSalida);
            System.out.println("Los datos han sido guardados en la base de datos.");

        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lee el archivo de entrada y crea una lista de objetos Estudiante
     * Implementación con String.split() en lugar de StringTokenizer
     *
     * @param archivoEntrada Ruta del archivo de entrada
     * @return Lista de estudiantes con sus notas
     * @throws IOException Si ocurre un error al leer el archivo
     */
    private List<Estudiante> leerArchivoEstudiantes(String archivoEntrada) throws IOException {
        List<Estudiante> estudiantes = new ArrayList<>();

        File archivo = new File(archivoEntrada);
        if (!archivo.exists()) {
            throw new IOException("El archivo de entrada no existe: " + archivoEntrada);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;
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
                            System.err.println("Error en línea " + numeroLinea + ": No se pudo convertir la nota para el estudiante " + nombre + " - " + datos[i]);
                        }
                    }

                    estudiantes.add(estudiante);
                } else {
                    System.err.println("Error en línea " + numeroLinea + ": Formato incorrecto - " + linea);
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
    private void escribirArchivoResultados(List<Estudiante> estudiantes, String archivoSalida) throws IOException {
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
     * Persiste los datos de los estudiantes en la base de datos
     *
     * @param estudiantes Lista de estudiantes a persistir
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     */
    private void persistirEstudiantesEnBD(List<Estudiante> estudiantes) throws SQLException {
        // Obtener conexión a la base de datos
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si existe la tabla, si no, crearla
            crearTablaEstudiantesSiNoExiste(conn);

            // Preparar la sentencia SQL para insertar estudiantes
            String sql = "INSERT INTO estudiantes (nombre, promedio, desviacion_estandar) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int registrosInsertados = 0;

                for (Estudiante estudiante : estudiantes) {
                    pstmt.setString(1, estudiante.getNombre());
                    pstmt.setDouble(2, estudiante.getPromedio());
                    pstmt.setDouble(3, estudiante.getDesviacionEstandar());

                    registrosInsertados += pstmt.executeUpdate();
                }

                System.out.println("Se han insertado " + registrosInsertados + " registros en la base de datos.");
            }
        }
    }

    /**
     * Crea la tabla de estudiantes si no existe
     *
     * @param conn Conexión a la base de datos
     * @throws SQLException Si ocurre un error al interactuar con la base de datos
     */
    private void crearTablaEstudiantesSiNoExiste(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS estudiantes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "promedio DOUBLE NOT NULL, " +
                "desviacion_estandar DOUBLE NOT NULL, " +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Muestra los resultados en la consola
     *
     * @param estudiantes Lista de estudiantes con sus estadísticas calculadas
     */
    private void mostrarResultados(List<Estudiante> estudiantes) {
        System.out.println("==== RESULTADOS DEL PROCESAMIENTO AVANZADO ====");
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
}