package app;


import model.Usuario;
import validator.UsuarioValidator;
import validator.UsuarioValidatorDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.UsuarioDAO;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== VALIDACIÓN DE USUARIOS ===");

        // Crear o verificar la tabla
        UsuarioDAO.crearTablaUsuarios();

        // Asegurarse de que la columna fecha_nacimiento existe (opcional)
        UsuarioDAO.agregarColumnaFechaNacimiento();

        // Resto del código...
    }

    private static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Cargar y validar usuarios de prueba");
            System.out.println("2. Validar usuarios existentes en la base de datos");
            System.out.println("3. Salir");
            System.out.print("Opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        cargarUsuariosPrueba();
                        break;
                    case 2:
                        validarUsuariosExistentes();
                        break;
                    case 3:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        } while (opcion != 3);

        scanner.close();
    }

    private static void cargarUsuariosPrueba() {
        // Crear instancia del DAO
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Crear la tabla de usuarios si no existe
        boolean tablaCreada = UsuarioDAO.crearTablaUsuarios();
        if (tablaCreada) {
            System.out.println("Tabla de usuarios disponible para su uso");
        } else {
            System.out.println("Error al verificar la tabla de usuarios");
            return; // Salir si no se puede crear la tabla
        }

        // Crear lista de usuarios de prueba con fechas de nacimiento
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario(1, "Juan", "juan@example.com",
                UsuarioValidator.parseFecha("1990-05-15")));
        usuarios.add(new Usuario(2, "María", "maria@example.com",
                UsuarioValidator.parseFecha("2010-08-20")));
        usuarios.add(new Usuario(3, "Pedro", "pedro@malformado",
                UsuarioValidator.parseFecha("1985-11-30")));
        usuarios.add(new Usuario(4, "Ana", "ana@example.com",
                UsuarioValidator.parseFecha("2005-03-10")));
        usuarios.add(new Usuario(5, "Carlos", "carlos@example.com",
                UsuarioValidator.parseFecha("1995-12-25")));

        System.out.println("\n=== VALIDACIÓN Y ALMACENAMIENTO DE USUARIOS ===");
        List<Usuario> usuariosValidos = new ArrayList<>();
        List<Usuario> usuariosRechazados = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            // Intentar guardar el usuario (incluye validación)
            if (usuarioDAO.guardarUsuario(usuario)) {
                usuariosValidos.add(usuario);
                System.out.println("Usuario " + usuario.getNombre() + " guardado correctamente en la base de datos");
            } else {
                usuariosRechazados.add(usuario);
            }
        }

        System.out.println("\n=== USUARIOS EN BASE DE DATOS ===");
        List<Usuario> usuariosEnBD = usuarioDAO.obtenerTodosLosUsuarios();
        for (Usuario u : usuariosEnBD) {
            System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombre() +
                    ", Email: " + u.getEmail() + ", Edad: " +
                    UsuarioValidator.calcularEdad(u.getFechaNacimiento()) + " años");
        }

        System.out.println("\n=== RESUMEN ===");
        System.out.println("Total usuarios procesados: " + usuarios.size());
        System.out.println("Usuarios válidos y guardados: " + usuariosValidos.size());
        System.out.println("Usuarios rechazados: " + usuariosRechazados.size());
    }

    private static void validarUsuariosExistentes() {
        System.out.println("\n=== VALIDACIÓN DE USUARIOS EXISTENTES EN LA BASE DE DATOS ===");

        UsuarioValidatorDB validador = new UsuarioValidatorDB();
        validador.generarReporteValidacion();
    }

}