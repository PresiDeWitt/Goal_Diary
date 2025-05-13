package app;

import dao.IUsuarioDAO;
import dao.UsuarioDAO;
import etl.ProcesadorEstudiantes;
import etl.ProcesadorEstudiantesAvanzado;
import model.Usuario;
import service.UsuarioServicio;

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

        // Crear una instancia del DAO y del servicio
        IUsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioServicio servicio = new UsuarioServicio(usuarioDAO);

        // Mostrar usuarios originales
        System.out.println("\nUsuarios originales:");
        servicio.mostrarUsuarios();

        // Limpiar y validar usuarios
        System.out.println("\nProceso de limpieza y validación:");
        List<Usuario> usuariosLimpios = servicio.limpiarYValidarUsuarios();

        // Mostrar resumen
        System.out.println("\nResumen:");
        System.out.println("Total de usuarios: " + servicio.obtenerUsuarios().size());
        System.out.println("Usuarios válidos después de limpieza: " + usuariosLimpios.size());
    }

    /**
     * Demostración del procesamiento de archivo de estudiantes
     */
    private static void demostrarProcesamientoEstudiantes() {
        System.out.println("\n=== 2. PROCESAMIENTO DE ARCHIVO DE ESTUDIANTES ===");

        // Crear una instancia del procesador
        ProcesadorEstudiantes procesador = new ProcesadorEstudiantes();

        // Procesar archivo
        System.out.println("\nIniciando procesamiento del archivo estudiantes.txt...");
        procesador.procesarArchivo("estudiantes.txt", "resultados_estudiantes.txt");
    }

    /**
     * Demostración del procesamiento avanzado con persistencia
     */
    private static void demostrarProcesamientoAvanzado() {
        System.out.println("\n=== 3. PROCESAMIENTO AVANZADO CON PERSISTENCIA ===");

        // Crear una instancia del procesador avanzado
        ProcesadorEstudiantesAvanzado procesadorAvanzado = new ProcesadorEstudiantesAvanzado();

        // Procesar archivo con persistencia
        System.out.println("\nIniciando procesamiento del archivo con persistencia...");
        procesadorAvanzado.procesarArchivoConPersistencia("estudiantes.txt", "resultados_estudiantes_bd.txt");
    }
}