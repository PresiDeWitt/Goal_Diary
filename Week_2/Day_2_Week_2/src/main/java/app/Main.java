package app;

import dao.IUsuarioDAO;
import dao.UsuarioDAO;
import etl.ProcesadorEstudiantes;
import etl.ProcesadorEstudiantesAvanzado;
import model.UsuarioDay_2;
import service.UsuarioServicio;

import java.io.File;
import java.util.List;

/**
 * Clase principal que demuestra la implementación de los ejercicios de la Actividad 7
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== ACTIVIDAD 7: EXTRACCIÓN Y LIMPIEZA DE DATOS ===\n");

        // 1. Demostración de limpieza y validación de usuarios
        demostrarLimpiezaUsuarios();

        // 2. Demostración de procesamiento de archivo de estudiantes
        demostrarProcesamientoEstudiantes();

        // 3. Demostración de procesamiento avanzado con persistencia
        demostrarProcesamientoAvanzado();
    }

    /**
     * Demostración de la limpieza y validación de usuarios
     */
    private static void demostrarLimpiezaUsuarios() {
        System.out.println("\n=== 1. LIMPIEZA Y VALIDACIÓN DE USUARIOS ===");

        try {
            // Crear una instancia del DAO y del servicio
            IUsuarioDAO usuarioDAO = new UsuarioDAO();
            UsuarioServicio servicio = new UsuarioServicio(usuarioDAO);

            // Mostrar usuarios originales
            System.out.println("\nUsuarios originales:");
            servicio.mostrarUsuarios();

            // Limpiar y validar usuarios
            System.out.println("\nProceso de limpieza y validación:");
            List<UsuarioDay_2> usuariosLimpios = servicio.limpiarYValidarUsuarios();

            // Mostrar resumen
            System.out.println("\nResumen:");
            System.out.println("Total de usuarios: " + servicio.obtenerUsuarios().size());
            System.out.println("Usuarios válidos después de limpieza: " + usuariosLimpios.size());
        } catch (Exception e) {
            System.err.println("Error en demostrarLimpiezaUsuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demostración del procesamiento de archivo de estudiantes
     */
    private static void demostrarProcesamientoEstudiantes() {
        System.out.println("\n=== 2. PROCESAMIENTO DE DATOS DE ESTUDIANTES ===");

        // Obtenemos la ruta absoluta al directorio de trabajo actual
        String directorioActual = System.getProperty("user.dir");

        // Construimos las rutas absolutas a los archivos
        String archivoEntrada = directorioActual + File.separator + "datos_estudiantes.txt";
        String archivoSalida = directorioActual + File.separator + "resultados_estudiantes.txt";

        System.out.println("Archivo de entrada: " + archivoEntrada);
        System.out.println("Archivo de salida: " + archivoSalida);
        System.out.println();

        try {
            // Verificar si el archivo de entrada existe
            File archivo = new File(archivoEntrada);
            if (!archivo.exists()) {
                System.err.println("ADVERTENCIA: El archivo de entrada no existe: " + archivoEntrada);
                System.err.println("Por favor, cree el archivo 'datos_estudiantes.txt' en: " + directorioActual);
                return;
            }

            ProcesadorEstudiantes.procesarArchivo(archivoEntrada, archivoSalida);
            System.out.println("\nProcesamiento completado exitosamente.");
        } catch (Exception e) {
            System.err.println("\nError durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demostración del procesamiento avanzado con persistencia
     */
    private static void demostrarProcesamientoAvanzado() {
        System.out.println("\n=== 3. PROCESAMIENTO AVANZADO CON PERSISTENCIA ===");

        try {
            // Obtenemos la ruta absoluta al directorio de trabajo actual
            String directorioActual = System.getProperty("user.dir");

            // Construimos las rutas absolutas a los archivos
            String archivoEntrada = directorioActual + File.separator + "datos_estudiantes.txt";
            String archivoSalida = directorioActual + File.separator + "resultados_estudiantes_bd.txt";

            // Verificar si el archivo de entrada existe
            File archivo = new File(archivoEntrada);
            if (!archivo.exists()) {
                System.err.println("ADVERTENCIA: El archivo de entrada no existe: " + archivoEntrada);
                System.err.println("Por favor, cree el archivo 'datos_estudiantes.txt' en: " + directorioActual);
                return;
            }

            // Crear una instancia del procesador avanzado
            ProcesadorEstudiantesAvanzado procesadorAvanzado = new ProcesadorEstudiantesAvanzado();

            // Procesar archivo con persistencia
            System.out.println("\nIniciando procesamiento del archivo con persistencia...");
            procesadorAvanzado.procesarArchivo(archivoEntrada, archivoSalida);

        } catch (Exception e) {
            System.err.println("\nError durante el procesamiento avanzado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}