package app;

import service.ApiService;
import service.PostConAutorService;
import service.UsuarioService;
import util.GsonParser;
import util.JacksonParser;
import util.JsonParser;

import java.io.IOException;
import java.util.Scanner;

/**
 * Clase principal extendida que muestra posts con información de autores.
 * Demuestra la funcionalidad de combinar datos de dos endpoints.
 */
public class MainExtra {

    public static void main(String[] args) {
        System.out.println("=== Lector de JSON con Posts y Autores ===");
        System.out.println("Seleccione la biblioteca para parsear JSON:");
        System.out.println("1. Gson (Google)");
        System.out.println("2. Jackson (FasterXML)");

        Scanner scanner = new Scanner(System.in);
        int parserOption = getOption(scanner, 1, 2);

        // Crear el parser adecuado según la selección
        JsonParser jsonParser;
        if (parserOption == 1) {
            jsonParser = new GsonParser();
            System.out.println("Utilizando Gson para el parsing de JSON");
        } else {
            jsonParser = new JacksonParser();
            System.out.println("Utilizando Jackson para el parsing de JSON");
        }


        // Crear servicios
        ApiService apiService = new ApiService();
        UsuarioService usuarioService = new UsuarioService(apiService, jsonParser);
        PostConAutorService postConAutorService = new PostConAutorService(apiService, jsonParser, usuarioService);

        try {
            // Mostrar menú de opciones
            mostrarMenu();
            int menuOption = getOption(scanner, 1, 4);

            switch (menuOption) {
                case 1:
                    // Mostrar todos los posts con autores
                    System.out.println("\nObteniendo posts con autores desde la API...\n");
                    postConAutorService.displayAllPostsConAutor();
                    break;

                case 2:
                    // Mostrar posts de un autor específico
                    System.out.println("\nMostrar posts de un autor específico:");
                    System.out.print("Ingrese el ID del autor (1-10): ");
                    int autorId = getOption(scanner, 1, 10);
                    postConAutorService.displayPostsByAutor(autorId);
                    break;

                case 3:
                    // Mostrar todos los usuarios
                    System.out.println("\nObteniendo usuarios desde la API...\n");
                    usuarioService.displayAllUsuarios();
                    break;

                case 4:
                    // Mostrar un post específico con autor
                    System.out.println("\nMostrar un post específico:");
                    System.out.print("Ingrese el ID del post (1-100): ");
                    int postId = getOption(scanner, 1, 100);
                    var postConAutor = postConAutorService.getPostConAutorById(postId);
                    System.out.println("\n" + postConAutor);
                    break;
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Muestra el menú de opciones disponibles.
     */
    private static void mostrarMenu() {
        System.out.println("\n=== Menú de Opciones ===");
        System.out.println("1. Mostrar todos los posts con autores");
        System.out.println("2. Mostrar posts de un autor específico");
        System.out.println("3. Mostrar todos los usuarios");
        System.out.println("4. Mostrar un post específico con autor");
    }

    /**
     * Obtiene una opción válida del usuario.
     *
     * @param scanner Scanner para leer la entrada
     * @param min Valor mínimo aceptable
     * @param max Valor máximo aceptable
     * @return Opción seleccionada
     */
    private static int getOption(Scanner scanner, int min, int max) {
        int option = -1;

        while (option < min || option > max) {
            System.out.print("Ingrese su opción (" + min + "-" + max + "): ");

            try {
                option = Integer.parseInt(scanner.nextLine().trim());

                if (option < min || option > max) {
                    System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        return option;
    }
}