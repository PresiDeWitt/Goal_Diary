package app;

import model.UsuarioDay_2;
import validator.UsuarioValidator;
import validator.UsuarioValidatorDB;
import dao.UsuarioDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== VALIDACIÓN DE USUARIOS ===");

        // Mostrar menú de opciones
        mostrarMenu();
    }

    private static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Cargar y validar usuarios");
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
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        boolean tablaCreada = UsuarioDAO.crearTablaUsuarios();
        if (!tablaCreada) {
            System.out.println("Error al verificar la tabla de usuarios");
            return;
        }

        //Array para guardar usuarios en la base de datos
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        usuarios.add(new UsuarioDay_2(1, "Juan", "juan@example.com",
                UsuarioValidator.parseFecha("1990-05-15")));
        usuarios.add(new UsuarioDay_2(2, "María", "maria@example.com",
                UsuarioValidator.parseFecha("2010-08-20")));
        usuarios.add(new UsuarioDay_2(3, "Pedro", "pedro@malformado",
                UsuarioValidator.parseFecha("1985-11-30")));
        usuarios.add(new UsuarioDay_2(4, "Ana", "ana@example.com",
                UsuarioValidator.parseFecha("2005-03-10")));
        usuarios.add(new UsuarioDay_2(5, "Carlos", "carlos@example.com",
                UsuarioValidator.parseFecha("1995-12-25")));

        System.out.println("\n=== VALIDACIÓN Y ALMACENAMIENTO DE USUARIOS ===");

        // Almacenamos todos los usuarios en la base de datos, válidos o no
        for (UsuarioDay_2 usuario : usuarios) {
            // Validar el usuario (solo para mostrar información)
            boolean esValido = UsuarioValidator.validarUsuario(usuario);

            // Guardar el usuario sin importar si es válido o no
            boolean guardado = usuarioDAO.guardarUsuarioSinValidar(usuario);

            if (guardado) {
                System.out.println("Usuario " + usuario.getNombre() + " guardado en la base de datos" +
                        (esValido ? " (válido)" : " (inválido)"));
            } else {
                System.out.println("Error al guardar usuario " + usuario.getNombre() + " en la base de datos");
            }
        }

        System.out.println("\n=== RESUMEN ===");
        System.out.println("Total usuarios procesados y guardados: " + usuarios.size());
    }

    private static void validarUsuariosExistentes() {
        System.out.println("\n=== VALIDACIÓN DE USUARIOS EXISTENTES EN LA BASE DE DATOS ===");

        UsuarioValidatorDB validador = new UsuarioValidatorDB();
        validador.generarReporteValidacion();
    }
}